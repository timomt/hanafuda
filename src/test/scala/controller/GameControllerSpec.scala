/*import model.{Card, CardMonth, CardName, CardType, Deck, GameManager, GameStatePlanned, Player}
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

  "processInput" should "print help" in {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      GameController.processInput("help")
    }
    val output = stream.toString
    output should equal(printHelp())
  }
    it should "exit the game" in {
      assertThrows[RuntimeException](GameController.processInput("exit"))
    }
    /*
    it should "print error message" in {
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        GameController.processInput("start")
      }
      val output = stream.toString
      output should equal("[Error]: You submitted a command that requires a started game without starting it correctly.\n")
    }
     */
  it should "print an error message when input is invalid and gameState is empty" in {
    GameController.gameState = None
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      GameController.processInput("invalid command")
    }
    val output = stream.toString
    output should equal("[Error]: You submitted a command that requires a started game without starting it correctly.\n")
  }

  /*
    it should "notify observer (continue)" in {
      val game = GameStatePlanned(
        players = List(
          Player("Test1", Deck(List.empty), Deck(List.empty), 0),
          Player("Test2", Deck(List.empty), Deck(List.empty), 2)
        ),
        deck = Deck.defaultDeck(),
        board = Deck(List.empty),
        stdout = None,
        stderr = None
      )

      GameController.gameState = Some(game)

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        GameController.processInput("continue")
      }
      val output = stream.toString
      output should equal (printBoard(game))
    }

   */

    it should "match the cards" in {

    }
}*/