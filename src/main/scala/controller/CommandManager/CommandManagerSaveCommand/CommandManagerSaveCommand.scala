package controller.CommandManager.CommandManagerSaveCommand

import controller.Command
import controller.CommandManager.CommandManager
import model.GameState

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