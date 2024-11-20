import controller.GameController
import view.TUIManager
import controller.GameController
import view.{TUIManager, GUIManager}
import javafx.application.Application

/*
* object Hanafuda
* entry point of this application.
* */

object Hanafuda {
    @main
    def main(): Unit = {
        GameController.add(TUIManager)
        println(TUIManager.printHelp())

        // Start the GUI in a separate thread
        new Thread(() => {
            Application.launch(classOf[GUIManager])
        }).start()

        // Run the TUI in the main thread
        while (true) {
            val input = scala.io.StdIn.readLine(s"\n")
            GameController.processInput(input)
        }
    }
}