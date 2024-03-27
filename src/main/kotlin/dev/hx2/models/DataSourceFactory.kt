package dev.hx2.models

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DataSourceFactory {
    fun init(dotenv: Dotenv) {
        Database.connect(configureHikari(dotenv))

        transaction {
            SchemaUtils.createMissingTablesAndColumns(UserService.Users)
        }
    }

    private fun configureHikari(dotenv: Dotenv): HikariDataSource {
        Class.forName("org.postgresql.Driver")
        val url = dotenv["POSTGRES_URL"]
        val user = dotenv["POSTGRES_USER"]
        val password = dotenv["POSTGRES_PASSWORD"]
        val config = HikariConfig()
        config.username = user
        config.password = password
        config.jdbcUrl = "jdbc:$url"
        config.driverClassName = "org.postgresql.Driver"
        config.validate()
        return HikariDataSource(config)
    }
}