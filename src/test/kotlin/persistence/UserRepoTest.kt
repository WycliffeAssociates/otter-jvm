package persistence

import data.Language
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
import persistence.data.LanguageFactory
import persistence.data.UserFactory
import persistence.model.Models
import persistence.repo.LanguageRepo
import persistence.repo.UserRepo
import java.io.File

class UserRepoTest {
    private lateinit var userRepo: Dao<User>
    private lateinit var languageRepo: Dao<Language>
    private lateinit var users: List<User>

    @Before
    fun setup(){
        var file = File("test.db")
        if(file.exists()){
            file.delete()
        }
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:test.db"

        // creates tables that do not already exist
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)
        // sets up data store
        val config = KotlinConfiguration(dataSource = dataSource, model = Models.DEFAULT)
        val dataStore = KotlinEntityDataStore<Persistable>(config)

        userRepo = UserRepo(dataStore)
        languageRepo = LanguageRepo(dataStore)
        users = UserFactory.makeUserList(1)
        users.forEach {
            it.sourceLanguages.forEach {
                it.id = languageRepo.insert(it).blockingFirst()
            }
            it.targetLanguages.forEach {
                it.id = languageRepo.insert(it).blockingFirst()
            }
            it.preferredTargetLanguage.id = languageRepo.insert(it.preferredTargetLanguage).blockingFirst()
            it.preferredSourceLanguage.id = languageRepo.insert(it.preferredSourceLanguage).blockingFirst()
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
            it.audioHash = UserFactory.randomUuid()
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