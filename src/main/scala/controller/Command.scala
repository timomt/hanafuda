package controller

import model.{DisplayType, GameManager, GameState}

/**
 * Trait representing a command that can be executed, undone, and redone.
 */
trait Command {
    /**
     * The previous state of the game before the command was executed.
     */
    protected var previousState: Option[GameState] = None

    /**
     * Executes the command and returns the new game state and the executed command.
     *
     * @param currentState the current state of the game
     * @return a tuple containing the new game state and the executed command
     */
    def execute(currentState: GameState): (GameState, Command)

    /**
     * Undoes the command and returns the previous game state.
     *
     * @param currentState the current state of the game
     * @return the previous game state
     */
    def undo(currentState: GameState): GameState = {
        previousState.getOrElse(throw new IllegalStateException("Undo called before execute. Call execute first."))
    }
}

trait CommandManager {
    protected val undoStack: scala.collection.mutable.Stack[Any]
    protected val redoStack: scala.collection.mutable.Stack[Any]
    def executeCommand(command: Command, currentState: GameState): GameState
    def undo(currentState: GameState): GameState
    def redo(currentState: GameState): GameState
}

/**
 * Class managing the execution, undoing, and redoing of commands.
 */
class CommandManagerSaveCommand extends CommandManager {
    protected val undoStack = scala.collection.mutable.Stack[Any]()
    protected val redoStack = scala.collection.mutable.Stack[Any]()

    def executeCommand(command: Command, currentState: GameState): GameState = {
        val (newState, executedCommand) = command.execute(currentState)
        undoStack.push(executedCommand)
        redoStack.clear()
        newState
    }

    def undo(currentState: GameState): GameState = {
        if (undoStack.nonEmpty) {
            val command = undoStack.pop()
            val newState = command.asInstanceOf[Command].undo(currentState)
            redoStack.push(command)
            newState
        } else {
            currentState.updateGameStateWithError("There is nothing to undo.")
        }
    }

    def redo(currentState: GameState): GameState = {
        if (redoStack.nonEmpty) {
            val command = redoStack.pop()
            val (newState, executedCommand) = command.asInstanceOf[Command].execute(currentState)
            undoStack.push(executedCommand)
            newState
        } else {
            currentState.updateGameStateWithError("There is nothing to redo.")
        }
    }
}


class CommandManagerSaveState extends CommandManager {
    protected val undoStack = scala.collection.mutable.Stack[Any]()
    protected val redoStack = scala.collection.mutable.Stack[Any]()

    def executeCommand(command: Command, currentState: GameState): GameState = {
        val (newState, executedCommand) = command.execute(currentState)
        undoStack.push(currentState)
        redoStack.clear()
        newState
    }

    def undo(currentState: GameState): GameState = {
        if (undoStack.nonEmpty) {
            val previousState = undoStack.pop()
            redoStack.push(currentState)
            previousState.asInstanceOf[GameState]
        } else {
            currentState.updateGameStateWithError("There is nothing to undo.")
        }
    }

    def redo(currentState: GameState): GameState = {
        if (redoStack.nonEmpty) {
            val nextState = redoStack.pop()
            undoStack.push(currentState)
            nextState.asInstanceOf[GameState]
        } else {
            currentState.updateGameStateWithError("There is nothing to redo.")
        }
    }
}


/**
 * Command to start a new game.
 *
 * @param firstPlayer the name of the first player
 * @param secondPlayer the name of the second player
 */
class StartGameCommand(firstPlayer: String, secondPlayer: String) extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = GameManager.newGame(firstPlayer, secondPlayer)
        (newState, this)
    }
}

/**
 * Command to display the help information.
 */
class HelpCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.updateGameStateWithDisplayType(DisplayType.HELP)
        (newState, this)
    }
}

/**
 * Command to display the combinations information.
 */
class CombinationsCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.updateGameStateWithDisplayType(DisplayType.COMBINATIONS)
        (newState, this)
    }
}

/**
 * Command to handle the koi-koi call.
 */
class KoiKoiCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = GameManager.koiKoiCallHandler(currentState)
        (newState, this)
    }
}

/**
 * Command to finish the game.
 */
class FinishCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val (firstS, secS) = GameManager.evaluateScore(currentState.players, 1, 1)
        val newState = GameManager.handleKoiKoi(currentState.players, firstS, secS, board = currentState.board, deck = currentState.deck)
        (newState, this)
    }
}

/**
 * Command to continue the game.
 */
class ContinueCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.updateGameStateWithDisplayType(DisplayType.GAME)
        (newState, this)
    }
}

/**
 * Command to start a new game with the same players.
 */
class NewCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = GameManager.newGame(currentState.players.head.name, currentState.players(1).name, currentState.players.head.score, currentState.players(1).score)
        (newState, this)
    }
}

/**
 * Command to match cards.
 *
 * @param xString the index+1 of the card in the player's hand  (or board if from stack)
 * @param yString the index+1 of the card on the board  
 */
class MatchCommand(xString: String, yString: String) extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.handleMatch(xString, yString)
        (newState, this)
    }
}

/**
 * Command to discard a card.
 *
 * @param xString the index+1 of the card in the player's hand (or from stack)
 */
class DiscardCommand(xString: String) extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.handleDiscard(xString)
        (newState, this)
    }
}