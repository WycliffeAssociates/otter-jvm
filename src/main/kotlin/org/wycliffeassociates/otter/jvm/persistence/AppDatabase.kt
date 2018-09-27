package org.wycliffeassociates.otter.jvm.persistence

import jooq.tables.daos.*
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.sqlite.SQLiteDataSource
import org.wycliffeassociates.otter.jvm.persistence.mapping.*
import org.wycliffeassociates.otter.jvm.persistence.repo.*
import java.io.File
import java.nio.file.FileSystems

object AppDatabase {
    private val config: Configuration

    init {
        // delete an old database
        val dbFile = File("${DirectoryProvider("8woc2018").getAppDataDirectory()}${ FileSystems.getDefault().separator}content.sqlite")
        dbFile.delete()

        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:${DirectoryProvider("8woc2018")
                .getAppDataDirectory()}${FileSystems.getDefault().separator}content.sqlite"
        sqLiteDataSource.config.toProperties().setProperty("foreign_keys", "true")
        config = DSL.using(sqLiteDataSource, SQLDialect.SQLITE).configuration()
        val file = File("src${File.separator}main${File.separator}Resources${File.separator}CreateAppDb.sql")
        var sql = StringBuffer()
        file.forEachLine {
            sql.append(it)
            if (it.contains(";")) {
                config.dsl().fetch(sql.toString())
                sql.delete(0, sql.length)
            }
        }
    }

    private val languageDao = LanguageDao(LanguageEntityDao(config), LanguageMapper())
    private val resourceContainerDao = ResourceMetadataDao(
            DublinCoreEntityDao(config),
            RcLinkEntityDao(config),
            ResourceMetadataMapper(languageDao)
    )
    private val markerDao = MarkerDao(MarkerEntityDao(config), MarkerMapper())
    private val takeDao = TakeDao(TakeEntityDao(config), markerDao, TakeMapper(markerDao))
    private val chunkMapper = ChunkMapper(takeDao, ContentDerivativeDao(config), ContentEntityDao(config))
    private val collectionDao = CollectionDao(
            CollectionEntityDao(config),
            ResourceLinkDao(config),
            ContentEntityDao(config),
            CollectionMapper(resourceContainerDao),
            chunkMapper
    )
    private val chunkDao = ChunkDao(ContentEntityDao(config), ContentDerivativeDao(config), ResourceLinkDao(config), chunkMapper)

    fun getLanguageDao(): LanguageDao = languageDao
    fun getResourceMetadataDao(): ResourceMetadataDao = resourceContainerDao
    fun getCollectionDao(): CollectionDao = collectionDao
    fun getChunkDao(): ChunkDao = chunkDao
    fun getTakeDao(): TakeDao = takeDao
    fun getMarkerDao(): MarkerDao = markerDao
}