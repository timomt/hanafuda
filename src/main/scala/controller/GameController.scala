package controller

import controller.GameController.notifyObservers
import model.{GameManager, GameState}
import view.TUIManager

// TODO: outsource all TUIManager calls into TUIManager update

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
    /*private*/ var gameState: Option[GameState] = None

    /*
    * def newGame()
    * initializes/overwrites this objects gameState with a default game
    * and notifies observers of the new GameState.
    * */
    /*private*/ def newGame(firstPlayer: String, secondPlayer: String): Unit = {
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
            print(TUIManager.printHelp())

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

        case s"match $x $y" =>
            gameState = Some(gameState.get.handleMatch(x, y))
            notifyObservers(gameState.get)

        case s"match $y" =>
            gameState = Some(gameState.get.handleMatch(y, "0"))
            notifyObservers(gameState.get)

        case s"discard $x" =>
            gameState = Some(gameState.get.handleDiscard(x))
            notifyObservers(gameState.get)

        case s"discard" =>
            gameState = Some(gameState.get.handleDiscard("0"))
            notifyObservers(gameState.get)

        case "new" => {
            gameState = Some(GameManager.newGame(gameState.get.players.head.name, gameState.get.players(1).name))
            notifyObservers(gameState.get)
        }
        /*
        case "test colors" =>
            val updatedFirstPlayer = gameState.get.players.head.copy(side = gameState.get.players.head.side.copy(cards = gameState.get.players.head.side.cards :+ Card(MARCH, HIKARI, CURTAIN, false)))
            val newPlayersList = gameState.get.players.tail :+ updatedFirstPlayer
            //val newPlayersList = updatedFirstPlayer :: gameState.players.tail
            val newState = GameState(newPlayersList, gameState.get.deck, gameState.get.board, gameState.get.matched, MatchType.RANDOM, None, None, None)
            println(TUIManager.printOverview(newState))*/

        case "combinations" =>
            println(TUIManager.printOverview(gameState.get))

        case _ =>
            gameState = Some(gameState.get.updateGameStateWithError("Wrong usage, see \"help\"."))
            notifyObservers(gameState.get)
    }

    /*
    * TODO: implement def evaluateScore(...)
    *  Evaluates the highest possible score of each player and returns a tuple of the result.
    *  The first tuple value is the score of the player of the current turn (game.players[0])*/
    //def evaluateScore(game: GameState): (Int, Int) = {}*/
}