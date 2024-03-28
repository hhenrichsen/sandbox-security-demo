package dev.hx2.models

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.*
import kotlinx.datetime.Clock.System.now
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class CreateNote(
    val title: String,
    val content: String,
    val group: String
)

@Serializable()
data class ExposedNote(
    val id: Int,
    val title: String,
    val content: String,
    val created: LocalDateTime,
    val owner: ExposedGroupWithoutMembers
)

class NoteService(private val groupService: GroupService) {
    object Notes : Table() {
        val id = integer("id").autoIncrement()
        val title = varchar("title", length = 50)
        val content = text("content")
        val owner = reference("owner", GroupService.Groups.id)
        val created = datetime("created").default(now().toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime())

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction {
            SchemaUtils.create(Notes)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(note: CreateNote): Int = dbQuery {
        Notes.insert {
            it[title] = note.title
            it[content] = note.content
            it[owner] = note.group.toIntOrNull() ?: throw IllegalArgumentException("Invalid owner id")
        }[Notes.id]
    }

    suspend fun read(id: Int): ExposedNote? = dbQuery {
        Notes.select { Notes.id eq id }
            .mapNotNull { result ->
                ExposedNote(
                    result[Notes.id],
                    result[Notes.title],
                    result[Notes.content],
                    result[Notes.created].toKotlinLocalDateTime(),
                    groupService.read(result[Notes.owner]) ?: return@mapNotNull null
                )
            }.singleOrNull()
    }

    suspend fun getByGroup(groupId: Int): List<ExposedNote> = dbQuery {
        val group = groupService.read(groupId) ?: return@dbQuery emptyList()
        Notes.select { Notes.owner eq groupId }
            .mapNotNull { result ->
                ExposedNote(
                    result[Notes.id],
                    result[Notes.title],
                    result[Notes.content],
                    result[Notes.created].toKotlinLocalDateTime(),
                    group,
                )
            }
    }

    suspend fun readAll(): List<ExposedNote> = dbQuery {
        Notes.selectAll()
            .mapNotNull { result ->
                ExposedNote(
                    result[Notes.id],
                    result[Notes.title],
                    result[Notes.content],
                    result[Notes.created].toKotlinLocalDateTime(),
                    groupService.read(result[Notes.owner]) ?: return@mapNotNull null
                )
            }
    }

    suspend fun update(note: ExposedNote) = dbQuery {
        Notes.update({ Notes.id eq note.id }) {
            it[title] = note.title
            it[content] = note.content
            it[owner] = note.owner.id
        }
    }

    suspend fun delete(id: Int) = dbQuery {
        Notes.deleteWhere { Notes.id eq id }
    }
}



