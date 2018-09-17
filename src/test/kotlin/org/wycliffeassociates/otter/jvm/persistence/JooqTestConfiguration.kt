package org.wycliffeassociates.otter.jvm.persistence

import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.sqlite.SQLiteDataSource
import java.io.File

object JooqTestConfiguration {
    fun setup(databasePath: String): Configuration {
        Class.forName("org.sqlite.JDBC")
        println("Creating $databasePath")
        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:$databasePath"
        sqLiteDataSource.config.toProperties().setProperty("foreign_keys", "true")
        val config = DSL.using(sqLiteDataSource, SQLDialect.SQLITE).configuration()
        val file = File(listOf("src", "main", "resources", "createAppDb.sql").joinToString(File.separator))
        val sql = StringBuffer()
        file.forEachLine {
            sql.append(it)
            if (it.contains(";")) {
                config.dsl().fetch(sql.toString())
                sql.delete(0, sql.length)
            }
        }
        return config
    }

    fun tearDown(databasePath: String) {
        // delete existing database
        val dbFile = File(databasePath)
        if (dbFile.exists()) {
            println("Deleting $databasePath")
            dbFile.delete()
        }
    }
}