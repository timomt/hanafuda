import com.google.inject.Guice
import view.TUIManager
import controller.{CommandManager, GameController}
import view.GUIManager

/**
 * Entry point of the Hanafuda application.
 */
object Hanafuda {
    /**
     * Main method to start the application.
     */
    val injector = Guice.createInjector(new HanafudaModule)
    val commandManager = injector.getInstance(classOf[CommandManager])
    GameController.commandManager = commandManager
    
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