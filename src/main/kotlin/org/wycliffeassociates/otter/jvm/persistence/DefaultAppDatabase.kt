package org.wycliffeassociates.otter.jvm.persistence

import jooq.tables.daos.*
import org.intellij.lang.annotations.Language
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.sqlite.SQLiteDataSource
import org.wycliffeassociates.otter.jvm.persistence.mapping.*
import org.wycliffeassociates.otter.jvm.persistence.repo.*
import java.io.File
import java.nio.file.FileSystems

object DefaultAppDatabase {
    private val config: Configuration
    // changed names to repo to distinguish our DAOS from generated

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
    }

    val languageDao = LanguageDao(LanguageEntityDao(config), LanguageMapper())
    val resourceContainerDao = ResourceContainerDao(
            DublinCoreEntityDao(config),
            RcLinkEntityDao(config),
            ResourceContainerMapper(languageDao)
    )
    val collectionDao = CollectionDao(CollectionEntityDao(config), CollectionMapper(resourceContainerDao))
    val takeDao = TakeDao(TakeEntityDao(config), TakeMapper())
    val chunkDao = ChunkDao(ContentEntityDao(config), ChunkMapper(takeDao))
}