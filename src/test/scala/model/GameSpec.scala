import model.DisplayType.{SPOILER, SUMMARY}
import model.GameManager.GameManager
import model.GameManager.GameManagerDefault.GameManagerDefault
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameStatePendingKoiKoi, GameStatePlanned, GameStateRandom, GameStateSummary, GameStateUninitialized, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameSpec extends AnyFlatSpec with Matchers {
    given gameManager: GameManager = new GameManagerDefault()

    /* ------------------------- */
    /* ------ updateGameStateWithError ------ */
    "updateGameStateWithDisplayType[GameStatePlanned]" should "set displayType attribute correctly" in {
        val game = GameStatePlanned(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None
        )
        assert(game.updateGameStateWithDisplayType(DisplayType.GAME) === game)
        assert(game.updateGameStateWithDisplayType(DisplayType.SUMMARY).displayType === DisplayType.SUMMARY)
        assert(game.updateGameStateWithDisplayType(DisplayType.HELP).displayType === DisplayType.HELP)
        assert(game.updateGameStateWithDisplayType(DisplayType.SPOILER).displayType === DisplayType.SPOILER)
        assert(game.updateGameStateWithDisplayType(DisplayType.COMBINATIONS).displayType === DisplayType.COMBINATIONS)
    }
    "updateGameStateWithError" should "return a copy with the provided stderr included" in {
        val game = GameStatePlanned(
            players = List(
                Player("Test1", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("Test2", Deck(List.empty), Deck(List.empty), 2, calledKoiKoi = false, yakusToIgnore = List.empty)),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            stdout = None,
            stderr = None
        )
        val updatedGame = game.updateGameStateWithError("error")
        assert(updatedGame.stderr.isDefined)
    }

    /* ------------------------- */
    /* ------ handleDiscard ------ */
    "handleDiscard[GameStatePlanned]" should "return error on invalid input" in {
        val game = gameManager.newGame("", "")
        assert(game.handleDiscard("-1").stderr.isDefined)
        assert(game.handleDiscard("faskdaswd").stderr.isDefined)
        assert(game.handleDiscard("10").stderr.isDefined)
    }
    it should "only discard as last resort" in {
        val game = gameManager.newGame("", "")
        assert(game.players.head.hand.cards.forall(c => {
            if (game.board.cards.exists(p => c.month == p.month)) {
                game.handleDiscard(s"${game.players.head.hand.cards.indexOf(c) + 1}").stderr.isDefined
            } else {
                game.handleDiscard(s"${game.players.head.hand.cards.indexOf(c) + 1}").stderr.isEmpty
            }
        }))
    }
    it should "discard properly" in {
        val game = GameStatePlanned(
            players =
                List(
                    Player(yakusToIgnore = List.empty,
                        name = "",
                        hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
                        side = Deck(List.empty),
                        score = 0,
                        calledKoiKoi = false
                    ),
                    Player(yakusToIgnore = List.empty,
                        name = "",
                        hand = Deck(List.empty),
                        side = Deck(List.empty),
                        score = 0, 
                        calledKoiKoi = false
                    )
                )
            ,
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0))),
            stdout = None,
            stderr = None
        )
        val updatedGame = game.handleDiscard("1")
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.players.head.hand.cards.isEmpty)
        assert(updatedGame.board.cards.contains(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0)))
    }
    "handleDiscard[GameStateRandom]" should "return error on invalid input" in {
        val game = GameStateRandom(
            players = List(
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                ),
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
            matched = Deck(List.empty),
            queued = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0),
            stdout = None,
            stderr = None
        )
        assert(game.handleDiscard("-1").stderr.isDefined)
        assert(game.handleDiscard("faskdaswd").stderr.isDefined)
        assert(game.handleDiscard("10").stderr.isDefined)
    }
    it should "only discard as last resort" in {
        val game = GameStateRandom(
            players = List(
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                ),
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))),
            matched = Deck(List.empty),
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0),
            stdout = None,
            stderr = None
        )
        assert(game.handleDiscard("").stderr.isDefined)
    }
    it should "discard properly" in {
        val game = GameStateRandom(
            players = List(
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                ),
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0))),
            matched = Deck(List.empty),
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0),
            stdout = None,
            stderr = None
        )
        assert(game.handleDiscard("").stderr.isEmpty)
        assert(game.handleDiscard("").isInstanceOf[GameStatePlanned])
        assert(game.handleDiscard("").players === game.players.reverse)
        assert(game.handleDiscard("").board.cards === List(
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0),
            Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)))
    }
    it should "check for koi-koi" in {
        val game = GameStateRandom(
            players = List(
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
                    side = Deck(List(
                        Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0)
                    )),
                    score = 0,
                    calledKoiKoi = false
                ),
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0))),
            matched = Deck(List(Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 0))),
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0),
            stdout = None,
            stderr = None
        )
        assert(game.handleDiscard("").isInstanceOf[GameStatePendingKoiKoi])
    }

    /* ------------------------- */
    /* ------ handleMatch ------ */
    "handleMatch[GameStatePlanned]" should "return error for invalid input" in {
        val game = GameStatePlanned(
            players = List(
                Player("",
                    Deck(
                        List(
                            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0),
                            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0)
                        )
                    ), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0))),
            stdout = None,
            stderr = None
        )
        assert(game.handleMatch("dwasd", "1").stderr.isDefined)
        assert(game.handleMatch("1", "dwasd").stderr.isDefined)
        assert(game.handleMatch("-5", "1").stderr.isDefined)
        assert(game.handleMatch("1", "-5").stderr.isDefined)
        assert(game.handleMatch("2", "1").stderr.isDefined)
    }
    it should "match default rules properly" in {
        val game = GameStatePlanned(
            players = List(
                Player("",
                    Deck(
                        List(
                            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0),
                            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0)
                        )
                    ), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0))),
            stdout = None,
            stderr = None
        )
        val updatedGame = game.handleMatch("1", "1")
        assert(updatedGame.isInstanceOf[GameStateRandom])
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.players.head.hand.cards === List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0)))
        assert(updatedGame.board.cards.isEmpty)
        assert(updatedGame.matchedDeck.isDefined && updatedGame.matchedDeck.get.cards === List(
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0)
        ))
        assert(updatedGame.queuedCard.isDefined)
        assert(updatedGame.matchedDeck.isDefined)
    }
    it should "match 3/4 rule properly" in {
        val game = GameStatePlanned(
            players = List(
                Player("",
                    Deck(
                        List(
                            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0),
                            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0)
                        )
                    ), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(
                List(
                    Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true, 0),
                    Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true, 0),
                    Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true, 0)
                )
            ),
            stdout = None,
            stderr = None
        )
        val updatedGame = game.handleMatch("1", "1")
        assert(updatedGame.isInstanceOf[GameStateRandom])
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.players.head.hand.cards === List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0)))
        assert(updatedGame.board.cards.isEmpty)
        assert(updatedGame.matchedDeck.isDefined && updatedGame.matchedDeck.get.cards.isEmpty)
        assert(updatedGame.players.head.side.cards === List(
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true, 0),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true, 0),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true, 0)
        ))
        assert(updatedGame.queuedCard.isDefined)
        assert(updatedGame.matchedDeck.isDefined)
    }
    "handleMatch[GameStateRandom]" should "return error for invalid input" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0),
                Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List.empty),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING, false, 0)
        )
        assert(game.handleMatch("dwasd", "1").stderr.isDefined)
        assert(game.handleMatch("1", "dwasd").stderr.isDefined)
        assert(game.handleMatch("-5", "1").stderr.isDefined)
        assert(game.handleMatch("2", "-5").stderr.isDefined)
    }
    it should "match default rules properly" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck.defaultDeck(), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck.defaultDeck(), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0))),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING, false, 0)
        )
        val updatedGame = game.handleMatch("2", "")
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.isInstanceOf[GameStatePlanned])
        assert(updatedGame.board.cards === List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, false, 0)))
        assert(updatedGame.players(1).side.cards === List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING, false, 0),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0),
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0)
        ))
        assert(updatedGame.queuedCard.isEmpty)
        assert(updatedGame.matchedDeck.isEmpty)
    }
    it should "match 3/4 rule properly" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck.defaultDeck(), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck.defaultDeck(), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0))),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING, false, 0)
        )
        val updatedGame = game.handleMatch("2", "")
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.isInstanceOf[GameStatePlanned])
        assert(updatedGame.board.cards.isEmpty)
        assert(updatedGame.players(1).side.cards === List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING, false, 0),
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
        ))
        assert(updatedGame.queuedCard.isEmpty)
        assert(updatedGame.matchedDeck.isEmpty)
    }
    it should "check for koi-koi in default match" in {
        val game = GameStateRandom(
            players = List(
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                ),
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN, false, 0))),
            matched = Deck(List(Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 0))),
            queued = Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0),
            stdout = None,
            stderr = None
        )
        assert(game.handleMatch("1", "").isInstanceOf[GameStatePendingKoiKoi])
    }
    it should "check for koi-koi in 3/4 rule" in {
        val game = GameStateRandom(
            players = List(
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                ),
                Player(yakusToIgnore = List.empty,
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            matched = Deck(List(Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 0))),
            queued = Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0),
            stdout = None,
            stderr = None
        )
        assert(game.handleMatch("1", "").isInstanceOf[GameStatePendingKoiKoi])
    }
    it should "proceed to summary if deck is empty or both players hands are empty" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck(List.empty),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0))),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING, false, 0)
        )
        assert(game.handleMatch("1", "").isInstanceOf[GameStateSummary])
    }
    "updateGameStateWithDisplayType[GameStateRandom]" should "set displayType attribute correctly" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0))),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING, false, 0)
        )
        assert(game.updateGameStateWithDisplayType(DisplayType.GAME) === game)
        assert(game.updateGameStateWithDisplayType(DisplayType.SUMMARY).displayType === DisplayType.SUMMARY)
        assert(game.updateGameStateWithDisplayType(DisplayType.HELP).displayType === DisplayType.HELP)
        assert(game.updateGameStateWithDisplayType(DisplayType.SPOILER).displayType === DisplayType.SPOILER)
        assert(game.updateGameStateWithDisplayType(DisplayType.COMBINATIONS).displayType === DisplayType.COMBINATIONS)
    }

    /* ------------------------- */
    /* ------ GameStatePendingKoiKoi ------ */
    "updateGameStateWithDisplayType[GameStatePendingKoiKoi]" should "set displayType attribute correctly" in {
        val game = GameStatePendingKoiKoi(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None
        )
        assert(game.updateGameStateWithDisplayType(DisplayType.GAME) === game)
        assert(game.updateGameStateWithDisplayType(DisplayType.SUMMARY).displayType === DisplayType.SUMMARY)
        assert(game.updateGameStateWithDisplayType(DisplayType.HELP).displayType === DisplayType.HELP)
        assert(game.updateGameStateWithDisplayType(DisplayType.SPOILER).displayType === DisplayType.SPOILER)
        assert(game.updateGameStateWithDisplayType(DisplayType.COMBINATIONS).displayType === DisplayType.COMBINATIONS)
    }

    "updateGameStateWithError[GameStatePendingKoiKoi], handleDiscard and handleMatch" should "return Some in stderr" in {
        val game = GameStatePendingKoiKoi(
            players = List.empty, deck = Deck(List.empty), board = Deck(List.empty),
            displayType = SPOILER, stdout = None, stderr = None
        )
        assert(game.updateGameStateWithError("").stderr.isDefined)
        assert(game.handleDiscard("").stderr.isDefined)
        assert(game.handleMatch("", "").stderr.isDefined)
    }

    /* ------------------------- */
    /* --------- GameStateSummary -------- */
    "GameStateSummary" should "initialize regular values to None or empty" in {
        val game = GameStateSummary(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None,
            displayType = DisplayType.SUMMARY
        )
        assert(game.matchedDeck === None)
        assert(game.queuedCard === None)
    }
    "updateGameStateWithDisplayType[GameStateSummary]" should "set displayType attribute correctly" in {
        val game = GameStateSummary(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None,
            displayType = DisplayType.SUMMARY
        )
        assert(game.updateGameStateWithDisplayType(DisplayType.SUMMARY) === game)
        assert(game.updateGameStateWithDisplayType(DisplayType.GAME).displayType === DisplayType.GAME)
        assert(game.updateGameStateWithDisplayType(DisplayType.HELP).displayType === DisplayType.HELP)
        assert(game.updateGameStateWithDisplayType(DisplayType.SPOILER).displayType === DisplayType.SPOILER)
        assert(game.updateGameStateWithDisplayType(DisplayType.COMBINATIONS).displayType === DisplayType.COMBINATIONS)
    }
    "handleMatch[GameStateSummary]" should "return error in stderr" in {
        val game = GameStateSummary(
            players = List(
                Player("", Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0))), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None,
            displayType = DisplayType.SUMMARY
        )
        assert(game.handleMatch("1", "1").stderr.isDefined)
    }
    "handleDiscard[GameStateSummary]" should "return error in stderr" in {
        val game = GameStateSummary(
            players = List(
                Player("", Deck(List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true, 0))), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
            )),
            stdout = None,
            stderr = None,
            displayType = DisplayType.SUMMARY
        )
        assert(game.handleDiscard("1").stderr.isDefined)
    }

    /* ------------------------- */
    /* --------- GameStateUninitialized -------- */
    "GameStateUninitialized" should "initialize regular values to None or empty" in {
        val game = GameStateUninitialized(
            displayType = DisplayType.HELP,
            stderr = None
        )
        assert(game.players === List.empty)
        assert(game.deck === Deck(List.empty))
        assert(game.board === Deck(List.empty))
        assert(game.stdout === None)
    }
    "updateGameStateWithDisplayType[GameStateUninitialized]" should "set displayType attribute correctly" in {
        val game = GameStateUninitialized(
            displayType = DisplayType.HELP,
            stderr = None
        )
        assert(game.updateGameStateWithDisplayType(DisplayType.HELP) === game)
        assert(game.updateGameStateWithDisplayType(DisplayType.GAME).displayType === DisplayType.GAME)
        assert(game.updateGameStateWithDisplayType(DisplayType.SUMMARY).displayType === DisplayType.SUMMARY)
        assert(game.updateGameStateWithDisplayType(DisplayType.SPOILER).displayType === DisplayType.SPOILER)
        assert(game.updateGameStateWithDisplayType(DisplayType.COMBINATIONS).displayType === DisplayType.COMBINATIONS)
    }
    "handleMatch[GameStateUninitialized]" should "return error in stderr" in {
        val game = GameStateUninitialized(
            displayType = DisplayType.HELP,
            stderr = None
        )
        assert(game.handleMatch("1", "1").stderr.isDefined)
    }
    "handleDiscard[GameStateUninitialized]" should "return error in stderr" in {
        val game = GameStateUninitialized(
            displayType = DisplayType.HELP,
            stderr = None
        )
        assert(game.handleDiscard("1").stderr.isDefined)
    }
    /* ------------------------- */
}