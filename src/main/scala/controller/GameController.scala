package controller

import FileIO.FileIO
import com.google.inject.Inject
import model.{DisplayType, GameState, GameStatePendingKoiKoi, GameStateUninitialized}
import controller.CommandManager.CommandManager
import model.GameManager.GameManager
import model.GameManager.GameManagerInstance.given_GameManager

import scala.compiletime.uninitialized

/**
 * MVC: Controller
 * Object to operate in between model and view.
 */
object GameController extends Observable {
    /**
     * The current state of the game operated by this object.
     */
    var gameState: GameState = GameStateUninitialized(displayType = DisplayType.HELP, stderr = None)
    @Inject var commandManager: CommandManager = uninitialized
    @Inject var fileIO: FileIO = uninitialized
    
    /**
     * Processes a String to change the current GameState
     * and notifies observers of the new GameState.
     *
     * @param input the input string to process
     */
    def processInput(input: String): Unit = {
        gameState = input match {
            case "exit" =>
                sys.exit(0)

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

            case "save" =>
                commandManager.executeCommand(new SaveCommand, gameState)

            case "load" =>
                commandManager.executeCommand(new LoadCommand, gameState)
                
            case _ =>
                gameState.updateGameStateWithError("Wrong usage, see \"help\".")
        }
        notifyObservers(gameState)
    }
}