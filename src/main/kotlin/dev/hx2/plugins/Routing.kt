package dev.hx2.plugins

import dev.hx2.core.HtmlSanitizerService
import dev.hx2.core.MarkdownService
import dev.hx2.models.*
import dev.hx2.pages.index
import dev.hx2.pages.postView
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val sanitizer: HtmlSanitizerService by inject()
    val markdownService: MarkdownService by inject()
    val postService: PostService by inject()
    val userService: UserService by inject()

    routing {
        post("/api/users") {
            val user = call.receive<ExposedUser>()
            val id = userService.create(user)
            call.respond(HttpStatusCode.Created, id)
        }
        get("/api/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = userService.read(id)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        put("/api/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<ExposedUser>()
            userService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }
        delete("/api/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            userService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
        post("/api/post") {
            val post = call.receive<CreatePost>()
            val id = postService.create(Post(post.content, post.title, post.slug, markdownService.parse(post.content)))
            call.respond(HttpStatusCode.Created, id)
        }
        get("/") {
            call.respondHtml {
                index()
            }
        }
        get("/{slug}") {
            val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.NotFound)
            val post = postService.getBySlug(slug) ?: return@get call.respond(HttpStatusCode.NotFound)
            val body = sanitizer.sanitize(post.html)
            call.respondHtml {
                postView(post.title, body)
            }
        }
    }
}
