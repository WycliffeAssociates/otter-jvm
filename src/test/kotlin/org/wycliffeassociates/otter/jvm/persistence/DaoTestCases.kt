package org.wycliffeassociates.otter.jvm.persistence

import io.reactivex.Observable
import org.junit.Assert
import org.wycliffeassociates.otter.common.data.dao.Dao

object DaoTestCases {

    fun <T: Any> assertInsertAndRetrieveSingle(dao: Dao<T>, testObject: T) {
        val insertedId = dao
                .insert(testObject)
                .blockingFirst()
        setId(testObject, insertedId)
        val retrievedObject = dao
                .getById(insertedId)
                .blockingFirst()
        Assert.assertEquals(testObject, retrievedObject)
    }

    fun <T: Any> assertInsertAndRetrieveAll(dao: Dao<T>, testObjects: List<T>) {
        // Assume empty dao
        testObjects.forEach {
            val insertedId = dao
                    .insert(it)
                    .blockingFirst()
            setId(it, insertedId)
        }
        // Get all
        val retrievedObjects = dao
                .getAll()
                .blockingFirst()
        Assert.assertTrue(retrievedObjects.containsAll(testObjects))
    }

    fun <T: Any> assertUpdate(dao: Dao<T>, testObject: T) {
        dao
                .update(testObject)
                .blockingAwait()
        val id = getId(testObject)
        val retrievedObject = dao
                .getById(id)
                .blockingFirst()
        Assert.assertEquals(testObject, retrievedObject)
    }

    fun <T: Any> assertDelete(dao: Dao<T>, testObject: T) {
        dao
                .delete(testObject)
                .blockingAwait()
        val id = getId(testObject)
        Assert.assertTrue(dao
                .getById(id)
                .onErrorResumeNext { it: Throwable ->
                    if (it is NoSuchElementException) {
                        // Expected result
                    } else {
                        Assert.fail()
                    }
                    Observable.empty<T>()
                }
                .isEmpty
                .blockingGet())
    }

    private fun <T: Any> setId(obj: T, id: Int) {
        val idField = obj.javaClass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(obj, id)
    }

    private fun <T: Any> getId(obj: T): Int {
        val idField = obj.javaClass.getDeclaredField("id")
        idField.isAccessible = true
        return idField.getInt(obj)
    }
}