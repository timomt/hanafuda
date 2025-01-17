package controller.CommandManager.CommandManagerSaveState

import controller.Command
import controller.CommandManager.CommandManager
import model.GameState

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
