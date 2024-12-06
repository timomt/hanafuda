package model

/**
 * Represents a player in the game.
 *
 * @param name the name of the player
 * @param hand the current hand of the player
 * @param side the player's side deck representing their gathered cards
 * @param score the current highest possible score of combinations
 * @param calledKoiKoi whether the player has already decided to continue the game after scoring a combination
 * @param yakusToIgnore a list of combinations to ignore when checking for a win (preventing double recognition after scoring once)
 */
case class Player(name: String, hand: Deck, side: Deck, score: Int, calledKoiKoi: Boolean, yakusToIgnore: List[Combination])

/**
 * Enum used to differentiate between different scenes, since this game is based on State (GameState).
 * TUI and GUI will be able to display the current state according to this DisplayType.
 */
enum DisplayType {
    case GAME, COMBINATIONS, HELP, SPOILER, SUMMARY
}

/**
 * case class GameState(...)
 * players:= The list of players (size 2), where the player at index 0 is to make a move
 * deck:= The deck of remaining cards to draw
 * board:= The cards currently layed out in the middle
 * stdout:= A String for feedback messages, for example: "discarded card..."
 * stderr:= A String for error messages, for example: "wrong usage..."
 * displayType:= The GameStates DisplayType
 * matchedDeck:= A Deck of matched cards
 *   -> Every turn consists of a planned match (hand with board) and random match (drawn from stack with board),
 *      the cards matched from hand first get put on a separate place on board and are put into the players library after random match.
 * queuedCard:= The Card on top of stack waiting to be matched (random match)
 * handleMatch:= Function to handle a match
 * handleDiscard:= Function to handle a discard
 * updateGameStateWithError:= Used to set stderr
 * updateGameStateWithDisplayType:= Used to set DisplayType
 */
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

/**
 * Represents the state where a player has to match a card from their hand with a card on the board.
 *
 * @param players the list of players
 * @param deck the deck of remaining cards to draw
 * @param board the cards currently laid out in the middle
 * @param displayType the GameState's DisplayType
 * @param stdout a String for feedback messages
 * @param stderr a String for error messages
 */
