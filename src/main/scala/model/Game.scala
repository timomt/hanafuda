package model

import model.DisplayType.SUMMARY

/*
* case class Player(...)
* name:= Name of the player
* hand:= The current hand of the player
* side:= The players side Deck representing their gathered cards
* score:= The current highest possible score of combinations
* */
case class Player(name: String, hand: Deck, side: Deck, score: Int, calledKoiKoi: Boolean)

enum DisplayType {
    case GAME, COMBINATIONS, HELP, SPOILER, SUMMARY
}

/*
* case class GameState(...)
* players:= The list of players (size 2), where the player at index 0 is to make a move
* deck:= The deck of remaining cards to draw
* board:= The cards currently layed out in the middle
* matched:= A deck of currently matched cards
* queued:= The Card on top of stack waiting to be matched
* */
trait GameState {
    def players: List[Player]
    def deck: Deck
    def board: Deck
    def stdout: Option[String]
    def stderr: Option[String]
    def displayType: DisplayType
    def matchedDeck: Option[Deck] = None
    def queuedCard: Option[Card] = None
    def handleMatch(xS: String, yS: String): GameState
    def handleDiscard(xS: String): GameState
    def updateGameStateWithError(errorMessage: String): GameState
    def updateGameStateWithDisplayType(display: DisplayType): GameState
}

case class GameStatePlanned(players: List[Player], deck: Deck, board: Deck, displayType: DisplayType = DisplayType.GAME, stdout: Option[String], stderr: Option[String]) extends GameState {
    /*
    * updateGameStateWithError(..)
    * */
    def updateGameStateWithError(errorMessage: String): GameState = {
        this.copy(stdout = None, stderr = Some(errorMessage))
    }

    def updateGameStateWithDisplayType(display: DisplayType): GameState = {
        this.copy(displayType = display)
    }

    /*
    * handleDiscard(..)
    * */
    override def handleDiscard(xS: String): GameState = {
        val x = xS.toIntOption.getOrElse(0)
        if (x >= 1 && x <= this.players.head.hand.cards.size) {  // Selected card has to be valid
            if (this.board.cards.exists(c => c.month == this.players.head.hand.cards(x-1).month)) {     // Can only discard as last resort
                updateGameStateWithError("You can not discard a card when it is possible to match, look closer.")
            } else {    // Discard is valid
                val updatedPlayer = this.players.head.copy(hand = Deck(this.players.head.hand.cards.patch(x-1, Nil, 1)))
                val updatedBoard = Deck(this.board.cards.appended(this.players.head.hand.cards(x-1)))
                val (updatedQueued, updatedDeck) = Deck.poll(this.deck)
                GameStateRandom(
                    players = List(updatedPlayer, this.players(1)),
                    board = updatedBoard,
                    deck = updatedDeck,
                    stderr = None,
                    queued = updatedQueued.get,
                    matched = Deck(List.empty),
                    stdout = Some(s"Discarded card $x."),
                    displayType = DisplayType.GAME
                )
            }
        } else {
            updateGameStateWithError("You have to provide a valid number, see \"help\".")
        }
    }

    /*
    * handleMatch(..)
    * */
    override def handleMatch(xS: String, yS: String): GameState = {
        val x = xS.toIntOption.getOrElse(0)
        val y = yS.toIntOption.getOrElse(0)

        // Only accept non negative values
        if (x < 1 || y < 1 || y > this.board.cards.size || x > this.players.head.hand.cards.size) {
            updateGameStateWithError("You have to provide valid numbers, see \"help\".")
        } else {
            if (this.players.head.hand.cards(x - 1).month == this.board.cards(y - 1).month) { // Match is valid
                val (updatedQueued, updatedDeck) = Deck.poll(this.deck)
                if (this.board.cards(y - 1).grouped) { // Collect a whole month
                    val updatedPlayers = List(this.players.head.copy(
                        hand = Deck(this.players.head.hand.cards.patch(x - 1, Nil, 1)),
                        side = Deck(List(this.players.head.hand.cards(x - 1)).appendedAll(this.board.cards.filter(c => c.month == this.players.head.hand.cards(x - 1).month)))
                    ), this.players(1))
                    GameStateRandom(
                        players = updatedPlayers,
                        board = Deck(this.board.cards.filterNot(c => c.month == this.players.head.hand.cards(x - 1).month)),
                        stdout = Some(s"Matched a whole month (${this.players.head.hand.cards(x - 1).month})."),
                        stderr = None,
                        deck = updatedDeck,
                        matched = Deck(List.empty),
                        queued = updatedQueued.get,
                        displayType = DisplayType.GAME
                    )
                } else { // Default match
                    val updatedMatched = Deck(List(this.players.head.hand.cards(x - 1), this.board.cards(y - 1)))
                    val updatedBoard = Deck(this.board.cards.patch(y - 1, Nil, 1))
                    val updatedPlayers = List(this.players.head.copy(hand = Deck(this.players.head.hand.cards.patch(x - 1, Nil, 1))), this.players(1))
                    GameStateRandom(
                        players = updatedPlayers,
                        board = updatedBoard,
                        matched = updatedMatched,
                        stdout = Some(s"Matched $x with $y."),
                        stderr = None,
                        deck = updatedDeck,
                        queued = updatedQueued.get,
                        displayType = DisplayType.GAME
                    )
                }
            } else { // Match is not valid (different months)
                updateGameStateWithError(s"You can not match cards of different months (${this.players.head.hand.cards(x - 1).month} and ${this.board.cards(y - 1).month}).")
            }
        }
    }
}

