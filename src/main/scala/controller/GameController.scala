package controller

import controller.GameController.notifyObservers
import model.CardMonth.MARCH
import model.CardName.CURTAIN
import model.CardType.HIKARI
import model.{Card, Deck, GameManager, GameState, MatchType, Player}
import view.TUIManager

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
    private var gameState: Option[GameState] = None

    /*
    * def newGame()
    * initializes/overwrites this objects gameState with a default game
    * and notifies observers of the new GameState.
    * */
    private def newGame(firstPlayer: String, secondPlayer: String): Unit = {
        gameState = Some(GameManager.newGame(firstPlayer, secondPlayer))
        notifyObservers(gameState.get)
    }

    /*
    * def processInput(...)
    * processes a String to change the current GameState
    * and notifies observers of the new GameState.
    * TODO: process input and create new GameState
    * */
    def processInput(input: String): Unit = input match {
        case "help" =>
            println(TUIManager.printHelp())

        case "exit" =>
            sys.exit(0)

        case i if gameState.isEmpty =>
            i match {
                case s"start $firstPlayer $secondPlayer" => newGame(firstPlayer, secondPlayer)
                case _ => println("[Error]: You submitted a command that requires a started game without starting it correctly.")
            }

        // All following cases assert gameState is Some
        case "continue" =>
            notifyObservers(gameState.get)

        case s"match $x $y" if x.toInt >= 1 && x.toInt <= 8 && y.toInt >= 1 && y.toInt <= 8 => matchCards(x.toInt, y.toInt)

        case s"match $x" if x.toInt >= 1 && x.toInt <= gameState.get.players.head.hand.cards.size => matchCards(x.toInt, 0)

        case "test colors" =>
            val updatedFirstPlayer = gameState.get.players.head.copy(side = gameState.get.players.head.side.copy(cards = gameState.get.players.head.side.cards :+ Card(MARCH, HIKARI, CURTAIN)))
            val newPlayersList = gameState.get.players.tail :+ updatedFirstPlayer
            //val newPlayersList = updatedFirstPlayer :: gameState.players.tail
            val newState = GameState(newPlayersList, gameState.get.deck, gameState.get.board, gameState.get.matched, MatchType.RANDOM, None, None, None)
            println(TUIManager.printOverview(newState))

        case "combinations" =>
            println(TUIManager.printOverview(gameState.get))

        case _ =>
            println("Invalid input. Please enter in the format: match x y where x and y are integers within the range.")
            notifyObservers(gameState.get)
    }

    /*
    * def matchCards(...)
    * routine to match cards.
    * first Int represents index+1 of current players hand.
    * second Int represents index+1 of current board.
    * */
    private def matchCards(x: Int, y: Int): GameState = {
        if (y == 0) {
            if (gameState.get.queued.isEmpty) {

            } else {

            }

        } else {
            if (gameState.get.queued.isEmpty) {
                if (gameState.get.players.head.hand.cards(x-1).month == gameState.get.board.cards(y-1).month) {
                    val newMatched = Deck(gameState.get.matched.cards.appendedAll(List(gameState.get.board.cards(y-1), gameState.get.players.head.hand.cards(x-1))))
                    val newHand = Deck(gameState.get.players.head.hand.cards.filterNot(_ == gameState.get.players.head.hand.cards(x-1)))//TODO: game board nicht korrekt updated?
                    val (polled, newDeck) = Deck.poll(gameState.get.deck) //TODO: what if polled is None? eg: deck empty?
                    println(gameState.get.board.cards.size)
                    gameState = Some(GameState(List(gameState.get.players.head.copy(hand = newHand), gameState.get.players(1)), newDeck, Deck(gameState.get.board.cards.patch(y-1, Nil, 1)), newMatched, MatchType.RANDOM, Some(polled.get), Some(s"Matched $x with $y"), None))
                    println(gameState.get.board.cards.size)
                    notifyObservers(gameState.get)
                } else {
                    gameState = Some(gameState.get.copy(stderr = Some("You can't match 2 cards of different months.")))
                    notifyObservers(gameState.get)
                }
            } else {
                gameState = Some(gameState.get.copy(stderr = Some("You have to match the card on top of stack: \"match <card on board>\".")))
                notifyObservers(gameState.get)
            }
        }
        gameState.get
    }


    /*
    * TODO: implement def evaluateScore(...)
    *  Evaluates the highest possible score of each player and returns a tuple of the result.
    *  The first tuple value is the score of the player of the current turn (game.players[0])*/
    //def evaluateScore(game: GameState): (Int, Int) = {}*/
}