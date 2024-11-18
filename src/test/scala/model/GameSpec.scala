import model.DisplayType.{SPOILER, SUMMARY}
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameManager, GameStatePendingKoiKoi, GameStatePlanned, GameStateRandom, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameSpec extends AnyFlatSpec with Matchers {
    /* ------------------------- */
    /* ------ updateGameStateWithError ------ */
    "updateGameStateWithDisplayType[GameStatePlanned]" should "set displayType attribute correctly" in {
        val game = GameStatePlanned(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true)
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
                Player("Test1", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false),
                Player("Test2", Deck(List.empty), Deck(List.empty), 2, calledKoiKoi = false)),
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
        val game = GameManager.newGame("", "")
        assert(game.handleDiscard("-1").stderr.isDefined)
        assert(game.handleDiscard("faskdaswd").stderr.isDefined)
        assert(game.handleDiscard("10").stderr.isDefined)
    }
    it should "only discard as last resort" in {
        val game = GameManager.newGame("", "")
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
                    Player(
                        name = "",
                        hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN))),
                        side = Deck(List.empty),
                        score = 0,
                        calledKoiKoi = false
                    ),
                    Player(
                        name = "",
                        hand = Deck(List.empty),
                        side = Deck(List.empty),
                        score = 0, 
                        calledKoiKoi = false
                    )
                )
            ,
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN))),
            stdout = None,
            stderr = None
        )
        val updatedGame = game.handleDiscard("1")
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.players.head.hand.cards.isEmpty)
        assert(updatedGame.board.cards.contains(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN)))
    }

    "handleDiscard[GameStateRandom]" should "return error on invalid input" in {
        val game = GameStateRandom(
            players = List(
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN))),
            matched = Deck(List.empty),
            queued = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN),
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
                Player(
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN))),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))),
            matched = Deck(List.empty),
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
            stdout = None,
            stderr = None
        )
        assert(game.handleDiscard("").stderr.isDefined)
    }
    it should "discard properly" in {
        val game = GameStateRandom(
            players = List(
                Player(
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN))),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0, 
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN))),
            matched = Deck(List.empty),
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
            stdout = None,
            stderr = None
        )
        assert(game.handleDiscard("").stderr.isEmpty)
        assert(game.handleDiscard("").isInstanceOf[GameStatePlanned])
        assert(game.handleDiscard("").players === game.players.reverse)
        assert(game.handleDiscard("").board.cards === List(
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN),
            Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)))
    }

    it should "check for koi-koi" in {
        val game = GameStateRandom(
            players = List(
                Player(
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN))),
                    side = Deck(List(
                        Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON)
                    )),
                    score = 0,
                    calledKoiKoi = false
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN))),
            matched = Deck(List(Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP))),
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
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
                            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN),
                            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN)
                        )
                    ), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN))),
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
                            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN),
                            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN)
                        )
                    ), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN))),
            stdout = None,
            stderr = None
        )
        val updatedGame = game.handleMatch("1", "1")
        assert(updatedGame.isInstanceOf[GameStateRandom])
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.players.head.hand.cards === List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN)))
        assert(updatedGame.board.cards.isEmpty)
        assert(updatedGame.matchedDeck.isDefined && updatedGame.matchedDeck.get.cards === List(
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN)
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
                            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN),
                            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN)
                        )
                    ), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(
                List(
                    Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true),
                    Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true),
                    Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true)
                )
            ),
            stdout = None,
            stderr = None
        )
        val updatedGame = game.handleMatch("1", "1")
        assert(updatedGame.isInstanceOf[GameStateRandom])
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.players.head.hand.cards === List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN)))
        assert(updatedGame.board.cards.isEmpty)
        assert(updatedGame.matchedDeck.isDefined && updatedGame.matchedDeck.get.cards.isEmpty)
        assert(updatedGame.players.head.side.cards === List(
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true),
            Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN, true)
        ))
        assert(updatedGame.queuedCard.isDefined)
        assert(updatedGame.matchedDeck.isDefined)
    }

    "handleMatch[GameStateRandom]" should "return error for invalid input" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN),
                Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List.empty),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING)
        )
        assert(game.handleMatch("dwasd", "1").stderr.isDefined)
        assert(game.handleMatch("1", "dwasd").stderr.isDefined)
        assert(game.handleMatch("-5", "1").stderr.isDefined)
        assert(game.handleMatch("2", "-5").stderr.isDefined)
    }
    it should "match default rules properly" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN))),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING)
        )
        val updatedGame = game.handleMatch("2", "")
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.isInstanceOf[GameStatePlanned])
        assert(updatedGame.board.cards === List(Card(CardMonth.JULY, CardType.TANE, CardName.PLAIN)))
        assert(updatedGame.players(1).side.cards === List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN),
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN)
        ))
        assert(updatedGame.queuedCard.isEmpty)
        assert(updatedGame.matchedDeck.isEmpty)
    }
    it should "match 3/4 rule properly" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN))),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING)
        )
        val updatedGame = game.handleMatch("2", "")
        assert(updatedGame.stderr.isEmpty)
        assert(updatedGame.isInstanceOf[GameStatePlanned])
        assert(updatedGame.board.cards.isEmpty)
        assert(updatedGame.players(1).side.cards === List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING),
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
            Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true)
        ))
        assert(updatedGame.queuedCard.isEmpty)
        assert(updatedGame.matchedDeck.isEmpty)
    }
    it should "check for koi-koi in default match" in {
        val game = GameStateRandom(
            players = List(
                Player(
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN))),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN))),
            matched = Deck(List(Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP))),
            queued = Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            stdout = None,
            stderr = None
        )
        assert(game.handleMatch("1", "").isInstanceOf[GameStatePendingKoiKoi])
    }
    it should "check for koi-koi in 3/4 rule" in {
        val game = GameStateRandom(
            players = List(
                Player(
                    name = "",
                    hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN))),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0,
                    calledKoiKoi = false
                )
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.AUGUST, CardType.TANE, CardName.PLAIN, true)
            )),
            matched = Deck(List(Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP))),
            queued = Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            stdout = None,
            stderr = None
        )
        assert(game.handleMatch("1", "").isInstanceOf[GameStatePendingKoiKoi])
    }

    "updateGameStateWithDisplayType[GameStateRandom]" should "set displayType attribute correctly" in {
        val game = GameStateRandom(
            players = List(
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true)
            )),
            stdout = None,
            stderr = None,
            matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN))),
            queued = Card(CardMonth.MARCH, CardType.HIKARI, CardName.LIGHTNING)
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
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false),
                Player("", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true),
                Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true)
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
}