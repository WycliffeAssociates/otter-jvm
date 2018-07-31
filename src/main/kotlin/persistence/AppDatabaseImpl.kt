package persistence

import app.filesystem.DirectoryProvider
import data.dao.Dao
import data.dao.LanguageDao
import data.model.*
import data.persistence.AppDatabase
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.sqlite.SQLiteDataSource
import persistence.repo.LanguageRepo
import persistence.repo.UserLanguageRepo
import persistence.repo.UserRepo
import java.io.File
import java.nio.file.FileSystems

object AppDatabaseImpl: AppDatabase {
    private val config: Configuration
    // changed names to repo to distinguish our DAOS from generated
    private val languageRepo: LanguageDao
    private val userLanguageRepo: UserLanguageRepo
    private val userRepo: Dao<User>

    init {
        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:" +
                DirectoryProvider("8woc2018").getAppDataDirectory("", false) +
                FileSystems.getDefault().separator +
                "content.sqlite"

        sqLiteDataSource.config.toProperties().setProperty("foreign_keys", "true")

        config = DSL.using(sqLiteDataSource, SQLDialect.SQLITE).configuration()
        val file = File("src/main/Resources/TestAppDbInit.sql")
        var sql = StringBuffer()
        file.forEachLine {
            sql.append(it)
            if (it.contains(";")){
                config.dsl().fetch(sql.toString())
                sql.delete(0, sql.length)
            }
        }

        languageRepo = LanguageRepo(config)
        userLanguageRepo = UserLanguageRepo(config)
        userRepo = UserRepo(config, languageRepo)
    }

    override fun getUserDao() =  userRepo

    override fun getLanguageDao() = languageRepo

    override fun getAnthologyDao(): Dao<Anthology> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookDao(): Dao<Book> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChapterDao(): Dao<Chapter> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChunkDao(): Dao<Chunk> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProjectDao(): Dao<Project> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTakesDao(): Dao<Take> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}