case class GameStateRandom(players: List[Player], deck: Deck, board: Deck, matched: Deck, queued: Card, displayType: DisplayType = DisplayType.GAME, stdout: Option[String], stderr: Option[String]) extends GameState {
    /*
    * override class specific values
    * */
    override def matchedDeck: Option[Deck] = Some(matched)
    override def queuedCard: Option[Card] = Some(queued)

    def updateGameStateWithDisplayType(display: DisplayType): GameState = {
        this.copy(displayType = display)
    }

    /*
    * updateGameStateWithError(..)
    * */
    def updateGameStateWithError(errorMessage: String): GameState = {
        this.copy(stdout = None, stderr = Some(errorMessage), displayType = DisplayType.GAME)
    }
    
    /*
    * def handleDiscard(..)
    * */
    override def handleDiscard(xS: String): GameState = {
        if (this.board.cards.exists(c => c.month == this.queued.month)) {     // Can only discard as last resort
            updateGameStateWithError("You can not discard a card when it is possible to match, look closer.")
        } else {    // Discard card
            val updatedPlayers = List(this.players(1), this.players.head.copy(
                side = Deck(this.players.head.side.cards.appendedAll(this.matched.cards))
            ))
            val updatedBoard = Deck(this.board.cards.appended(this.queued))
            if (this.deck.cards.isEmpty
                || updatedPlayers.head.hand.cards.isEmpty && updatedPlayers(1).hand.cards.isEmpty) {    // empty check
                GameManager.handleKoiKoi(updatedPlayers, updatedPlayers.head.score, updatedPlayers(1).score, board = updatedBoard, deck = this.deck, true)
            } else if (yakuCombinations.exists(_.evaluate(updatedPlayers(1)) > 0)) {     // koi-koi check
                GameManager.koiKoiHandler(this.copy(
                    players = updatedPlayers.reverse,
                    board = updatedBoard
                ))
            } else {
                GameStatePlanned(
                    deck = this.deck,
                    players = updatedPlayers,
                    board = updatedBoard,
                    stdout = Some("Discarded drawn card."),
                    stderr = None,
                    displayType = DisplayType.SPOILER
                )
            }
        }
    }

    /*
    * def handleMatch(..)
    * */
    override def handleMatch(yS: String, xS: String): GameState = {
        val y = yS.toIntOption.getOrElse(0)

        // Only accept non negative values
        if (y < 1 || y > this.board.cards.size) {
            updateGameStateWithError("You have to provide a valid number, see \"help\".")
        } else {
            if (this.queued.month == this.board.cards(y - 1).month) { // Valid match
                if (this.board.cards(y - 1).grouped) { // Collect a whole month
                    val updatedPlayers = List(this.players(1), this.players.head.copy(
                        side = Deck(this.players.head.side.cards
                            .appended(this.queued)
                            .appendedAll(this.matched.cards)
                            .appendedAll(this.board.cards.filter(c => c.month == this.queued.month)))
                    ))
                    val updatedBoard = Deck(this.board.cards.filterNot(c => c.month == this.queued.month))
                    if (this.deck.cards.isEmpty
                        || updatedPlayers.head.hand.cards.isEmpty && updatedPlayers(1).hand.cards.isEmpty) {    // empty check
                        GameManager.handleKoiKoi(updatedPlayers, updatedPlayers.head.score, updatedPlayers(1).score, deck = this.deck, board = updatedBoard, true)
                    } else if (yakuCombinations.exists(_.evaluate(updatedPlayers(1)) > 0)) {     // koi-koi check
                        GameManager.koiKoiHandler(this.copy(
                            players = updatedPlayers.reverse,
                            board = updatedBoard
                        ))
                    } else {
                        GameStatePlanned(
                            deck = this.deck,
                            players = updatedPlayers,
                            board = updatedBoard,
                            stdout = Some(s"Matched a whole month (${this.queued.month})."),
                            stderr = None,
                            displayType = DisplayType.SPOILER
                        )
                    }
                } else { // Default match
                    val updatedPlayers = List(this.players(1), this.players.head.copy(
                        side = Deck(this.players.head.side.cards
                            .appendedAll(List(this.queued, this.board.cards(y - 1)))
                            .appendedAll(this.matched.cards))
                    ))
                    val updatedBoard = Deck(this.board.cards.patch(y - 1, Nil, 1))
                    if (this.deck.cards.isEmpty
                        || updatedPlayers.head.hand.cards.isEmpty && updatedPlayers(1).hand.cards.isEmpty) {    // empty check
                        GameManager.handleKoiKoi(updatedPlayers, updatedPlayers.head.score, updatedPlayers(1).score, deck = this.deck, board = updatedBoard, true)
                    } else if (yakuCombinations.exists(_.evaluate(updatedPlayers(1)) > 0)) {     // koi-koi check
                        GameManager.koiKoiHandler(this.copy(
                            players = updatedPlayers.reverse,
                            board = updatedBoard
                        ))
                    } else {
                        GameStatePlanned(
                            deck = this.deck,
                            players = updatedPlayers,
                            board = updatedBoard,
                            stdout = Some(s"Matched drawn card with $y."),
                            stderr = None,
                            displayType = DisplayType.SPOILER
                        )
                    }
                }
            } else { // Match is not valid (different months)
                updateGameStateWithError(s"You can not match cards of different months (${this.queued.month} and ${this.board.cards(y - 1).month}).")
            }
        }
    }
}

