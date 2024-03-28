package dev.hx2.plugins

import dev.hx2.core.MarkdownService
import dev.hx2.models.*
import dev.hx2.pages.*
import dev.hx2.services.Authentication
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

suspend fun <R> PipelineContext<*, ApplicationCall>.withContent(contentType: String, block: suspend () -> R?): R? {
    if (call.request.headers["Accept"]?.contains(contentType) != true) {
        return null
    }
    return block()
}

suspend fun <R> PipelineContext<*, ApplicationCall>.withErrorHandling(block: suspend () -> R?): R? {
    return try {
        block()
    } catch (ex: Exception) {
        when (ex) {
            is BadRequestException,
            is IllegalArgumentException,
            is CannotTransformContentToTypeException -> {
                call.respond(HttpStatusCode.BadRequest)
                error(ex)
            }

            else -> {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "Unknown error")
                error(ex)
            }
        }
    }
}

suspend fun <R> PipelineContext<*, ApplicationCall>.withGroup(
    groupService: GroupService,
    key: String = "group",
    block: suspend (group: ExposedGroupWithoutMembers) -> R?
): R? {
    val slug = call.parameters[key]
    if (slug == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid slug")
        return null
    }
    val group = groupService.readBySlug(slug)
    if (group == null) {
        call.respond(HttpStatusCode.NotFound, "Group not found")
        return null
    }
    return block(group)
}

suspend fun <R> PipelineContext<*, ApplicationCall>.withFullGroup(
    groupService: GroupService,
    key: String = "group",
    block: suspend (group: ExposedGroup) -> R?
): R? {
    val slug = call.parameters[key]
    if (slug == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid slug")
        return null
    }
    val group = groupService.readFullBySlug(slug)
    if (group == null) {
        call.respond(HttpStatusCode.NotFound, "Group not found")
        return null
    }
    return block(group)
}

suspend fun <R> PipelineContext<*, ApplicationCall>.withNote(
    groupService: GroupService,
    noteService: NoteService,
    groupKey: String = "group",
    block: suspend (group: ExposedGroup, note: ExposedNote) -> R?
): R? {
    return withFullGroup<R>(groupService, groupKey) { group ->
        val noteId = call.parameters["id"]?.toIntOrNull()
        if (noteId == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@withFullGroup null
        }
        val note = noteService.read(noteId)
        if (note == null) {
            call.respond(HttpStatusCode.NotFound)
            return@withFullGroup null
        }
        if (group.id != note.owner.id) {
            call.respond(HttpStatusCode.NotFound)
            return@withFullGroup null
        }
        return@withFullGroup block(group, note)
    }
}

@Serializable
data class InviteUser(val username: String, val group: String)

