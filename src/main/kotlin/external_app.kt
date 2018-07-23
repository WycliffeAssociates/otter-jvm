import app.filesystem.ProfileConfig
import app.filesystem.DirectoryProvider
import app.filesystem.Take
import java.io.File
import java.io.Reader
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.*


fun main(args:Array<String>) {

    val os = System.getProperty("os.name").toUpperCase() //current user's OS

    val locale = Locale.getDefault()

    val labels= ResourceBundle.getBundle("Resources", locale) //accesses the properties file for the current project

    val program = labels.getString("Ext_Recording_Application_Name") //gets the external recording application name

    //create the take recording file (tests AudioStore class functions)
    val take = Take("en", "ot", "gen", "01", "01", "02")
    val take_recording_file = take.createFile()


    //open the specified external application record the take file
    try {
        println("Opening $program")

        //open Audacity (must be installed on the user's computer)
        try{
            //determines and executes the appropriate command to open Audacity
            if (os.contains("WIN")){
                //need to fix: Windows does not recognize start command for external apps
                val process = Runtime.getRuntime().exec("start $program $take_recording_file.absolutePath")
            }
            else if (os.contains("MAC")){
                val process = Runtime.getRuntime().exec("open -a $program $take_recording_file.absolutePath")
            }
            else { //Linux OS
                val process = Runtime.getRuntime().exec("$program $take_recording_file.absolutePath")
            }
            //calls helper function to get the current status of Audacity
            var status = getCurrStatus(os, true)
            while(status == 1){
                status = getCurrStatus(os, false)
            }
            println("User has closed the program")
        }
        catch (e:Exception) {
            println("Error getting status of $program")
            e.printStackTrace()
        }
    }
    catch (e:Exception) {
        println("Error creating file")
        e.printStackTrace()
    }
}


//gets the current status of Audacity on the user's system
fun getCurrStatus(os: String, first: Boolean):Int{
    val status_file: String

    /*locates the appropriate shell executable file to execute in order to determine the
      status of Audacity on the user's system*/
    if (os.contains("WIN")) {
        status_file = "./findstatus_WIN.sh"
    }
    else if (os.contains("MAC")){
        status_file = "./findstatus_MAC.sh"
    }
    else{
        status_file = "./findstatus_LINUX.sh"
    }

    /*execute the appropriate shell executable file to find currently running
      processes on the system and filter according to Audacity*/
    val audacityPB = ProcessBuilder(status_file)
    val audacityProcess = audacityPB.start()

    //load the output of the shell executable
    val stdin = BufferedReader(InputStreamReader(audacityProcess.getInputStream()))
    var prcs = 0

    var s = stdin.readLine()

    while(s != null){
       if (!s.contains("grep Audacity.app")){
            prcs = 1
       }
       s = stdin.readLine()
    }
        
     if (os.contains("MAC")){
        if (!first){
            //return the status 
            return prcs
        }
        return 1
     }
     else{
         //return the status
         return prcs
     }
}








