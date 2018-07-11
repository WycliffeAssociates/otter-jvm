package app.backEnd

import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File

class UserModelTest {
    val userHandler:UserHandler = UserHandler()

    @Test
    fun createTest() {
        var tmp = File("tr.db")
        Assert.assertTrue(tmp.exists())
        userHandler.closeStore()
    }

    @Test(expected = NoSuchElementException::class)
    fun insertDeleteTest() {
        val hash = "213jlk21j3"
        val tmp = userHandler.createUser(hash, "somewhere")
        Assert.assertTrue(tmp._id > 0)
        userHandler.deleteUser(hash)
        userHandler.getUser(hash)

    }

    @Test
    fun getTest(){
        for (i in 0..10){
            userHandler.createUser(i.toString(), i.toString())
        }
        var num = 0
        userHandler.getUsers().forEach {
            Assert.assertEquals(num.toString(), it.hash)
            num++
        }
        for (i in 0..10){
            userHandler.deleteUser(i.toString())
        }
    }

    @After
    fun close(){
        userHandler.closeStore()
    }
}