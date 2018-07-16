package persistence

import data.Language
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
import persistence.model.Models
import persistence.repo.LanguageRepo

class LanguageRepoTest {
    private lateinit var languageRepo: LanguageRepo
    private lateinit var languages: List<Language>

    @Before
    fun setup(){
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:test.db"

        // creates tables that do not already exist
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.DROP_CREATE)
        // sets up data store
        val config = KotlinConfiguration(dataSource = dataSource, model = Models.DEFAULT)
        val dataStore = KotlinEntityDataStore<Persistable>(config)

        languageRepo = LanguageRepo(dataStore)
        languages = LanguageFactory.makeLanguages(10)
        languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
    }

    @Test
    fun retrieveTest(){
        languages.forEach {
            languageRepo.getById(it.id).test().assertValue(it)
        }
    }

    @Test
    fun retrieveAllTest(){
        languageRepo.getAll().test().assertValue(languages)
    }

    @Test
    fun updateTest(){
        languages.forEach {
            val newLanguage = Language(
                    it.id,
                    LanguageFactory.randomUuid(),
                    LanguageFactory.randomUuid(),
                    true,
                    LanguageFactory.randomUuid()
            )
            languageRepo.update(newLanguage).test().assertComplete()
        }
    }

    @After
    @Test
    fun deleteTest() {
        languages.forEach {
            languageRepo.delete(it).test().assertComplete()
        }
    }
}