fun Application.configureRouting() {
    val markdownService: MarkdownService by inject()
    val userService: UserService by inject()
    val noteService: NoteService by inject()
    val groupService: GroupService by inject()
    val authentication: Authentication by inject()

    routing {
        post("/api/register") {
            withErrorHandling {
                val user = call.receive<CreateUser>()
                if (userService.getByUsername(user.username) != null) {
                    call.respond(HttpStatusCode.Conflict)
                    return@withErrorHandling
                }
                val (id, token) = userService.create(user)
                call.response.headers.append("Set-Cookie", "token=$token; HttpOnly; SameSite=Strict; Path=/;")
                call.response.headers.append("HX-Redirect", "/")
                call.respond(HttpStatusCode.Created)
            }
        }
        get("/api/me") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    call.respond(HttpStatusCode.OK, auth)
                }
            }
        }
        post("/api/groups") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    val group = call.receive<CreateGroup>()
                    val id = groupService.create(
                        group.slug?.lowercase()?.replace(Regex("\\W"), "-") ?: group.title.replace(
                            Regex("\\W"),
                            "-"
                        ),
                        group.title,
                        auth.id
                    )
                    call.response.headers.append("HX-Redirect", "/")
                    call.respond(HttpStatusCode.Created, id)
                }
            }
        }
        get("/api/groups") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    call.respond(HttpStatusCode.OK, groupService.getByUser(auth.id))
                }
            }
        }
        get("/api/groups/{id}") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    withGroup(groupService) { group ->
                        call.respond(HttpStatusCode.OK, group)
                    }
                }
            }
        }
        post("/api/groups/invite") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    val invite = call.receive<InviteUser>()
                    val group = groupService.read(
                        invite.group.toIntOrNull() ?: return@withAuth call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid group ID format"
                        )
                    )
                    if (group == null) {
                        call.respond(HttpStatusCode.NotFound, "Group not found")
                        return@withAuth
                    }
                    if (group.admin != auth.id) {
                        call.respond(HttpStatusCode.Forbidden, "You are not the admin of this group")
                        return@withAuth
                    }
                    val user = userService.getByUsername(invite.username)
                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                        return@withAuth
                    }
                    call.response.headers.append("HX-Redirect", "/")
                    groupService.inviteUser(group, user)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        patch("/api/groups/{id}") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    val group = call.receive<ExposedGroupWithoutMembers>()
                    withGroup(groupService) { existing ->
                        if (existing.admin != auth.id) {
                            call.respond(HttpStatusCode.Forbidden)
                            return@withGroup
                        }
                        groupService.update(group)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
        get("/api/groups/{group}/notes") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    withFullGroup(groupService) { group ->
                        if (!group.members.any { it.id == auth.id }) {
                            call.respond(HttpStatusCode.Forbidden)
                            return@withFullGroup
                        }
                        val notes = noteService.getByGroup(group.id)
                        withContent(ContentType.Application.Json.toString()) {
                            call.respond(HttpStatusCode.OK, notes)
                        }
                        println(notes)
                        withContent(ContentType.Text.Html.toString()) {
                            call.respondHtml {
                                body {
                                    div {
                                        if (notes.isEmpty()) {
                                            p { +"No notes found" }
                                        } else {
                                            notes.map {
                                                a("/notes/${group.slug}/${it.id}") {
                                                    +it.title
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        post("/api/notes") {
            withErrorHandling {
                authentication.withAuth(call) { auth ->
                    val note = call.receive<CreateNote>()
                    val group = note.group.toIntOrNull()?.let { groupService.readFull(it) } ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@withAuth
                    }
                    val id = noteService.create(note)
                    call.response.headers.append("HX-Redirect", "/notes/${group.slug}/$id")
                    call.respond(HttpStatusCode.Created, "/notes/${group.slug}/$id")
                }
            }
        }
        get("/api/health") {
            withErrorHandling {
                val query = call.request.queryParameters["query"] ?: "SELECT 1;"
                if (query.contains("user")) {
                    call.respond(
                        HttpStatusCode.fromValue(418),
                        "For the purposes of this, I'm going to keep other users' tokens private ;), good find!"
                    )
                }
                newSuspendedTransaction() {
                    val transaction = TransactionManager.currentOrNull()
                        ?: run {
                            call.respond(HttpStatusCode.InternalServerError);
                            return@newSuspendedTransaction
                        }
                    val resultMap = mutableListOf<Map<String, String>>()
                    transaction(0, 0, true) {
                        val statement = transaction.connection.prepareStatement(query, true)
                        val result = statement.executeQuery()
                        while (result.next()) {
                            val rowMap = mutableMapOf<String, String>()
                            for (i in 1..result.metaData.columnCount) {
                                rowMap[result.metaData.getColumnName(i)] = result.getObject(i).toString()
                            }
                            resultMap.add(rowMap)
                        }
                    }
                    call.respond(HttpStatusCode.OK, resultMap)
                }
            }
        }
        get("/notes/{group}/{id}") {
            withErrorHandling {
                withGroup(groupService, "group") { group ->
                    val noteId = call.parameters["id"]?.toIntOrNull()
                    if (noteId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@withGroup
                    }
                    val note = noteService.read(noteId)
                    if (note == null) {
                        call.respond(HttpStatusCode.NotFound)
                        return@withGroup
                    }
                    call.respondHtml {
                        postView(note, markdownService.parse(note.content))
                    }
                }
            }
        }
        get("/") {
            authentication.withAuthOrNull(call) { auth ->
                println(auth)
                val groups = auth?.let { groupService.getByUser(it.id) } ?: emptyList()
                call.respondHtml {
                    index(auth, groups)
                }
            }
        }
        get("/register") {
            authentication.withAuthOrNull(call) {
                if (it != null) {
                    call.response.headers.append("HX-Redirect", "/")
                    call.respondRedirect("/")
                }
                call.respondHtml {
                    register()
                }
            }
        }
        get("/notes/new") {
            authentication.withAuth(call) { auth ->
                val groups = groupService.getByUser(auth.id)
                call.respondHtml {
                    createNote(groups, auth)
                }
            }
            call.respond(HttpStatusCode.Unauthorized)
        }
        get("/notes/mine") {
            authentication.withAuth(call) { auth ->
                val groups = groupService.getByUser(auth.id)
                val notes = groups.flatMap {
                    noteService.getByGroup(it.id)
                }.sortedBy { it.created }
                call.respondHtml {
                    myNotes(notes, auth)
                }
            }
        }
        get("/groups/new") {
            authentication.withAuth(call) { auth ->
                call.respondHtml {
                    createGroup(auth)
                }
            }
        }
        get("/groups/invite") {
            authentication.withAuth(call) { auth ->
                val groups = groupService.getByUser(auth.id, requireAdmin = true)
                val usernames = userService.readAllUsernames()
                call.respondHtml {
                    inviteUser(groups, usernames, auth)
                }
            }
        }
    }
}
