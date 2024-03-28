package dev.hx2.models

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


val tokenCharacters = listOf(
    ('a'..'z'),
    ('A'..'Z'),
    ('0'..'9')
).flatten()

@Serializable
data class CreateUser(
    val username: String
)

@Serializable
data class ExposedUser(
    val id: Int,
    val username: String,
    val token: String,
    val lastNotification: LocalDateTime? = null
)

class UserService() {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val username = varchar("username", length = 50).uniqueIndex()
        val token =
            varchar("token", length = 50).default((1..50).map {
                tokenCharacters.random()
            }.joinToString(""))
        val lastNotification = datetime("last_notification").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction {
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: CreateUser): Pair<Int, String> = dbQuery {
        val res = Users.insert {
            it[username] = user.username
        }
        Pair(res[Users.id], res[Users.token])
    }

    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map {
                    ExposedUser(
                        it[Users.id],
                        it[Users.username],
                        it[Users.token],
                        it[Users.lastNotification]?.toKotlinLocalDateTime()
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun readAllUsernames(): List<String> {
        return dbQuery {
            Users.selectAll().map { it[Users.username] }
        }
    }

    suspend fun getByUsername(username: String): ExposedUser? {
        return dbQuery {
            Users.select { Users.username eq username }
                .map {
                    ExposedUser(
                        it[Users.id],
                        it[Users.username],
                        it[Users.token],
                        it[Users.lastNotification]?.toKotlinLocalDateTime()
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun getUserByToken(token: String): ExposedUser? {
        return dbQuery {
            Users.select { Users.token eq token }
                .map {
                    ExposedUser(
                        it[Users.id],
                        it[Users.username],
                        it[Users.token],
                        it[Users.lastNotification]?.toKotlinLocalDateTime()
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: ExposedUser) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[username] = user.username
                it[token] = user.token
                it[lastNotification] = user.lastNotification?.toJavaLocalDateTime()
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}
