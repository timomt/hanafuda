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

        //TODO: move error checking into matchCards
        case s"match $x $y" if x.toIntOption.isDefined
                            && y.toIntOption.isDefined
                            && (x.toInt >= 1 && x.toInt <= gameState.get.players.head.hand.cards.size && y.toInt >= 1 && y.toInt <= gameState.get.board.cards.size) => matchCards(x.toInt, y.toInt)

        case s"match $x" if x.toIntOption.isDefined
                         && (x.toInt >= 1 && x.toInt <= gameState.get.players.head.hand.cards.size) => matchCards(x.toInt, 0)

        case "match" => matchCards(0, 0)

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
    * */ //TODO: switch players
    //TODO: edge case both players run out of cards: month ends, eval score
    //TODO: add cards from matched board to library
    private def matchCards(x: Int, y: Int): GameState = {
        if (y == 0) {
            if (gameState.get.matchType == MatchType.PLANNED) {
                val newHand = Deck(gameState.get.players.head.hand.cards.filterNot(_ == gameState.get.players.head.hand.cards(x - 1)))
                val newBoard = Deck(gameState.get.board.cards.appended(gameState.get.players.head.hand.cards(x - 1)))
                gameState = Some(GameState(players=List(gameState.get.players.head.copy(hand = newHand), gameState.get.players(1)), deck=gameState.get.deck, matched=gameState.get.matched, board=newBoard, matchType=MatchType.RANDOM, queued=None, stdout=Some(s"Matched $x with nothing."), stderr=None))
                notifyObservers(gameState.get)
            } else {
                if (x == 0) {
                    val newBoard = Deck(gameState.get.board.cards.appended(gameState.get.queued.get))
                    val updatedPlayer = gameState.get.players.head.copy(side=gameState.get.matched)
                    gameState = Some(GameState(players=List(gameState.get.players(1), updatedPlayer), deck=gameState.get.deck, matched=Deck(List.empty), board=newBoard, matchType=MatchType.PLANNED, queued=None, stdout=Some("Matched drawn card with nothing."), stderr=None))
                    notifyObservers(gameState.get)
                } else {
                    if (gameState.get.board.cards(x-1).month == gameState.get.queued.get.month) {
                        val updatedPlayer = gameState.get.players.head.copy(side=Deck(gameState.get.matched.cards.appendedAll(List(gameState.get.queued.get, gameState.get.board.cards(x-1)))))
                        gameState = Some(GameState(players = List(gameState.get.players(1), updatedPlayer), deck = gameState.get.deck, matched=Deck(List.empty), board=Deck(gameState.get.board.cards.patch(x-1, Nil, 1)), matchType = MatchType.PLANNED, queued = None, stdout = Some(s"Matched drawn card with $x."), stderr = None))
                        notifyObservers(gameState.get)
                    } else {
                        gameState = Some(gameState.get.copy(stderr = Some("You can't match 2 cards of different months."), stdout = None))
                        notifyObservers(gameState.get)
                    }
                }
            }
        } else {
            if (gameState.get.queued.isEmpty && gameState.get.matchType == MatchType.PLANNED) {
                if (gameState.get.players.head.hand.cards(x-1).month == gameState.get.board.cards(y-1).month) {
                    if (gameState.get.deck.cards.isEmpty) {
                        //TODO: evaluate score and end month if deck runs out of cards
                    } else {
                        val newMatched = Deck(gameState.get.matched.cards.appendedAll(List(gameState.get.board.cards(y - 1), gameState.get.players.head.hand.cards(x - 1))))
                        val newHand = Deck(gameState.get.players.head.hand.cards.filterNot(_ == gameState.get.players.head.hand.cards(x - 1)))
                        val (polled, newDeck) = Deck.poll(gameState.get.deck)
                        gameState = Some(GameState(List(gameState.get.players.head.copy(hand = newHand), gameState.get.players(1)), newDeck, Deck(gameState.get.board.cards.patch(y - 1, Nil, 1)), newMatched, MatchType.RANDOM, Some(polled.get), Some(s"Matched $x with $y"), None))
                        notifyObservers(gameState.get)
                    }
                } else {
                    gameState = Some(gameState.get.copy(stderr = Some("You can't match 2 cards of different months."), stdout = None))
                    notifyObservers(gameState.get)
                }
            } else {
                gameState = Some(gameState.get.copy(stderr = Some("You have to match the card on top of stack: \"match <card on board>\"."), stdout = None))
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