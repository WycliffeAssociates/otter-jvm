
import javafx.beans.property.SimpleStringProperty
import java.util.*

object UIColorsManager {
    val colorResourceFile = SimpleStringProperty("UIColors")
    val Colors = ResourceBundle.getBundle(colorResourceFile.value)
}