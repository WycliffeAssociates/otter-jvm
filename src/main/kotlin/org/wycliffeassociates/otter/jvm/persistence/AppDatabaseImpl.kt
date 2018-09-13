package org.wycliffeassociates.otter.jvm.persistence

import org.wycliffeassociates.otter.common.data.dao.LanguageDao
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.sqlite.SQLiteDataSource
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repo.LanguageRepo
import java.io.File
import java.nio.file.FileSystems

object AppDatabaseImpl {
    private val config: Configuration
    // changed names to repo to distinguish our DAOS from generated
    private val languageRepo: LanguageDao
    private val languageMapper: LanguageMapper

    init {
        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:${DirectoryProvider("8woc2018")
                .getAppDataDirectory()}${FileSystems.getDefault().separator}content.sqlite"
        sqLiteDataSource.config.toProperties().setProperty("foreign_keys", "true")
        config = DSL.using(sqLiteDataSource, SQLDialect.SQLITE).configuration()
        val file = File("src${File.separator}main${File.separator}Resources${File.separator}createAppDb.sql")
        var sql = StringBuffer()
        file.forEachLine {
            sql.append(it)
            if (it.contains(";")) {
                config.dsl().fetch(sql.toString())
                sql.delete(0, sql.length)
            }
        }

        languageMapper = LanguageMapper()
        languageRepo = LanguageRepo(config, languageMapper)
    }
    fun getLanguageDao() = languageRepo
}