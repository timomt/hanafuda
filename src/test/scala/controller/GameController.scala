import controller.GameController
import org.scalatest.PrivateMethodTester
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Card, CardMonth, CardName, CardType, Deck, GameManager, GameState, MatchType, Player}

class GameControllerSpec extends AnyWordSpec with Matchers with PrivateMethodTester {
    "matchCards" should {
        "return error for negative input" in {
            val matchCards = PrivateMethod[GameState](Symbol("matchCards"))
            val initialState = GameManager.newGame(" ", " ")
            val result = GameController.invokePrivate(matchCards(initialState, "-1", "-5"))
            assert(result.stderr.isDefined)
        }
        "call handleMatchPlanned when matchType is PLANNED" in {

        }
        "call handleMatchRandom when matchType is RANDOM" in {

        }
    }
    "handleMatchRandomMatch" should {
        "return error for invalid match" in {
            val method = PrivateMethod[GameState](Symbol("handleMatchRandomMatch"))
            val initialState = GameManager.newGame(" ", " ").copy(board = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN))),
                queued = Some(Card(CardMonth.MARCH, CardType.HIKARI, CardName.PLAIN)))
            val result = GameController.invokePrivate(method(initialState, 1))
            assert(result.stderr.isDefined)
        }
        "collect a whole month properly" in {

        }
        "execute default match properly" in {
            val method = PrivateMethod[GameState](Symbol("handleMatchRandomMatch"))
            val initialState = GameState(
                players = List(Player(" ", Deck.defaultDeck(), Deck(List.empty), 0),
                    Player(" ", Deck.defaultDeck(), Deck(List.empty), 0)),
                deck = Deck.defaultDeck(),
                board = Deck(List(Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN))),
                matched = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
                    Card(CardMonth.MAY, CardType.HIKARI, CardName.PLAIN))),
                matchType = MatchType.RANDOM,
                queued = Some(Card(CardMonth.MARCH, CardType.HIKARI, CardName.PLAIN)),
                stdout = None,
                stderr = None
            )
            val result = GameController.invokePrivate(method(initialState, 1))
            assert(result.stderr.isEmpty)
            assert(result.stdout.isDefined)
            assert(result.queued.isEmpty)
            assert(result.matchType === MatchType.PLANNED)
            assert(result.matched.cards.isEmpty)
            assert(result.board.cards.isEmpty)
            assert(result.players(1).side.cards === List(
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.PLAIN),
                Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN),
                Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
                Card(CardMonth.MAY, CardType.HIKARI, CardName.PLAIN)
            ))
        }
    }
}