case class GameStatePlanned(players: List[Player], deck: Deck, board: Deck, displayType: DisplayType = DisplayType.GAME, stdout: Option[String], stderr: Option[String]) extends GameState {
    /*
    * def updateGameStateWithError(..)
    * returns a new GameState with applied stderr.
    * */
    def updateGameStateWithError(errorMessage: String): GameState = {
        this.copy(stdout = None, stderr = Some(errorMessage))
    }

    /*
    * def updateGameStateWithDisplayType(..)
    * returns a new GameState with applied displayType.
    * */
    def updateGameStateWithDisplayType(display: DisplayType): GameState = {
        this.copy(displayType = display)
    }

    /*
    * handleDiscard(..)
    * discards the card referenced by xS from hand to board.
    * xS does NOT represent the index, but rather the number of the card. (index+1)
    * if xS cannot be casted to Int, is out of range,
    * or the card it points to can possibly matched, then return with stderr set accordingly.
    * returns instance of GameStateRandom after successful call.
    * */
    override def handleDiscard(xS: String): GameState = {
        val x = xS.toIntOption.getOrElse(0)
        if (x >= 1 && x <= this.players.head.hand.cards.size) {  // Selected card has to be valid
            /* Discarding a card is only possible if there is no matching card on board */
            if (this.board.cards.exists(c => c.month == this.players.head.hand.cards(x-1).month)) {
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
        } else {    // Discard is not valid since xS is either invalid or out of bounds
            updateGameStateWithError("You have to provide a valid number, see \"help\".")
        }
    }

    /*
    * handleMatch(..)
    * Two cards at number xS (hand) and yS (board) can be matched of their months are equal.
    * If a whole month has been dealt to board and a player matches the last remaining card from hand,
    * therefor completing a whole month, they will receive not only their 2 matched cards but the whole month instead.
    * returns instance of GameStateRandom after successful call.
    * */
    override def handleMatch(xS: String, yS: String): GameState = {
        val x = xS.toIntOption.getOrElse(0)
        val y = yS.toIntOption.getOrElse(0)

        // Check for valid input
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

/**
 * Represents the state where a player has to match a drawn card from the stack with a card on the board.
 *
 * @param players the list of players
 * @param deck the deck of remaining cards to draw
 * @param board the cards currently laid out in the middle
 * @param matched the deck of matched cards
 * @param queued the card on top of the stack waiting to be matched
 * @param displayType the GameState's DisplayType
 * @param stdout a String for feedback messages
 * @param stderr a String for error messages
 */
case class GameStateRandom(players: List[Player], deck: Deck, board: Deck, matched: Deck, queued: Card, displayType: DisplayType = DisplayType.GAME, stdout: Option[String], stderr: Option[String]) extends GameState {
    /*
    * override class specific values since matchedDeck and queuedCard will be used.
    * */
    override def matchedDeck: Option[Deck] = Some(matched)
    override def queuedCard: Option[Card] = Some(queued)

    /*
    * def updateGameStateWithDisplayType(..)
    * returns a new GameState with applied displayType.
    * */
    def updateGameStateWithDisplayType(display: DisplayType): GameState = {
        this.copy(displayType = display)
    }

    /*
    * def updateGameStateWithError(..)
    * returns a new GameState with applied stderr.
    * */
    def updateGameStateWithError(errorMessage: String): GameState = {
        this.copy(stdout = None, stderr = Some(errorMessage), displayType = DisplayType.GAME)
    }

    /*
    * handleDiscard(..)
    * discards the card referenced by xS from stack to board.
    * xS does NOT represent the index, but rather the number of the card. (index+1)
    * if xS cannot be casted to Int, is out of range,
    * or the card it points to can possibly matched, then return with stderr set accordingly.
    * returns instance of GameStatePlanned after successful call.
    * */
    override def handleDiscard(xS: String): GameState = {
        /* Discarding a card is only possible if there is no matching card on board */
        if (this.board.cards.exists(c => c.month == this.queued.month)) {
            updateGameStateWithError("You can not discard a card when it is possible to match, look closer.")
        } else {    // Discard is valid
            val updatedPlayers = List(this.players(1), this.players.head.copy(
                side = Deck(this.players.head.side.cards.appendedAll(this.matched.cards))
            ))
            val updatedBoard = Deck(this.board.cards.appended(this.queued))
            /* Evaluate the score for the current state if either the deck or both players hand are empty */
            if (this.deck.cards.isEmpty
                || updatedPlayers.head.hand.cards.isEmpty && updatedPlayers(1).hand.cards.isEmpty) {
                GameManager.handleKoiKoi(updatedPlayers, updatedPlayers.head.score, updatedPlayers(1).score, board = updatedBoard, deck = this.deck, true)
            } else if (GameManager.playerHasScoredNewYaku(updatedPlayers(1))) {    /* Check if a player has scored a yaku */
                GameManager.koiKoiHandler(this.copy(
                    players = updatedPlayers.reverse,
                    board = updatedBoard
                ))
            } else {    /* Discard as usual */
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
   * handleMatch(..)
   * Two cards at number xS (stack) and yS (board) can be matched of their months are equal.
   * If a whole month has been dealt to board and a player matches the last remaining card from hand,
   * therefor completing a whole month, they will receive not only their 2 matched cards but the whole month instead.
   * returns instance of GameStatePlanned after successful call.
   * */
    override def handleMatch(yS: String, xS: String): GameState = {
        val y = yS.toIntOption.getOrElse(0)

        // Check for valid input
        if (y < 1 || y > this.board.cards.size) {
            updateGameStateWithError("You have to provide a valid number, see \"help\".")
        } else {
            if (this.queued.month == this.board.cards(y - 1).month) { // Valid match
                /* Check if a whole month has been collected */
                if (this.board.cards(y - 1).grouped) {
                    val updatedPlayers = List(this.players(1), this.players.head.copy(
                        side = Deck(this.players.head.side.cards
                            .appended(this.queued)
                            .appendedAll(this.matched.cards)
                            .appendedAll(this.board.cards.filter(c => c.month == this.queued.month)))
                    ))
                    val updatedBoard = Deck(this.board.cards.filterNot(c => c.month == this.queued.month))
                    /* Evaluate the score for the current state if either the deck or both players hand are empty */
                    if (this.deck.cards.isEmpty
                        || updatedPlayers.head.hand.cards.isEmpty && updatedPlayers(1).hand.cards.isEmpty) {
                        GameManager.handleKoiKoi(updatedPlayers, updatedPlayers.head.score, updatedPlayers(1).score, deck = this.deck, board = updatedBoard, true)
                    } else if (GameManager.playerHasScoredNewYaku(updatedPlayers(1))) {    /* Check if a player has scored a yaku */
                        GameManager.koiKoiHandler(this.copy(
                            players = updatedPlayers.reverse,
                            board = updatedBoard
                        ))
                    } else {    /* State after whole month match */
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
                    /* Evaluate the score for the current state if either the deck or both players hand are empty */
                    if (this.deck.cards.isEmpty
                        || updatedPlayers.head.hand.cards.isEmpty && updatedPlayers(1).hand.cards.isEmpty) {
                        GameManager.handleKoiKoi(updatedPlayers, updatedPlayers.head.score, updatedPlayers(1).score, deck = this.deck, board = updatedBoard, true)
                    } else if (GameManager.playerHasScoredNewYaku(updatedPlayers(1))) {      /* Check if a player has scored a yaku */
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
            } else { // Match is not valid
                updateGameStateWithError(s"You can not match cards of different months (${this.queued.month} and ${this.board.cards(y - 1).month}).")
            }
        }
    }
}

/**
 * Represents the state after a player has scored a yaku for the first time and has to decide whether to continue or finish.
 *
 * @param players the list of players
 * @param deck the deck of remaining cards to draw
 * @param board the cards currently laid out in the middle
 * @param displayType the GameState's DisplayType
 * @param stdout a String for feedback messages
 * @param stderr a String for error messages
 */
case class GameStatePendingKoiKoi(players: List[Player], deck: Deck, board: Deck, displayType: DisplayType = DisplayType.GAME, stdout: Option[String], stderr: Option[String]) extends GameState {
    /*
    * def updateGameStateWithDisplayType(..)
    * returns a new GameState with applied displayType.
    * */
    def updateGameStateWithDisplayType(display: DisplayType): GameState = {
        this.copy(displayType = display)
    }

    /*
    * def updateGameStateWithError(..)
    * returns a new GameState with applied stderr.
    **/
    def updateGameStateWithError(errorMessage: String): GameState = {
        this.copy(stdout = None, stderr = Some(errorMessage), displayType = DisplayType.GAME)
    }

    /*
    * def handleDiscard(..)
    * always returns an error in stderr because the state is waiting for a different decision (koi-koi or finish)
    * */
    override def handleDiscard(xS: String): GameState = {
        this.updateGameStateWithError("You have to either call \"koi-koi\" or \"finish\".")
    }

    /*
    * def handleMatch(..)
    * always returns an error in stderr because the state is waiting for a different decision (koi-koi or finish)
    * */
    override def handleMatch(xS: String, yS: String): GameState = {
        this.updateGameStateWithError("You have to either call \"koi-koi\" or \"finish\".")
    }
}

/**
 * Represents the state after a game has been completed.
 *
 * @param players the list of players
 * @param deck the deck of remaining cards to draw
 * @param board the cards currently laid out in the middle
 * @param displayType the GameState's DisplayType
 * @param stdout a String for feedback messages
 * @param stderr a String for error messages
 * @param outOfCardsEnding true if the game ended because the deck ran out of cards
 */
case class GameStateSummary(players: List[Player], deck: Deck, board: Deck, displayType: DisplayType, stdout: Option[String], stderr: Option[String], outOfCardsEnding: Boolean = false) extends GameState {
    override def matchedDeck: Option[Deck] = None
    override def queuedCard: Option[Card] = None

    /*
    * def handleMatch
    * always returns an error because there is nothing to match in a finished game.
    * */
    override def handleMatch(xS: String, yS: String): GameState = updateGameStateWithError("You first have to create a new game, see \"help\".")

    /*
    * def handleDiscard
    * always returns an error because there is nothing to discard in a finished game.
    * */
    override def handleDiscard(xS: String): GameState = updateGameStateWithError("You first have to create a new game, see \"help\".")

    /*
    * def updateGameStateWithError(..)
    * returns a new GameState with applied stderr.
    **/
    override def updateGameStateWithError(errorMessage: String): GameState = this.copy(stdout = None, stderr = Some(errorMessage))

    /*
    * def updateGameStateWithDisplayType(..)
    * returns a new GameState with applied displayType.
    * */
    override def updateGameStateWithDisplayType(display: DisplayType): GameState = this.copy(displayType = display)
}

/**
 * Represents the state of an uninitialized game.
 *
 * @param displayType the GameState's DisplayType
 * @param stderr a String for error messages
 */
case class GameStateUninitialized(displayType: DisplayType, stderr: Option[String]) extends GameState {
    /* All typical GameState values are either empty or None because they first have to be initialized */
    override def players: List[Player] = List.empty
    override def deck: Deck = Deck(List.empty)
    override def board: Deck = Deck(List.empty)
    override def stdout: Option[String] = None

    /*
    * def handleMatch
    * always returns an error because there is nothing to match in an uninitialized game.
    * */
    override def handleMatch(xS: String, yS: String): GameState = {
        this.copy(stderr = Some("Game is not initialized."))
    }

    /*
    * def handleDiscard
    * always returns an error because there is nothing to discard in an uninitialized game.
    * */
    override def handleDiscard(xS: String): GameState = {
        this.copy(stderr = Some("Game is not initialized."))
    }

    /*
    * def updateGameStateWithError(..)
    * returns a new GameState with applied stderr.
    **/
    override def updateGameStateWithError(errorMessage: String): GameState = {
        this.copy(stderr = Some(errorMessage))
    }

    /*
    * def updateGameStateWithDisplayType(..)
    * returns a new GameState with applied displayType.
    * */
    override def updateGameStateWithDisplayType(display: DisplayType): GameState = {
        this.copy(displayType = display)
    }
}