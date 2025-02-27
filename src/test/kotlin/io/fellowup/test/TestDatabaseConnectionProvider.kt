package io.fellowup.test

import org.jetbrains.exposed.sql.Database

internal object TestDatabaseConnectionProvider {

    fun provide(): Database {
        return Database.connect(
        url = "jdbc:postgresql://localhost:5432/app_fellow_up?serverTimezone=UTC",
        user = "app_fellow_up",
            password = "app_fellow_up"
        )
    }
}
