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
            call.respond(HttpStatusCode.Unauthorized)
            return
        }
        return block(user)
    }

    suspend fun withAuthOrNull(
        call: ApplicationCall,
        block: suspend (auth: ExposedUser?) -> Unit
    ) {
        println(call.request.cookies.rawCookies)
        println(call.request.cookies["token"])
        val auth = call.request.cookies["token"] ?: return block(null)
        val user = userService.getUserByToken(auth)
        println(user)
        return block(user)
    }
}