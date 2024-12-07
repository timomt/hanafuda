package controller

import model.GameState

/**
 * Trait defining Observers and their update method.
 */
trait Observer {
    /**
     * Updates the observer with the new game state.
     *
     * @param gameState the new game state
     */
    def update(gameState: GameState): Unit
}

/**
 * Class defining Observables and their methods.
 */
class Observable {
    /**
     * A Vector of all Observers subscribed to this instance.
     */
    var subscribers: Vector[Observer] = Vector()

    /**
     * Adds an Observer to the Vector of subscribers.
     *
     * @param s the Observer to add
     */
    def add(s: Observer): Unit = subscribers = subscribers :+ s

    /**
     * Removes an Observer from the Vector of subscribers.
     *
     * @param s the Observer to remove
     */
    def remove(s: Observer): Unit = subscribers = subscribers.filterNot(o => o == s)

    /**
     * Notifies all Observers in the Vector of subscribers of the new GameState.
     *
     * @param gameState the new game state
     */
    def notifyObservers(gameState: GameState): Unit = subscribers.foreach(o => o.update(gameState))
}