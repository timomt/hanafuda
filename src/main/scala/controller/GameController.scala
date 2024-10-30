package controller

import controller.GameController.notifyObservers
import model.{GameManager, GameState}
import scala.compiletime.uninitialized

/*
* MVC: Controller
* object GameController
* an object to operate in between model and view.
* */
object GameController extends Observable {
    /*
    * gameState
    * the current state of the game operated by this object.
    * */
    private var gameState: GameState = uninitialized

    /*
    * def newGame()
    * initializes/overwrites this objects gameState with a default game
    * and notifies observers of the new GameState.
    * */
    def newGame(): Unit = {
        gameState = GameManager.newGame()
        notifyObservers(gameState)
    }

    /*
    * def processInput(...)
    * processes a String to change the current GameState
    * and notifies observers of the new GameState.
    * TODO: process input and create new GameState
    * */
    def processInput(input: String): Unit = {
        val newGameState = input match {
            case s"match $x $y" if x.toInt >= 1 && x.toInt <= 8 && y.toInt >= 1 && y.toInt <= 8 =>
                GameManager.matchCards(gameState, x.toInt - 1, y.toInt - 1)
            case _ =>
                println("Invalid input. Please enter in the format: match x y where x and y are integers within the range.")
                gameState
        }
        gameState = newGameState
        notifyObservers(gameState)
    }
}