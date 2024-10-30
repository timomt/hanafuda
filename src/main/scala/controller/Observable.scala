package controller
import model.GameState

/*
* trait Observer
* defines Observers and their update method.
* */
trait Observer {
    def update(gameState: GameState): Unit
}

/*
* class Observable
* defines Observables and their methods.
* */
class Observable {
    /*
    * private var subscribers
    * a Vector of all Observers subscribed to this instance.
    * */
    private var subscribers: Vector[Observer] = Vector()
    
    /*
    * def add(...)
    * adds an Observer to the Vector of subscribers.
    * */
    def add(s:Observer): Unit = subscribers=subscribers:+s
    
    /*
    * def remove(...)
    * removes an Observer from the Vector of subscribers.
    * */
    def remove(s:Observer): Unit = subscribers=subscribers.filterNot(o => o == s)
    
    /*
    * def notifyObservers(...)
    * notifies all Observers in the Vector of subscribers of the new GameState
    * */
    def notifyObservers(gameState: GameState): Unit = subscribers.foreach(o => o.update(gameState))
}