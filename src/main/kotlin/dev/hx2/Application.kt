package dev.hx2

import dev.hx2.core.coreModule
import dev.hx2.models.DataSourceFactory
import dev.hx2.models.modelsModule
import dev.hx2.plugins.configureHTTP
import dev.hx2.plugins.configureMonitoring
import dev.hx2.plugins.configureRouting
import dev.hx2.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.koin

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    koin {
        modules(coreModule, modelsModule)
    }
    DataSourceFactory.init(environment)
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
}

