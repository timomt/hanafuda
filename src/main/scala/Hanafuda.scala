import controller.GameController
import view.TUIManager

/*
* object Hanafuda
* entry point of this application.
* */

object Hanafuda {
    @main
    def main(): Unit = {
        GameController.add(TUIManager)
        println(TUIManager.printHelp())
        
        while (true) {
            val input = scala.io.StdIn.readLine(s"\n")
            GameController.processInput(input)
        }
    }
}