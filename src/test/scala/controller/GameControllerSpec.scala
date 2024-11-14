import model.{Card, CardMonth, CardName, CardType, Deck, GameManager, GameStatePlanned, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import view.TUIManager.{printBoard, printHelp}
import model.GameState


class GameControllerSpec extends AnyFlatSpec with Matchers {

  "new Game" should "initialize a new game and notify observer" in {
    GameController.newGame("Test1", "Test2")
    GameController.gameState match {
      case Some(GameStatePlanned(players, _, _, _, _)) =>
        players.map(_.name) should contain allOf ("Test1", "Test2")
      case _ => fail("GameState was not initialized correctly")
    }

  }
}
