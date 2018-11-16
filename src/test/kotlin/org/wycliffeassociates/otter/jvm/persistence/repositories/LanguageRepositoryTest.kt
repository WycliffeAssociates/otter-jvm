package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.mockito.stubbing.Answer
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.persistence.database.daos.LanguageDao
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase

class LanguageRepositoryTest {
    val mockDatabase = MockDatabase.database()

    // UUT
    private val languageRepository = LanguageRepository(mockDatabase)

    @Test
    fun shouldCRUDLanguage() {
        val language = create()

        val retrieved = retrieveBySlug(language.slug)
        Assert.assertEquals(language, retrieved)

        update(language)
        val retrievedUpdated = retrieveBySlug(language.slug)
        Assert.assertEquals(language, retrievedUpdated)

        delete(language)
        try {
            retrieveBySlug(language.slug)
            Assert.fail()
        } catch (exc: RuntimeException) {
            // Do nothing; this is expected
        }
    }

    @Test
    fun shouldHandleDaoExceptionInUpdate() {
        val language: Language = mock { on { id } doReturn 0 }
        val mockExceptionDao: LanguageDao = mock(defaultAnswer = Answer<Any> { throw RuntimeException() })
        whenever(mockDatabase.getLanguageDao()).doReturn(mockExceptionDao)
        try {
            languageRepository.update(language).blockingAwait()
        } catch (e: java.lang.RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldGetAllLanguages() {
        val languages = listOf(create(), create("ar"))
        val retrieved = languageRepository.getAll().blockingGet()
        Assert.assertEquals(languages, retrieved)
    }

    @Test
    fun shouldGetGatewayLanguages() {
        val gateway = create("en", true)
        create("ar", false)
        val retrieved = languageRepository.getGateway().blockingGet()
        Assert.assertEquals(listOf(gateway), retrieved)
    }

    @Test
    fun shouldGetTargetLanguages() {
        val target = create("en", false)
        create("ar", true)
        val retrieved = languageRepository.getTargets().blockingGet()
        Assert.assertEquals(listOf(target), retrieved)
    }

    @Test
    fun shouldInsertAll() {
        val languages = listOf(
                create("en", true, false),
                create("ar", false, false)
        )
        val ids = languageRepository.insertAll(languages).blockingGet()
        for (i in 0 until languages.size) {
            languages[i].id = ids[i]
        }
        val retrieved = languageRepository.getAll().blockingGet()
        Assert.assertEquals(languages, retrieved)
    }

    // CRUD methods
    private fun create(slug: String = "en", gateway: Boolean = true, autoInsert: Boolean = true): Language {
        val language = Language(
                slug,
                "English",
                "English",
                "ltr",
                gateway
        )
        if (autoInsert) language.id = languageRepository.insert(language).blockingGet()
        return language
    }

    private fun retrieveBySlug(slug: String): Language = languageRepository
                    .getBySlug(slug)
                    .blockingGet()

    private fun update(language: Language) {
        language.slug = "fr"
        language.name = "Français"
        language.anglicizedName = "French"
        language.direction = "rtl"
        language.isGateway = false
        languageRepository.update(language).blockingAwait()
    }

    private fun delete(language: Language) {
        languageRepository.delete(language).blockingAwait()
    }
}