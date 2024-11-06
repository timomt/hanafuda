import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import view.TUIManager
/*import model.{Card, CardName, CardType, GameState, Player, Board, Side}

class TUIManagerSpec extends AnyFlatSpec with Matchers {

  "TUIManager" should "update the TUI according to the current GameState" in {
    val gameState = GameState(
      players = List(Player("Player1", Side(List.empty)), Player("Player2", Side(List.empty))),
      board = Board(List.empty)
    )
    noException should be thrownBy TUIManager.update(gameState)
  }

  it should "return a String representation of the provided GameState" in {
    val gameState = GameState(
      players = List(Player("Player1", Side(List.empty)), Player("Player2", Side(List.empty))),
      board = Board(List.empty)
    )
    val boardString = TUIManager.printBoard(gameState)
    boardString should include ("Current player: Player1")
  }

  it should "return a String representation of the overview of all (un)collected cards and their value" in {
    val gameState = GameState(
      players = List(Player("Player1", Side(List.empty)), Player("Player2", Side(List.empty))),
      board = Board(List.empty)
    )
    val overviewString = TUIManager.printOverview(gameState)
    overviewString should include ("Hanafuda Overview")
  }

  it should "colorize the unicode representation of a card depending on who owns it" in {
    val card = Card(CardName.RAIN, CardType.HIKARI, List("line1", "line2"))
    val gameState = GameState(
      players = List(Player("Player1", Side(List(card))), Player("Player2", Side(List.empty))),
      board = Board(List.empty)
    )
    val colorizedCard = TUIManager.colorizeOverviewCard(gameState, card)
    colorizedCard.head should include ("\u001b[32mline1\u001b[0m")
  }
}*/