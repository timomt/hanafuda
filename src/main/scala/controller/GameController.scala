package controller

import controller.GameController.notifyObservers
import model.{DisplayType, GameState, GameStatePendingKoiKoi, GameStateUninitialized}

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
    var gameState: GameState = GameStateUninitialized(displayType = DisplayType.HELP, stderr = None)
    private val commandManager = new CommandManager()

    /*
    * def processInput(...)
    * processes a String to change the current GameState
    * and notifies observers of the new GameState.
    * */
    def processInput(input: String): Unit = {
        gameState = input match {
            // $COVERAGE-OFF$
            case "exit" =>
                sys.exit(0)
            // $COVERAGE-ON$

            case "help" =>
                commandManager.executeCommand(new HelpCommand, gameState)

            case "undo" =>
                commandManager.undo(gameState)

            case "redo" =>
                commandManager.redo(gameState)

            case i if gameState.isInstanceOf[GameStateUninitialized] =>
                i match {
                    case s"start $firstPlayer $secondPlayer" =>
                        val command = new StartGameCommand(firstPlayer, secondPlayer)
                        commandManager.executeCommand(command, gameState)
                    case _ => gameState.updateGameStateWithError("You have to start a game first.")
                }

            case "combinations" | "com" =>
                commandManager.executeCommand(new CombinationsCommand, gameState)

            case i if gameState.isInstanceOf[GameStatePendingKoiKoi] =>
                i match {
                    case "koi-koi" =>
                        commandManager.executeCommand(new KoiKoiCommand, gameState)
                    case "finish" =>
                        commandManager.executeCommand(new FinishCommand, gameState)
                    case _ =>
                        gameState.updateGameStateWithError("You have to either call \"koi-koi\" or \"finish\".")
                }

            case "continue" | "con" =>
                commandManager.executeCommand(new ContinueCommand, gameState)

            case s"match $x $y" =>
                commandManager.executeCommand(new MatchCommand(x, y), gameState)

            case s"match $y" =>
                commandManager.executeCommand(new MatchCommand(y, "0"), gameState)

            case s"discard $x" =>
                commandManager.executeCommand(new DiscardCommand(x), gameState)

            case s"discard" =>
                commandManager.executeCommand(new DiscardCommand("0"), gameState)

            case "new" =>
                commandManager.executeCommand(new NewCommand, gameState)

            case _ =>
                gameState.updateGameStateWithError("Wrong usage, see \"help\".")
        }
        notifyObservers(gameState)
    }
}