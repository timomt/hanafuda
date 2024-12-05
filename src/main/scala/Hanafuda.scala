import controller.GameController
import view.TUIManager
import controller.GameController
import view.GUIManager

/*
* object Hanafuda
* entry point of this application.
* */

object Hanafuda {
    @main
    def main(): Unit = {
        GameController.add(TUIManager)
        GameController.add(GUIManager)
        println(TUIManager.printHelp())

        new Thread(() => {
            GUIManager.main(Array.empty)
        }).start()

        // Run the TUI in the main thread
        while (true) {
            val input = scala.io.StdIn.readLine(s"\n")
            GameController.processInput(input)
        }
    }
}