import controller.GameController
import org.scalatest.PrivateMethodTester
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Card, Deck, GameManager, GameState, MatchType, Player}

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
}