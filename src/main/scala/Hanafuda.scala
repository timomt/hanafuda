import FileIO.FileIO
import _root_.FileIO.FileIOJSON.ConfigManager
import com.google.inject.Guice
import view.TUIManager
import controller.CommandManager.CommandManager
import controller.GameController
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
    val fileIO = injector.getInstance(classOf[FileIO])
    GameController.commandManager = commandManager
    GameController.fileIO = fileIO
    
    @main
    def main(): Unit = {
        ConfigManager.loadConfig().concurrency match {
            case "tui" =>
                GameController.add(TUIManager)
                println(TUIManager.printHelp())
                while (true) {
                    val input = scala.io.StdIn.readLine(s"\n")
                    GameController.processInput(input)
                }

            case "gui" =>
                GameController.add(GUIManager)
                new Thread(() => {
                    GUIManager.main(Array.empty)
                }).start()

            case _ =>
                GameController.add(TUIManager)
                GameController.add(GUIManager)
                println(TUIManager.printHelp())

                new Thread(() => {
                    GUIManager.main(Array.empty)
                }).start()
                while (true) {
                    val input = scala.io.StdIn.readLine(s"\n")
                    GameController.processInput(input)
                }
        }
    }
}