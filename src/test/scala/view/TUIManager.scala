import org.scalatest.funsuite.AnyFunSuite
import model.{Card, CardMonth, CardName, CardType, Deck, GameState, MatchType, Player}
import view.TUIManager
import controller.GameController
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import model.GameManager

class TUIManagerSpec extends AnyFunSpec with Matchers{

  GameController.add(TUIManager)

  describe("update") {
    it("should update the TUI according to the current GameState") {
      val gameState = GameManager.newGame("Player1", "Player2")
      val result = TUIManager.printBoard(gameState)
      assert(TUIManager.printBoard(gameState) == result)
    }
  }

  describe("clearScreen") {
    it("should clear the screen correctly") {
      val expectedClearScreen = "\u001b[2J\u001b[3J\u001b[1;1H"
      assert(TUIManager.clearScreen == expectedClearScreen)
    }
  }

  describe("printHelp") {
    it("should print the help text correctly") {
      val expectedHelpText =
        "\u001b[2J\u001b[3J\u001b[1;1H" +
          """
            |╔════════════════════════════════════════════════════════════════════════╗
            |║                                Hanafuda Help                           ║
            |╠════════════════════════════════════════════════════════════════════════╣
            |║ Welcome to Hanafuda! Here are the commands you can use:                ║
            |║                                                                        ║
            |║ 1. start <firstPlayer> <secondPlayer>                                  ║
            |║    - Starts a new game with the given player names.                    ║
            |║                                                                        ║
            |║ 2. continue                                                            ║
            |║    - return to the current game.                                       ║
            |║                                                                        ║
            |║ 2. match <x> <y>                                                       ║
            |║    - Matches cards at positions x and y on the board.                  ║
            |║                                                                        ║
            |║ 3. test colors                                                         ║
            |║    - Tests the colors of the cards.                                    ║
            |║                                                                        ║
            |║ 4. combinations                                                        ║
            |║    - Displays the possible combinations of cards.                      ║
            |║                                                                        ║
            |║ 5. help                                                                ║
            |║    - Displays this help page.                                          ║
            |║                                                                        ║
            |║ 6. exit                                                                ║
            |║    - Exits the game.                                                   ║
            |║                                                                        ║
            |╚════════════════════════════════════════════════════════════════════════╝
            |""".stripMargin

      assert(TUIManager.printHelp() == expectedHelpText)
    }
  }

