import view.TUIManager
import controller.GameController
import view.GUIManager

/**
 * Entry point of the Hanafuda application.
 */
object Hanafuda {
    /**
     * Main method to start the application.
     */
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