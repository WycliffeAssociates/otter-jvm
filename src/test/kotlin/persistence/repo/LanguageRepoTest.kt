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
        dataStore = KotlinEntityDataStore(config)

        languageRepo = LanguageRepo(dataStore)
    }

    @Test
    fun insertAndRetrieveByIdTest(){
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
            Assert.assertEquals(it, languageRepo.getById(it.id).blockingFirst())
        }
    }

    @Test
    fun retrieveAllTest(){
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        Assert.assertEquals(LanguageStore.languages, languageRepo.getAll().blockingFirst())
    }

    @Test
    fun retrieveGatewayLanguagesTest(){
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        Assert.assertEquals(languageRepo.getGatewayLanguages().blockingFirst(),
                LanguageStore.languages.filter {
            it.isGateway
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
                    isGateway = false,
                    slug = "khi"
            )
            updatedLanguage.id = it.id

            // try to update the language in the repo

            languageRepo.update(updatedLanguage).blockingGet()

            Assert.assertEquals(languageRepo.getById(updatedLanguage.id).blockingFirst(), updatedLanguage)

            // roll back the tests for the next case
            languageRepo.update(it).blockingGet()
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

            languageRepo.delete(it).blockingGet()
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