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
        GameController.newGame()

        while (true) {
            val input = scala.io.StdIn.readLine(s"Enter your move: ")
            GameController.processInput(input)
        }
    }
}