package persistance

import app.requery.Models
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.sqlite.SQLiteDataSource
import persistance.repo.UserRepo

class TrDatabaseImpl {
    private val dataStore: KotlinEntityDataStore<Persistable>
    val userRepo: UserRepo
    init {
        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:tr.db"
        val conn = sqLiteDataSource.connection.isClosed

        // creates tables that do not already exist
        SchemaModifier(sqLiteDataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        // sets up datastore
        val config = KotlinConfiguration(dataSource = sqLiteDataSource, model = Models.DEFAULT)
        dataStore = KotlinEntityDataStore<Persistable>(config)

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