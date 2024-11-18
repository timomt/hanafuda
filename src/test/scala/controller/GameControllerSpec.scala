/*import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import model.{DisplayType, GameManager, GameState}

class GameControllerSpec extends AnyFlatSpec with Matchers {
//TODO: rewrite to make it not random


  "A GameController" should "initialize a new game" in {
    GameController.newGame("Player1", "Player2")
    GameController.gameState.isDefined should be(true)
  }

  it should "process input 'help'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("help")
    GameController.gameState.get.displayType should be(DisplayType.HELP)
  }

  /*
  it should "process input 'exit'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("exit")
  }
   */

  it should "process input 'start Player1 Player2'" in {
    GameController.processInput("start Player1 Player2")
    GameController.gameState.isDefined should be(true)
  }

  it should "process input 'continue'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("continue")
    GameController.gameState.get.displayType should be(DisplayType.GAME)
  }

  it should "process input 'match 1 2'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("match 1 2")
    GameController.gameState.get.players.head.hand.cards should have size 8
    GameController.gameState.get.players(1).hand.cards should have size 8
  }

  it should "process input 'match 1'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("match 1")
    GameController.gameState.get.players.head.hand.cards should have size 8
    GameController.gameState.get.players(1).hand.cards should have size 8
  }

  it should "process input 'discard 1'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("discard 1")
    GameController.gameState.get.players.head.hand.cards should have size 8
  }

  it should "process input 'discard'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("discard")
    GameController.gameState.get.players.head.hand.cards should have size 8
  }

  it should "process input 'new'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("new")
    //GameController.gameState.isDefined should be(true)
    GameController.gameState.get.players.map(_.name) should contain allOf("Player1", "Player2")
  }

  it should "process input 'combinations'" in {
    GameController.newGame("Player1", "Player2")
    GameController.processInput("combinations")
    GameController.gameState.get.displayType should be(DisplayType.COMBINATIONS)
  }
}*/