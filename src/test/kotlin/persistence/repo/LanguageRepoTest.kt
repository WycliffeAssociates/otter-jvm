package persistence.repo

import data.model.Language
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteDataSource
import persistence.data.LanguageStore
import persistence.tables.pojos.*
import java.io.File

class LanguageRepoTest {
    private lateinit var languageRepo: LanguageRepo
    @Before
    fun setup() {
        Class.forName("org.sqlite.JDBC")
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:test.sqlite"

        val config = DSL.using(dataSource.connection, SQLDialect.SQLITE).configuration()
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
    }

    @Test
    fun insertAndRetrieveByIdTest() {
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
            Assert.assertEquals(it, languageRepo.getById(it.id).blockingFirst())
        }
    }

    @Test
    fun retrieveAllTest() {
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        Assert.assertEquals(LanguageStore.languages, languageRepo.getAll().blockingFirst())
    }

    @Test
    fun retrieveGatewayLanguagesTest() {
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        Assert.assertEquals(
                languageRepo
                        .getGatewayLanguages()
                        .blockingFirst(),
                LanguageStore.languages.filter {
                    it.isGateway
                }
        )
    }

    @Test
    fun updateTest() {
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
//
//    @Test
//    fun deleteTest() {
//        val testUser = UserEntity()
//        testUser.setAudioPath("somepath")
//        testUser.setAudioHash("12345678")
//        testUser.id = dataStore.insert(testUser).id
//
//        LanguageStore.languages.forEach {
//            it.id = languageRepo.insert(it).blockingFirst()
//
//            val testUserLanguage = UserLanguage()
//            testUserLanguage.setLanguageEntityid(it.id)
//            testUserLanguage.setUserEntityid(testUser.id)
//
//            dataStore.insert(testUserLanguage)
//
//            languageRepo.delete(it).blockingGet()
//            try {
//                Assert.assertTrue(dataStore
//                        .select(IUserLanguage::class)
//                        .where(IUserLanguage::languageEntityid eq it.id)
//                        .get()
//                        .toList()
//                        .isEmpty()
//                )
//            } catch (e: AssertionError) {
//                println("Failed on")
//                println(it.slug)
//                throw e
//            }
//        }
//    }
}