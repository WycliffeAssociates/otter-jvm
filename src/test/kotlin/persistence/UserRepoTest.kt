package persistence

import data.User
import data.dao.Dao
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteDataSource
import persistence.data.UserFactory
import persistence.model.Models
import persistence.repo.UserRepo

class UserRepoTest {
    private lateinit var userRepo: Dao<User>
    private lateinit var users: List<User>

    @Before
    fun setup(){
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:test.db"

        // creates tables that do not already exist
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.DROP_CREATE)
        // sets up data store
        val config = KotlinConfiguration(dataSource = dataSource, model = Models.DEFAULT)
        val dataStore = KotlinEntityDataStore<Persistable>(config)

        userRepo = UserRepo(dataStore)
        users = UserFactory.makeUserList(10)
        users.forEach {
            it.id  = userRepo.insert(it).blockingFirst()

        }
    }

    @Test
    fun retrieveTest(){
        users.forEach {
            userRepo.getById(it.id).test().assertValue(it)
        }
    }

    @Test
    fun retrieveAllTest(){
        userRepo.getAll().test().assertValue(users)
    }

    @Test
    fun updateTest(){
        users.forEach {
            it.hash = UserFactory.randomUuid()
            userRepo.update(it).test().assertComplete()
        }
    }

    @After
    @Test
    fun deleteTest() {
        users.forEach {
            userRepo.delete(it).test().assertComplete()
        }
    }
}