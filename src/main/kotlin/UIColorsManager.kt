
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.onChange
import java.util.*

object UIColorsManager {
    val colorResourceFile = SimpleStringProperty("UIColors")
    val Colors = SimpleObjectProperty<ResourceBundle>()

    init {
        Colors.value = ResourceBundle.getBundle(colorResourceFile.value)
        colorResourceFile.onChange {
            Colors.value = ResourceBundle.getBundle(it)
        }
    }
}