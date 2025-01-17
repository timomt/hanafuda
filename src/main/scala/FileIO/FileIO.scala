package FileIO

import model.GameState

trait FileIO {
    def save(gameState: GameState): Boolean
    def load: GameState
}