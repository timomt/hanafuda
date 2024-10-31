package controller

import controller.GameController.notifyObservers
import model.CardMonth.MARCH
import model.CardName.CURTAIN
import model.CardType.HIKARI
import model.{Card, Deck, GameManager, GameState, Player}
import view.TUIManager

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
    private var gameState: Option[GameState] = None

    /*
    * def newGame()
    * initializes/overwrites this objects gameState with a default game
    * and notifies observers of the new GameState.
    * */
    private def newGame(firstPlayer: String, secondPlayer: String): Unit = {
        gameState = Some(GameManager.newGame(firstPlayer, secondPlayer))
        notifyObservers(gameState.get)
    }

    /*
    * def processInput(...)
    * processes a String to change the current GameState
    * and notifies observers of the new GameState.
    * TODO: process input and create new GameState
    * */
    def processInput(input: String): Unit = input match {
            case "help" =>
                println(TUIManager.printHelp())

            case "exit" => 
                sys.exit(0)
                
            case i if gameState.isEmpty =>
                i match {
                    case s"start $firstPlayer $secondPlayer" => newGame(firstPlayer, secondPlayer)
                    case _ => println("[Error]: You submitted a command that requires a started game without starting it correctly.")
                }

            // All following cases assert gameState is Some
            case "continue" =>
                notifyObservers(gameState.get)

            case s"match $x $y" if x.toInt >= 1 && x.toInt <= 8 && y.toInt >= 1 && y.toInt <= 8 =>
                GameManager.matchCards(gameState.get, x.toInt - 1, y.toInt - 1)

            case "test colors" =>
                val updatedFirstPlayer = gameState.get.players.head.copy(side = gameState.get.players.head.side.copy(cards = gameState.get.players.head.side.cards :+ Card(MARCH, HIKARI, CURTAIN)))
                val newPlayersList = gameState.get.players.tail :+ updatedFirstPlayer
                //val newPlayersList = updatedFirstPlayer :: gameState.players.tail
                val newState = GameState(newPlayersList, gameState.get.deck, gameState.get.board)
                println(TUIManager.printOverview(newState))

            case "combinations" =>
                println(TUIManager.printOverview(gameState.get))

            case _ =>
                println("Invalid input. Please enter in the format: match x y where x and y are integers within the range.")
                notifyObservers(gameState.get)
        }
}