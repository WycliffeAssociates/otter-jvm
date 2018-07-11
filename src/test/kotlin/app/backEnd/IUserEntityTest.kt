package app.backEnd

import data.User
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert
import org.junit.Test
import persistence.TrDatabaseImpl
import persistence.model.UserEntity
import java.io.File
import java.util.*

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
    fun insertDeleteTest() {
        val hash = "213jlk21j3"
        val user = User(hash = hash, recordedNamePath =  "Somewhere")
        val tmp = userRepo.insert(user)

        tmp.subscribe {
            userRepo.getById(it).subscribe {
                    Assert.assertEquals(hash, it.hash)
            }
        }
    }
//
//    @Test
//    fun getTest(){
//        val userList = ArrayList<Observable<Int>>()
//        for (i in 0..10){
//            userList.add(userRepo.insert(i.toString(), i.toString()))
//        }
//        var num = 0
//        userRepo.getAll().forEach {
//            Assert.assertEquals(num.toString(), it.hash)
//            num++
//        }
//        for (user in userList){
//            userRepo.delete(user = user)
//        }
//    }
//
    @After
    fun close(){
        trDatabaseImpl.closeStore()
    }
}