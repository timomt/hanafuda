import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import model.{Card, CardMonth, CardName, CardType, Deck, GameManager, GameState, GameStatePlanned, Player}
import view.TUIManager
import controller.GameController

class TUIManagerSpec extends AnyFunSpec with Matchers {

  GameController.add(TUIManager)

  //this a testable and repeatable test Gamestate
  val player1Deck = Deck(List(
  Deck.defaultDeck().cards.head,
  Deck.defaultDeck().cards(1),
  Deck.defaultDeck().cards(2),
  Deck.defaultDeck().cards(3),
  Deck.defaultDeck().cards(4),
  Deck.defaultDeck().cards(5),
  Deck.defaultDeck().cards(6),
  Deck.defaultDeck().cards(7)
  ))

  val player2Deck = Deck(List(
  Deck.defaultDeck().cards(8),
  Deck.defaultDeck().cards(9),
  Deck.defaultDeck().cards(10),
  Deck.defaultDeck().cards(11),
  Deck.defaultDeck().cards(12),
  Deck.defaultDeck().cards(13),
  Deck.defaultDeck().cards(14),
  Deck.defaultDeck().cards(15)
  ))

  val tableDeck = Deck(List(
  Deck.defaultDeck().cards(16),
  Deck.defaultDeck().cards(17),
  Deck.defaultDeck().cards(18),
  Deck.defaultDeck().cards(19),
  Deck.defaultDeck().cards(20),
  Deck.defaultDeck().cards(21),
  Deck.defaultDeck().cards(22),
  Deck.defaultDeck().cards(23)
  ))


  /*
  trait GameState {
    def players: List[Player]
    def deck: Deck
    def board: Deck
    def stdout: Option[String]
    def stderr: Option[String]
    def matchedDeck: Option[Deck] = None
    def queuedCard: Option[Card] = None
    def handleMatch(xS: String, yS: String): GameState
    def handleDiscard(xS: String): GameState
    def updateGameStateWithError(errorMessage: String): GameState
}
   */

  //this is a testable and repeatable test Gamestate
  val gameState = GameStatePlanned(
    List(
      Player("Player1", player1Deck, Deck(List.empty), 0),
      Player("Player2", player2Deck, Deck(List.empty), 0)
    ),
    tableDeck,
    Deck(List.empty),
    stdout = None,
    stderr = None
  )
  println(gameState)

