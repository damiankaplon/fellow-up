package io.fellowup.domain.db

import javax.sql.DataSource

fun interface DataSourceProvider {
    @Throws(IllegalStateException::class)
    fun provide(): DataSource
}
