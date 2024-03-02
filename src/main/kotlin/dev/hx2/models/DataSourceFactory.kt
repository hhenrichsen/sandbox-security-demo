package dev.hx2.models

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DataSourceFactory {
    fun init(environment: ApplicationEnvironment) {
        Database.connect(configureHikari(environment))

        transaction {
            SchemaUtils.createMissingTablesAndColumns(UserService.Users)
        }
    }

    private fun configureHikari(environment: ApplicationEnvironment): HikariDataSource {
        Class.forName("org.postgresql.Driver")
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()
        val config = HikariConfig()
        config.username = user
        config.password = password
        config.jdbcUrl = "jdbc:$url"
        config.driverClassName = "org.postgresql.Driver"
        config.validate()
        return HikariDataSource(config)
    }
}