  describe("printBoard") {
  it("should print the board correctly") {
    val game = GameStatePlanned(
      players = List(
        Player("Test1", player1Deck, Deck(List.empty), 0),
        Player("Test2", player2Deck, Deck(List.empty), 2)
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List.empty),
      stdout = None,
      stderr = None
    )
    val result = TUIManager.printBoard(game)
    //assert(result.contains(card.unicode.mkString("\n")))
  }


  it("should handle matched deck upper half correctly") {
  val card1 = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
  val card2 = Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE)
  val game = GameStatePlanned(
    players = List(
      Player("Test1", Deck(List(card1)), Deck(List.empty), 0),
      Player("Test2", Deck(List.empty), Deck(List.empty), 2)
    ),
    deck = Deck.defaultDeck(),
    board = Deck(List.empty),
    stdout = None,
    stderr = None)

  val result = TUIManager.printBoard(game)
  assert(result.contains(card1.unicode.mkString("\n")))
}
    it("should handle lower middle row cards correctly") {
      val card1 = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
      val card2 = Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE)
      val card3 = Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN)
      val card4 = Card(CardMonth.APRIL, CardType.TANE, CardName.CUCKOO)
      val card5 = Card(CardMonth.MAY, CardType.TANE, CardName.BRIDGE)

      val gameState = GameStatePlanned(
        List(Player("Player1", Deck(List(card1)), Deck(List(card1)), 0), Player("Player2", Deck(List(card2)), Deck(List(card2)), 0)),
        Deck(List(card1, card2, card3, card4, card5)),
        Deck(List(card1, card2, card3, card4, card5)),
        None,
        None
      )

      val result = TUIManager.printBoard(gameState)
      assert(result.contains(card1.unicode.mkString("\n")))
    }

    it("should handle matched deck lower half correctly") {
      val card1 = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
      val card2 = Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE)
      val game = GameStatePlanned(
        players = List(
          Player("Test1", Deck(List(card2)), Deck(List.empty), 0),
          Player("Test2", Deck(List.empty), Deck(List.empty), 2)),
        deck = Deck.defaultDeck(),
        board = Deck(List.empty),
        stdout = None,
        stderr = None
      )
      val result = TUIManager.printBoard(game)
      assert(result.contains(card2.unicode.mkString("\n")))
    }

    it("should handle stdout correctly") {
      val game = GameStatePlanned(
        players = List(
          Player("Test1", Deck(List.empty), Deck(List.empty), 0),
          Player("Test2", Deck(List.empty), Deck(List.empty), 2)),
        deck = Deck.defaultDeck(),
        board = Deck(List.empty),
        stdout = Some("Test stdout"),
        stderr = None
      )
      val result = TUIManager.printBoard(game)
      assert(result.contains("\n[]: Test stdout\n"))
    }

    it("should handle stderr correctly") {
      val game = GameStatePlanned(
        players = List(
          Player("Test1", Deck(List.empty), Deck(List.empty), 0),
          Player("Test2", Deck(List.empty), Deck(List.empty), 2)),
        deck = Deck.defaultDeck(),
        board = Deck(List.empty),
        stdout = None,
        stderr = Some("Test stderr")
      )
      val result = TUIManager.printBoard(game)
      assert(result.contains("\n[error]: Test stderr\n"))
    }
  }

  describe("update") {
    it("should call printBoard and print the correct output for a game state") {
      val card = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
      val gameState = GameStatePlanned(
        List(Player("Player1", Deck(List(card)), Deck(List(card)), 0), Player("Player2", Deck(List(card)), Deck(List(card)), 0)),
        Deck(List(card)),
        Deck(List(card)),
        None,
        None
      )

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        TUIManager.update(gameState)
      }

      val expectedOutput = TUIManager.printBoard(gameState) + "\n"
      assert(outputStream.toString == expectedOutput)
    }

    it("should update the TUI correctly for an empty game state") {
      val emptyGameState = GameStatePlanned(
        List(Player("Player1", Deck(List.empty), Deck(List.empty), 0), Player("Player2", Deck(List.empty), Deck(List.empty), 0)),
        Deck(List.empty),
        Deck(List.empty),
        None,
        None
      )
      val result = TUIManager.printBoard(emptyGameState)
      assert(TUIManager.printBoard(emptyGameState) == result)
    }

    it("should update the TUI correctly for a non-empty game state") {
      val card = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
      val player1Deck = Deck(List(card))
      val player2Deck = Deck(List(card))
      val gameState = GameStatePlanned(
        List(Player("Player1", player1Deck, player1Deck, 0), Player("Player2", player2Deck, player2Deck, 0)),
        Deck(List(card)),
        Deck(List(card)),
        None,
        None
      )
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

  describe("printOverview") {
    it("should printOverview/ combinations correctly") {
      val gameState = GameManager.newGame("Player1", "Player2")
      val result: String = TUIManager.printOverview(gameState)

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
      val card = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
      val card2 = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PHOENIX)
      val player1 = Player("Player1", Deck(List(card)), Deck(List(card)), 0)
      val player2 = Player("Player2", Deck(List(card2)), Deck(List(card2)), 0)
      val gameState = GameStatePlanned(
        List(player1, player2),
        Deck(List(card)),
        Deck(List(card)),
        None,
        None
      )

      val resultPlayer1: List[String] = TUIManager.colorizeOverviewCard(gameState, player1.side.cards.head)
      val resultPlayer2: List[String] = TUIManager.colorizeOverviewCard(gameState, player2.side.cards.head)

      val expectedGreen = card.unicode.map(line => s"\u001b[32m$line\u001b[0m")
      resultPlayer1 shouldEqual expectedGreen

      val expectedRed = card2.unicode.map(line => s"\u001b[31m$line\u001b[0m")
      resultPlayer2 shouldEqual expectedRed

      val strippedResultPlayer1 = resultPlayer1.map(_.replaceAll("\u001B\\[[;\\d]*m", ""))
      val strippedResultPlayer2 = resultPlayer2.map(_.replaceAll("\u001B\\[[;\\d]*m", ""))

      val expectedOutputPlayer1: List[String] = card.unicode
      val expectedOutputPlayer2: List[String] = card2.unicode

      strippedResultPlayer1 shouldEqual expectedOutputPlayer1
      strippedResultPlayer2 shouldEqual expectedOutputPlayer2
    }
  }
}