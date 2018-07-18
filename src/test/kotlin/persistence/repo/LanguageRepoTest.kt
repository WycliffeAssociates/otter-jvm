package persistence.repo

import data.Language
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteDataSource
import persistence.data.LanguageStore
import persistence.model.*
import java.util.*

class LanguageRepoTest {
    private lateinit var languageRepo: LanguageRepo
    private lateinit var dataStore: KotlinEntityDataStore<Persistable>

    @Before

    fun setup(){
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:test.db"

        // creates tables that do not already exist
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.DROP_CREATE)
        // sets up data store
        val config = KotlinConfiguration(dataSource = dataSource, model = Models.DEFAULT)
        dataStore = KotlinEntityDataStore<Persistable>(config)

        languageRepo = LanguageRepo(dataStore)
    }

    @Test
    fun insertAndRetrieveByIdTest(){
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
            languageRepo.getById(it.id).test().assertValue(it)
        }
    }

    @Test
    fun retrieveAllTest(){
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        languageRepo.getAll().test().assertValue(LanguageStore.languages)
    }

    @Test
    fun retrieveSourceLanguagesTest(){
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        languageRepo.getSourceLanguages().test().assertValue( LanguageStore.languages.filter {
            it.canBeSource
        })
    }

    @Test
    fun updateTest(){
        LanguageStore.languages.forEach {
            // insert the original language
            it.id = languageRepo.insert(it).blockingFirst()

            // create the updated version of the language
            val updatedLanguage = Language(
                    name = "Khoisan",
                    anglicizedName = "Khoisan",
                    canBeSource = false,
                    slug = "khi"
            )
            updatedLanguage.id = it.id

            // try to update the language in the repo
            languageRepo.update(updatedLanguage).test().assertComplete()
            languageRepo.getById(updatedLanguage.id).test().assertValue(updatedLanguage)
            // roll back the tests for the next case
            languageRepo.update(it).test().assertComplete()
        }
    }

    @Test
    fun deleteTest() {
        val testUser = UserEntity()
        testUser.setAudioPath("somepath")
        testUser.setAudioHash("12345678")
        testUser.id = dataStore.insert(testUser).id

        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()

            val testUserLanguage = UserLanguage()
            testUserLanguage.setLanguageEntityid(it.id)
            testUserLanguage.setUserEntityid(testUser.id)

            dataStore.insert(testUserLanguage)

            languageRepo.delete(it).test().assertComplete()
            try {
                Assert.assertTrue(dataStore.select(IUserLanguage::class).where(IUserLanguage::languageEntityid eq it.id).get().toList().isEmpty())
            } catch (e: AssertionError) {
                println("Failed on")
                println(it.slug)
                throw e
            }
        }
    }
}