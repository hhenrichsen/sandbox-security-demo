package dev.hx2

import dev.hx2.core.coreModule
import dev.hx2.models.DataSourceFactory
import dev.hx2.models.modelsModule
import dev.hx2.plugins.configureHTTP
import dev.hx2.plugins.configureMonitoring
import dev.hx2.plugins.configureRouting
import dev.hx2.plugins.configureSerialization
import dev.hx2.services.serviceModule
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.koin

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        port = 8080,
        watchPaths = listOf("classes"),
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    koin {
        modules(coreModule, modelsModule, serviceModule)
    }
    val dotenv = dotenv {
        directory = "src/main/resources"
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }
    DataSourceFactory.init(dotenv)
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
}

