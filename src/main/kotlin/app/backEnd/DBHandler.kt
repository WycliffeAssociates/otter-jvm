package app.backEnd

import app.requery.Models
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.sqlite.SQLiteDataSource

abstract class DBHandler{
    protected val dataStore: KotlinEntityDataStore<Persistable>
    init {
        Class.forName("org.sqlite.JDBC")

        val ds = SQLiteDataSource()
        ds.url = "jdbc:sqlite:tr.db"
        val conn = ds.connection.isClosed

        // creates tables that do not already exist
        SchemaModifier(ds, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        // sets up datastore
        val config = KotlinConfiguration(dataSource = ds, model = Models.DEFAULT)
        dataStore = KotlinEntityDataStore<Persistable>(config)
    }

    // function to close the data store
    fun closeStore(){
        dataStore.close()
    }



}