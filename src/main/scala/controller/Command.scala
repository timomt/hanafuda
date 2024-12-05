package controller

import model.{DisplayType, GameManager, GameState}

trait Command {
    protected var previousState: Option[GameState] = None
    def execute(currentState: GameState): (GameState, Command)
    def undo(currentState: GameState): GameState = {
        previousState.getOrElse(throw new IllegalStateException("Undo called before execute. Call execute first."))
    }
}

class CommandManager {
    private val undoStack = scala.collection.mutable.Stack[Command]()
    private val redoStack = scala.collection.mutable.Stack[Command]()

    def executeCommand(command: Command, currentState: GameState): GameState = {
        val (newState, executedCommand) = command.execute(currentState)
        undoStack.push(executedCommand)
        redoStack.clear()
        newState
    }

    def undo(currentState: GameState): GameState = {
        if (undoStack.nonEmpty) {
            val command = undoStack.pop()
            val newState = command.undo(currentState)
            redoStack.push(command)
            newState
        } else {
            currentState.updateGameStateWithError("There is nothing to undo.")
        }
    }

    def redo(currentState: GameState): GameState = {
        if (redoStack.nonEmpty) {
            val command = redoStack.pop()
            val (newState, executedCommand) = command.execute(currentState)
            undoStack.push(executedCommand)
            newState
        } else {
            currentState.updateGameStateWithError("There is nothing to redo.")
        }
    }
}

class StartGameCommand(firstPlayer: String, secondPlayer: String) extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = GameManager.newGame(firstPlayer, secondPlayer)
        (newState, this)
    }
}

class HelpCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.updateGameStateWithDisplayType(DisplayType.HELP)
        (newState, this)
    }
}

class CombinationsCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.updateGameStateWithDisplayType(DisplayType.COMBINATIONS)
        (newState, this)
    }
}

class KoiKoiCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = GameManager.koiKoiCallHandler(currentState)
        (newState, this)
    }
}

class FinishCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val (firstS, secS) = GameManager.evaluateScore(currentState.players, 1, 1)
        val newState = GameManager.handleKoiKoi(currentState.players, firstS, secS, board = currentState.board, deck = currentState.deck)
        (newState, this)
    }
}

class ContinueCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.updateGameStateWithDisplayType(DisplayType.GAME)
        (newState, this)
    }
}

class NewCommand extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = GameManager.newGame(currentState.players.head.name, currentState.players(1).name, currentState.players.head.score, currentState.players(1).score)
        (newState, this)
    }
}

class MatchCommand(xString: String, yString: String) extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.handleMatch(xString, yString)
        (newState, this)
    }
}

class DiscardCommand(xString: String) extends Command {
    override def execute(currentState: GameState): (GameState, Command) = {
        previousState = Some(currentState)
        val newState = currentState.handleDiscard(xString)
        (newState, this)
    }
}