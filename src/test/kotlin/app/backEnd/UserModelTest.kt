package app.backEnd

import org.junit.After
import org.junit.Assert
import org.junit.Test
import persistance.TrDatabaseImpl
import persistance.model.UserModel
import java.io.File

class UserModelTest {
    private val trDatabaseImpl = TrDatabaseImpl()
    private val userRepo = trDatabaseImpl.getUserDao()

    @Test
    fun createTest() {
        val tmp = File("tr.db")
        Assert.assertTrue(tmp.exists())
        trDatabaseImpl.closeStore()
    }

    @Test(expected = NoSuchElementException::class)
    fun insertDeleteTest() {
        val hash = "213jlk21j3"
        val tmp = userRepo.insert(hash, "somewhere")
        Assert.assertTrue(tmp.id > 0)
        userRepo.delete(tmp)
        userRepo.getByHash(hash)
    }

    @Test
    fun getTest(){
        val userList = ArrayList<UserModel>()
        for (i in 0..10){
            userList.add(userRepo.insert(i.toString(), i.toString()))
        }
        var num = 0
        userRepo.getAll().forEach {
            Assert.assertEquals(num.toString(), it.hash)
            num++
        }
        for (user in userList){
            userRepo.delete(user = user)
        }
    }

    @After
    fun close(){
        trDatabaseImpl.closeStore()
    }
}