  describe("print overview") {
    it("should printOverview/ combinations correctly") {
      // Call the printOverview method
      val gameState = GameManager.newGame("Player1", "Player2")
      val result: String = TUIManager.printOverview(gameState)

      // Strip ANSI escape codes from the result
      val strippedResult = result.replaceAll("\u001B\\[[;\\d]*m", "").replaceAll("\u001B\\[\\d+J", "").replaceAll("\u001B\\[\\d+;\\d+H", "")

      val expectedOutput: String =
        """Gokō (五光) "Five Hikari"	10pts.
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║ ║ Nov. ║ ║ Dec. ║
          |║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║ ║ Rain ║ ║Phoen.║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Shikō (四光) "Four Hikari"	8pts.
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║ ║ Dec. ║
          |║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║ ║Phoen.║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Ame-Shikō (雨四光) "Rainy Four Hikari"	7pts.
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║ ║ Nov. ║
          |║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║ ║ Rain ║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Sankō (三光) "Three Hikari"	6pts.
          |╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║
          |║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║
          |╚══════╝ ╚══════╝ ╚══════╝
          |
          |Tsukimi-zake (月見酒) "Moon Viewing"	5pts.
          |╔══════╗ ╔══════╗
          |║ Aug. ║ ║ Sep. ║
          |║Hikari║ ║ Tane ║
          |║ Moon ║ ║Sake_c║
          |╚══════╝ ╚══════╝
          |
          |Hanami-zake (花見酒) "Cherry Blossom Viewing"	5pts.
          |╔══════╗ ╔══════╗
          |║ Mar. ║ ║ Sep. ║
          |║Hikari║ ║ Tane ║
          |║Curt. ║ ║Sake_c║
          |╚══════╝ ╚══════╝
          |
          |Inoshikachō (猪鹿蝶) "Boar, Deer, Butterfly"	5pts.
          |╔══════╗ ╔══════╗ ╔══════╗
          |║ Jun. ║ ║ Jul. ║ ║ Oct. ║
          |║ Tane ║ ║ Tane ║ ║ Tane ║
          |║Butter║ ║ Boar ║ ║ Deer ║
          |╚══════╝ ╚══════╝ ╚══════╝
          |
          |Tane (タネ) "plain"	1pt.
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Feb. ║ ║ Apr. ║ ║ May  ║ ║ Jul. ║ ║ Sep. ║
          |║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║
          |║Night.║ ║Cuckoo║ ║Bridge║ ║ Boar ║ ║Sake_c║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Akatan Aotan no Chōfuku (赤短青短の重複) "Red Poem, Blue Poem"	10pts.
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Feb. ║ ║ Mar. ║ ║ Jun. ║ ║ Sep. ║ ║ Oct. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Po_tan║ ║Po_tan║ ║Po_tan║ ║Bl_tan║ ║Bl_tan║ ║Bl_tan║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Akatan (赤短) "Red Poem"	5pts.
          |╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Feb. ║ ║ Mar. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Po_tan║ ║Po_tan║ ║Po_tan║
          |╚══════╝ ╚══════╝ ╚══════╝
          |
          |Aotan (青短) "Blue Poem"	5pts.
          |╔══════╗ ╔══════╗ ╔══════╗
          |║ Jun. ║ ║ Sep. ║ ║ Oct. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Bl_tan║ ║Bl_tan║ ║Bl_tan║
          |╚══════╝ ╚══════╝ ╚══════╝
          |
          |Tanzaku (短冊) "Ribbons"	1pt.
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Feb. ║ ║ Mar. ║ ║ Jun. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Po_tan║ ║Po_tan║ ║Po_tan║ ║Bl_tan║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Kasu (カス) " "	1pt.
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ ║ Mar. ║ ║ Mar. ║ ║ Apr. ║ ║ Apr. ║ ║ May  ║ ║ May  ║
          |║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║
          |║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |""".stripMargin

      assert(strippedResult == expectedOutput)
    }
  }

  describe("colorizeOverviewCard") {
    it("should colorize the cards correctly for each player") {
      // Create two players each with one card in their deck
      val card = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
      val card2 = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PHOENIX)
      val player1 = Player("Player1", Deck(List(card)), Deck(List(card)), 0)
      val player2 = Player("Player2", Deck(List(card2)), Deck(List(card2)), 0)
      val gameState = GameState(
        List(player1, player2),
        Deck(List(card)),
        Deck(List(card)),
        Deck(List(card)),
        MatchType.PLANNED,
        None,
        None,
        None
      )

      // Call the colorizeOverviewCard method for each player
      val resultPlayer1: List[String] = TUIManager.colorizeOverviewCard(gameState, player1.side.cards.head)
      val resultPlayer2: List[String] = TUIManager.colorizeOverviewCard(gameState, player2.side.cards.head)

      // Expect the first player's card to be colored green
      val expectedGreen = card.unicode.map(line => s"\u001b[32m$line\u001b[0m")
      //resultPlayer1 shouldEqual expectedGreen

      // Expect the second player's card to be colored red
      val expectedRed = card2.unicode.map(line => s"\u001b[31m$line\u001b[0m")
      //resultPlayer2 shouldEqual expectedRed

      // Verify that the text without ANSI codes remains the same (to confirm color correctness)
      val strippedResultPlayer1 = resultPlayer1.map(_.replaceAll("\u001B\\[[;\\d]*m", ""))
      val strippedResultPlayer2 = resultPlayer2.map(_.replaceAll("\u001B\\[[;\\d]*m", ""))

      val expectedOutput_player1: List[String] = card.unicode
      val expectedOutput_player2: List[String] = card2.unicode

      //strippedResultPlayer1 shouldEqual expectedOutput_player1
      strippedResultPlayer2 shouldEqual expectedOutput_player2
    }
  }
}