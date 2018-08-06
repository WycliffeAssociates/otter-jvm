import tornadofx.*
import java.net.URI
import javafx.scene.media.AudioClip

class MyView: View() {
    val audioPlayer = AudioPlayer();
    override val root = vbox {
        button("Press me to load"){
            onDoubleClick {
//                val myURI = URI("file:///C:/Users/stockwja/Documents/GitHub/8woc2018-jvm/TestSHORT.wav")
                val myAudioClip = AudioClip("file:///C:/Users/stockwja/Documents/GitHub/8woc2018-jvm/TestSHORT.wav")
                audioPlayer.loadFromAudioClip(myAudioClip)
            }
        }
        button("Press me to play"){
            onDoubleClick {
                val myURI = URI("file:///C:/Users/stockwja/Documents/GitHub/8woc2018-jvm/TestSHORT.wav")
                audioPlayer.play()
            }
        }
        button("Press me to stop"){
            onDoubleClick {
                val myURI = URI("file:///C:/Users/stockwja/Documents/GitHub/8woc2018-jvm/TestSHORT.wav")
                audioPlayer.stop()
            }
        }
    }
}

class MyApp: App(MyView::class)

fun main(args: Array<String>) {
    launch<MyApp>(args)
}
