package dev.hx2.models

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


@Serializable
data class CreateGroup(val title: String, val slug: String? = title.replace(Regex("\\W"), "-"))

interface ExposedGroupLike {
    val id: Int
    val title: String
    val slug: String
}

@Serializable
data class ExposedGroup(
    override val id: Int,
    override val title: String,
    val admin: ExposedUser,
    override val slug: String,
    val members: List<ExposedUser>
) : ExposedGroupLike

@Serializable
data class ExposedGroupWithoutMembers(
    override val id: Int,
    override val title: String,
    val admin: Int,
    override val slug: String
) : ExposedGroupLike

class GroupService(val userService: UserService) {
    object Groups : Table() {
        val id = integer("id").autoIncrement()
        val admin = reference("admin", UserService.Users.id)
        val slug = varchar("slug", length = 50).uniqueIndex()
        val title = varchar("title", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    object GroupMembership : Table() {
        val group = reference("group", Groups.id)
        val user = reference("user", UserService.Users.id)

        override val primaryKey = PrimaryKey(group, user)
    }

    init {
        transaction {
            SchemaUtils.create(Groups)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(slug: String, title: String, adminId: Int): Int = dbQuery {
        Groups.insert {
            it[this.title] = title
            it[this.admin] = adminId
            it[this.slug] = slug
        }[Groups.id]
    }


    suspend fun create(group: ExposedGroupLike): Int = dbQuery {
        Groups.insert {
            it[admin] = group.id
            it[title] = group.title
            it[slug] = group.slug
        }[Groups.id]
    }

    suspend fun getFullByUser(userId: Int): List<ExposedGroup> {
        return dbQuery {
            Groups.select { GroupMembership.user eq userId }.map { parseFullGroup(it) } +
                    Groups.select { Groups.admin eq userId }.map { parseFullGroup(it) }
        }
    }

    suspend fun getByUser(userId: Int, requireAdmin: Boolean = false): List<ExposedGroupWithoutMembers> {
        return dbQuery {
            if (requireAdmin) {
                Groups.select { Groups.admin eq userId }.map { parseGroup(it) }
            } else {
                GroupMembership.select { GroupMembership.user eq userId }.mapNotNull { result ->
                    read(result[GroupMembership.group])
                } + Groups.select { Groups.admin eq userId }.map { parseGroup(it) }
            }
        }
    }


    private fun parseGroup(result: ResultRow): ExposedGroupWithoutMembers {
        return ExposedGroupWithoutMembers(
            result[Groups.id],
            result[Groups.title],
            result[Groups.admin],
            result[Groups.slug]
        )
    }

    private suspend fun parseFullGroup(result: ResultRow): ExposedGroup {
        val owner = (userService.read(result[Groups.admin])?.let { listOf(it) } ?: emptyList())
        val members = GroupMembership.select {
            GroupMembership.group eq result[Groups.id]
        }.mapNotNull { membership ->
            userService.read(membership[GroupMembership.user])
        } + owner

        return ExposedGroup(
            result[Groups.id],
            result[Groups.title],
            userService.read(result[Groups.admin])!!,
            result[Groups.slug],
            members
        )
    }

    suspend fun inviteUser(group: ExposedGroupLike, user: ExposedUser) {
        dbQuery {
            GroupMembership.insertIgnore {
                it[GroupMembership.group] = group.id
                it[GroupMembership.user] = user.id
            }
        }
    }

    suspend fun readFull(id: Int): ExposedGroup? {
        return dbQuery {
            Groups.select { Groups.id eq id }
                .map { parseFullGroup(it) }
                .singleOrNull()
        }
    }

    suspend fun read(id: Int): ExposedGroupWithoutMembers? {
        return dbQuery {
            Groups.select { Groups.id eq id }
                .map { parseGroup(it) }
                .singleOrNull()
        }
    }

    suspend fun readBySlug(slug: String): ExposedGroupWithoutMembers? {
        return dbQuery {
            Groups.select { Groups.slug eq slug }
                .map { parseGroup(it) }
                .singleOrNull()
        }
    }

    suspend fun readFullBySlug(slug: String): ExposedGroup? {
        return dbQuery {
            Groups.select { Groups.slug eq slug }
                .map { parseFullGroup(it) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, group: ExposedGroup) {
        dbQuery {
            Groups.update({ Groups.id eq id }) {
                it[admin] = group.admin.id
                it[slug] = group.slug
            }
            group.members.forEach { user ->
                GroupMembership.insertIgnore {
                    it[GroupMembership.group] = id
                    it[GroupMembership.user] = user.id
                }
            }
        }
    }

    suspend fun update(group: ExposedGroupWithoutMembers) {
        dbQuery {
            Groups.update({ Groups.id eq group.id }) {
                it[admin] = group.admin
                it[slug] = group.slug
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Groups.deleteWhere { Groups.id eq id }
        }
    }
}


