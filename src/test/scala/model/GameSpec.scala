import model.{Card, CardMonth, CardName, CardType, Deck, GameManager, GameStatePlanned, GameStateRandom, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameSpec extends AnyFlatSpec with Matchers {
    /* ------------------------- */
    /* ------ updateGameStateWithError ------ */

    "updateGameStateWithError" should "return a copy with the provided stderr included" in {
        val game = GameStatePlanned(
            players = List(
                Player("Test1", Deck(List.empty), Deck(List.empty), 0),
                Player("Test2", Deck(List.empty), Deck(List.empty), 2)),
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
                        score = 0
                    ),
                    Player(
                        name = "",
                        hand = Deck(List.empty),
                        side = Deck(List.empty),
                        score = 0
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
                    score = 0
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0
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
                    score = 0
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0
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
                    score = 0
                ),
                Player(
                    name = "",
                    hand = Deck(List.empty),
                    side = Deck(List.empty),
                    score = 0
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
                    ), Deck(List.empty), 0),
                Player("", Deck(List.empty), Deck(List.empty), 0)
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
                    ), Deck(List.empty), 0),
                Player("", Deck(List.empty), Deck(List.empty), 0)
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
                    ), Deck(List.empty), 0),
                Player("", Deck(List.empty), Deck(List.empty), 0)
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
                Player("", Deck(List.empty), Deck(List.empty), 0),
                Player("", Deck(List.empty), Deck(List.empty), 0)
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
                Player("", Deck(List.empty), Deck(List.empty), 0),
                Player("", Deck(List.empty), Deck(List.empty), 0)
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
                Player("", Deck(List.empty), Deck(List.empty), 0),
                Player("", Deck(List.empty), Deck(List.empty), 0)
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

    /* ------------------------- */
}