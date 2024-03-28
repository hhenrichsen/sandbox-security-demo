package dev.hx2.services

import dev.hx2.models.ExposedUser
import dev.hx2.models.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

class Authentication(private val userService: UserService) {
    suspend fun withAuth(
        call: ApplicationCall,
        block: suspend (auth: ExposedUser) -> Unit
    ) {
        val auth = call.request.cookies["token"]
        if (auth == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return
        }
        val user = userService.getUserByToken(auth)
        if (user == null) {
            call.response.headers.append("Set-Cookie", "token=; Path=/; HttpOnly; SameSite=Strict")
            call.respond(HttpStatusCode.Unauthorized)
            return
        }
        return block(user)
    }

    suspend fun withAuthOrNull(
        call: ApplicationCall,
        block: suspend (auth: ExposedUser?) -> Unit
    ) {
        val auth = call.request.cookies["token"] ?: return block(null)
        val user = userService.getUserByToken(auth)
        if (user == null) {
            call.response.headers.append("Set-Cookie", "token=; Path=/; HttpOnly; SameSite=Strict")
        }
        return block(user)
    }
}