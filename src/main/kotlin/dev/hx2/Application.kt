package dev.hx2

import dev.hx2.core.coreModule
import dev.hx2.models.DataSourceFactory
import dev.hx2.models.modelsModule
import dev.hx2.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.koin

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    koin {
        modules(coreModule, modelsModule)
    }
    DataSourceFactory.init(environment)
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureTemplating()
    configureDatabases()
    configureSockets()
    configureRouting()
}

