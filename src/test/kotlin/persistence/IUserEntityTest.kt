package persistence

import data.User
import io.reactivex.Observable
import io.reactivex.exceptions.UndeliverableException
import org.junit.After
import org.junit.Assert
import org.junit.Test
import persistence.TrDatabaseImpl
import persistence.repo.UserRepo
import java.io.File
import java.util.*
import java.util.concurrent.DelayQueue
import kotlin.NoSuchElementException

class IUserEntityTest {
    private val trDatabaseImpl = TrDatabaseImpl()
    private val userRepo = trDatabaseImpl.getUserDao()

    @Test
    fun createTest() {
        val tmp = File("tr.db")
        Assert.assertTrue(tmp.exists())
        trDatabaseImpl.closeStore()
    }

    @Test
    fun insertTest() {
        val hash = "213jlk21j3"
        val user = User(hash = hash, recordedNamePath =  "Somewhere")
        val tmp = userRepo.insert(user)

        tmp.subscribe {
            userRepo.getById(it).subscribe {
                    Assert.assertEquals(hash, it.hash)
            }
        }
    }

    @Test(expected =  NoSuchElementException::class)
    fun deleteTest() {
        val hash = "213jlk21j3"
        val user = User(hash = hash, recordedNamePath =  "Somewhere")
        val tmp = userRepo.delete(user)
        if (userRepo is UserRepo) {
            userRepo.getByHash(user.hash)
        }
    }

    @Test
    fun getTest(){
        val userList = ArrayList<Observable<Int>>()
        for (i in 0..10){
            userList.add(userRepo.insert(User(hash = i.toString(), recordedNamePath = i.toString())))
        }
        var num = 0
        userRepo.getAll().subscribe{
            it.forEach {
                Assert.assertEquals(num.toString(), it.hash)
                num++
            }
        }
    }

    @After
    fun close(){
        trDatabaseImpl.closeStore()
    }
}