case class GameStatePendingKoiKoi(players: List[Player], deck: Deck, board: Deck, displayType: DisplayType = DisplayType.GAME, stdout: Option[String], stderr: Option[String]) extends GameState {
    def updateGameStateWithDisplayType(display: DisplayType): GameState = {
        this.copy(displayType = display)
    }
    
    def updateGameStateWithError(errorMessage: String): GameState = {
        this.copy(stdout = None, stderr = Some(errorMessage), displayType = DisplayType.GAME)
    }

    override def handleDiscard(xS: String): GameState = {
        this.updateGameStateWithError("You have to either call \"koi-koi\" or \"finish\".")
    }

    override def handleMatch(xS: String, yS: String): GameState = {
        this.updateGameStateWithError("You have to either call \"koi-koi\" or \"finish\".")
    }
}

case class GameStateSummary(players: List[Player], deck: Deck, board: Deck, displayType: DisplayType, stdout: Option[String], stderr: Option[String], outOfCardsEnding: Boolean = false) extends GameState {
    override def matchedDeck: Option[Deck] = None
    override def queuedCard: Option[Card] = None
    override def handleMatch(xS: String, yS: String): GameState = updateGameStateWithError("You first have to create a new game, see \"help\".")
    override def handleDiscard(xS: String): GameState = updateGameStateWithError("You first have to create a new game, see \"help\".")
    override def updateGameStateWithError(errorMessage: String): GameState = this.copy(stdout = None, stderr = Some(errorMessage))
    override def updateGameStateWithDisplayType(display: DisplayType): GameState = this.copy(displayType = display)
}