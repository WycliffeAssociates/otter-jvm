import LessAmbiguous.DummyClass
import data.Language
import data.User
import tornadofx.*;

class MyView: View() {

    val myDao = DummyClass();

    val myLanguage = Language(42, "elv", "xkcd", false, "elvish");
    val myUser = User(42, "my wandering friend", "my stranger dearest", mutableListOf(myLanguage),
            mutableListOf(myLanguage) );

    override val root = hbox {
        button {
            label("save");
            action {
                val sub = myDao.insert(myUser).subscribe { println(it) };
            }
        }
    }

}

