package persistence

import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.sqlite.SQLiteDataSource
import persistence.model.Models
import persistence.repo.UserRepo

class TrDatabaseImpl {
    private val dataStore: KotlinEntityDataStore<Persistable>
    private val userRepo: UserRepo
    init {
        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:tr.db"

        // creates tables that do not already exist
        SchemaModifier(sqLiteDataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        // sets up data store
        val config = KotlinConfiguration(dataSource = sqLiteDataSource, model = Models.DEFAULT)
        dataStore = KotlinEntityDataStore(config)

        userRepo = UserRepo(dataStore)
    }

    fun getUserDao(): UserRepo{
        return userRepo
    }

    // function to close the data store
    fun closeStore(){
        dataStore.close()
    }
}