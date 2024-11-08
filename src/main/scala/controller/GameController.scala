package controller

import controller.GameController.notifyObservers
import model.CardMonth.MARCH
import model.CardName.CURTAIN
import model.CardType.HIKARI
import model.MatchType.PLANNED
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
            print(TUIManager.printHelp())

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

        case s"match $x $y" =>
            gameState = Some(matchCards(gameState.get, x, y))
            notifyObservers(gameState.get)

        case s"match $x" =>
            gameState = Some(matchCards(gameState.get, x, "0"))
            notifyObservers(gameState.get)

        case "match" =>
            gameState = Some(matchCards(gameState.get, "0", "0"))
            notifyObservers(gameState.get)

        case "new" => {
            gameState = Some(GameManager.newGame(gameState.get.players.head.name, gameState.get.players(1).name))
            notifyObservers(gameState.get)
        }

        case "test colors" =>
            val updatedFirstPlayer = gameState.get.players.head.copy(side = gameState.get.players.head.side.copy(cards = gameState.get.players.head.side.cards :+ Card(MARCH, HIKARI, CURTAIN, false)))
            val newPlayersList = gameState.get.players.tail :+ updatedFirstPlayer
            //val newPlayersList = updatedFirstPlayer :: gameState.players.tail
            val newState = GameState(newPlayersList, gameState.get.deck, gameState.get.board, gameState.get.matched, MatchType.RANDOM, None, None, None)
            println(TUIManager.printOverview(newState))

        case "combinations" =>
            println(TUIManager.printOverview(gameState.get))

        case _ =>
            notifyObservers(gameState.get)
    }

    /* --------------------------------------- */

    /* --- Matching logic ---*/

    /*
    * matchCards()
    * */ //TODO: check for empy decks
         //TODO: check for koi-koi
    private def matchCards(game:GameState, xString: String, yString: String): GameState = {
        val x = xString.toIntOption.getOrElse(0)
        val y = yString.toIntOption.getOrElse(0)

        // Only accept non negative values
        if (x < 0 || y < 0) {
           updateGameStateWithError(game, "You have to provide positive numbers, see \"help\".")
        } else {
            game.matchType match {
                case MatchType.PLANNED => handleMatchPlanned(game, x, y)
                case MatchType.RANDOM => handleMatchRandom(game, x)
            }
        }
    }

    /* --------------------------------------- */

    /* --- Planned matching --- */

    /*
    * handleMatchPlanned(..)
    * */
    private def handleMatchPlanned(game: GameState, x: Int, y: Int): GameState = {
        if (x == 0) {   // Auto discard is only possible in a random match
            updateGameStateWithError(game, "You have to specify which card to discard/match, see \"help\".")
        } else {
            if (y == 0 && x <= game.players.head.hand.cards.size) {     // Discard selected card
                handleMatchPlannedDiscard(game, x)
            } else if (y <= game.board.cards.size && x <= game.players.head.hand.cards.size) {  // Match x with y
                handleMatchPlannedMatch(game, x, y)
            } else {    // Index out of bounds
                updateGameStateWithError(game, "You have to specify correct numbers, see \"help\".")
            }
        }
    }

    /*
    * updateGameStateWithError(..)
    * */
    private def updateGameStateWithError(game: GameState, errorMessage: String): GameState = {
        game.copy(stdout = None, stderr = Some(errorMessage))
    }

    /*
    * handleMatchPlannedDiscard(..)
    * */
    private def handleMatchPlannedDiscard(game: GameState, x: Int): GameState = {
        if (game.board.cards.exists(c => c.month == game.players.head.hand.cards(x-1).month)) {     // Can only discard as last resort
            updateGameStateWithError(game, "You can not discard a card when it is possible to match, look closer.")
        } else {
            val updatedPlayer = game.players.head.copy(hand = Deck(game.players.head.hand.cards.patch(x-1, Nil, 1)))
            val updatedBoard = Deck(game.board.cards.appended(game.players.head.hand.cards(x-1)))
            val (updatedQueued, updatedDeck) = Deck.poll(game.deck)
            game.copy(
                players = List(updatedPlayer, game.players(1)),
                board = updatedBoard,
                queued = updatedQueued,
                deck = updatedDeck,
                stderr = None,
                matchType = MatchType.RANDOM,
                stdout = Some(s"Discarded card $x.")
            )
        }
    }

    /*
    * handleMatchPlannedMatch(..)
    * */
    private def handleMatchPlannedMatch(game: GameState, x: Int, y: Int): GameState = {
        if (game.players.head.hand.cards(x-1).month == game.board.cards(y-1).month) {   // Match is valid
            val (updatedQueued, updatedDeck) = Deck.poll(game.deck)
            if (game.board.cards(y-1).grouped) {    // Collect a whole month
                val updatedPlayers = List(game.players.head.copy(
                    hand = Deck(game.players.head.hand.cards.patch(x-1, Nil, 0)),
                    side = Deck(List(game.players.head.hand.cards(x-1)).appendedAll(game.board.cards.filter(c => c.month == game.players.head.hand.cards(x-1).month)))
                ), game.players(1))
                game.copy(
                    players = updatedPlayers,
                    board = Deck(game.board.cards.filterNot(c => c.month == game.players.head.hand.cards(x-1))),
                    stdout = Some(s"Matched a whole month (${game.players.head.hand.cards(x-1).month})."),
                    stderr = None,
                    deck = updatedDeck,
                    queued = updatedQueued,
                    matchType = MatchType.RANDOM
                )
            } else {    // Default match
                val updatedMatched = Deck(List(game.players.head.hand.cards(x-1), game.board.cards(y-1)))
                val updatedBoard = Deck(game.board.cards.patch(y-1, Nil, 1))
                val updatedPlayers = List(game.players.head.copy(hand = Deck(game.players.head.hand.cards.patch(x-1, Nil, 1))), game.players(1))
                game.copy(
                    players = updatedPlayers,
                    board = updatedBoard,
                    matched = updatedMatched,
                    stdout = Some(s"Matched $x with $y."),
                    stderr = None,
                    deck = updatedDeck,
                    queued = updatedQueued,
                    matchType = MatchType.RANDOM
                )
            }
        } else {    // Match is not valid (different months)
            updateGameStateWithError(game, s"You can not match cards of different months (${game.players.head.hand.cards(x-1).month} and ${game.board.cards(y-1).month}).")
        }
    }

    /* --------------------------------------- */

    /* --- Random matching --- */

    /*
    * handleMatchRandom(..)
    * */
    private def handleMatchRandom(game: GameState, y: Int): GameState = {
        if (y == 0) {   // Discard queued card
            handleMatchRandomDiscard(game)
        } else if (y <= game.board.cards.size) {    // Match queued card with board
            handleMatchRandomMatch(game, y)
        } else {    // Index out of bounds
            updateGameStateWithError(game, s"The number you are trying to match is out of bounds, see \"help\".")
        }
    }

    /*
    * handleMatchRandomDiscard(..)
    * */
    private def handleMatchRandomDiscard(game: GameState): GameState = {
        if (game.board.cards.exists(c => c.month == game.queued.get.month)) {     // Can only discard as last resort
            updateGameStateWithError(game, "You can not discard a card when it is possible to match, look closer.")
        } else {    // Discard card
            val updatedPlayers = List(game.players(1), game.players.head.copy(
                side = Deck(game.players.head.side.cards.appended(game.queued.get).appendedAll(game.matched.cards))
            ))
            game.copy(
               players = updatedPlayers,
               board = Deck(game.board.cards.appended(game.queued.get)),
               matched = Deck(List.empty),
               queued = None,
               matchType = MatchType.PLANNED,
               stdout = Some("Discarded drawn card."),
               stderr = None
            )
        }
    }


    /*
    * handleMatchRandomMatch(..)
    * */
    private def handleMatchRandomMatch(game: GameState, y: Int): GameState = {
        if (game.queued.get.month == game.board.cards(y-1).month) {     // Valid match
            if (game.board.cards(y-1).grouped) { // Collect a whole month
                val updatedPlayers = List(game.players(1), game.players.head.copy(
                    side = Deck(game.players.head.side.cards
                        .appended(game.queued.get)
                        .appendedAll(game.matched.cards)
                        .appendedAll(game.board.cards.filter(c => c.month == game.queued.get.month)))
                ))
                game.copy(
                    players = updatedPlayers,
                    board = Deck(game.board.cards.filterNot(c => c.month == game.queued.get.month)),
                    matched = Deck(List.empty),
                    queued = None,
                    matchType = PLANNED,
                    stdout = Some(s"Matched a whole month (${game.queued.get.month})."),
                    stderr = None
                )
            } else {    // Default match
                val updatedPlayers = List(game.players(1), game.players.head.copy(
                    side = Deck(game.players.head.side.cards
                        .appendedAll(List(game.queued.get, game.board.cards(y-1)))
                        .appendedAll(game.matched.cards))
                ))
                game.copy(
                    players = updatedPlayers,
                    board = Deck(game.board.cards.patch(y-1, Nil, 1)),
                    matched = Deck(List.empty),
                    queued = None,
                    matchType = MatchType.PLANNED,
                    stdout = Some(s"Matched drawn card with $y."),
                    stderr = None
                )
            }
        } else {    // Match is not valid (different months)
            updateGameStateWithError(game, s"You can not match cards of different months (${game.queued.get.month} and ${game.board.cards(y-1).month}).")
        }
    }

    /* --------------------------------------- */

    /*
    * TODO: implement def evaluateScore(...)
    *  Evaluates the highest possible score of each player and returns a tuple of the result.
    *  The first tuple value is the score of the player of the current turn (game.players[0])*/
    //def evaluateScore(game: GameState): (Int, Int) = {}*/
}