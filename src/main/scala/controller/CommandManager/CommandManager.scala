package controller.CommandManager
import controller.Command
import model.GameState

trait CommandManager {
    protected val undoStack: scala.collection.mutable.Stack[Any]
    protected val redoStack: scala.collection.mutable.Stack[Any]
    def executeCommand(command: Command, currentState: GameState): GameState
    def undo(currentState: GameState): GameState
    def redo(currentState: GameState): GameState
}