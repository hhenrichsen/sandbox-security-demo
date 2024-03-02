package dev.hx2.models

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class CreatePost(val content: String, val title: String, val slug: String)

@Serializable
data class Post(val content: String, val title: String, val slug: String, val html: String)
class PostService {
    object Posts : Table() {
        val id = integer("id").autoIncrement()
        val slug = varchar("slug", length = 50)
        val title = text("title")
        val content = text("content")
        val html = text("html")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction {
            SchemaUtils.create(Posts)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(post: Post): Int = dbQuery {
        Posts.insert {
            it[content] = post.content
            it[slug] = post.slug
            it[html] = post.html
            it[title] = post.title
        }[Posts.id]
    }

    suspend fun read(id: Int): Post? {
        return dbQuery {
            Posts.select { Posts.id eq id }
                .map { Post(it[Posts.content], it[Posts.title], it[Posts.slug], it[Posts.html]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, post: Post) {
        dbQuery {
            Posts.update({ Posts.id eq id }) {
                it[content] = post.content
                it[slug] = post.slug
                it[html] = post.html
                it[title] = post.title
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Posts.deleteWhere { Posts.id.eq(id) }
        }
    }

    suspend fun getBySlug(slug: String): Post? {
        return dbQuery {
            Posts.select { Posts.slug eq slug }
                .map { Post(it[Posts.content], it[Posts.title], it[Posts.slug], it[Posts.html]) }
                .singleOrNull()
        }
    }
}