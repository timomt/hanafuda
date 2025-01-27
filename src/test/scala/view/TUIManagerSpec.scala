import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameStatePlanned, GameStateRandom, GameStateSummary, Player}
import view.TUIManager
import controller.GameController
import model.DisplayType.SUMMARY
import model.GameManager.GameManager
import model.GameManager.GameManagerDefault.GameManagerDefault

class TUIManagerSpec extends AnyFunSpec with Matchers {
  given gameManager: GameManager = new GameManagerDefault()

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
  Deck.defaultDeck().cards(23),
  ))

  //this is a testable and repeatable test Gamestate
  val gameState = GameStatePlanned(
    List(
      Player("Player1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
      Player("Player2", player2Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
    ),
    tableDeck,
    Deck(List.empty),
    stdout = None,
    stderr = None
  )

  describe("update") {
    it("should output game") {
      val gameState = GameStatePlanned(
        List(
          Player("Player1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Player2", player2Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        tableDeck,
        Deck(List.empty),
        stdout = None,
        stderr = None,
        displayType = DisplayType.GAME
      )

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        TUIManager.update(gameState)
      }
      val expectedOutput = TUIManager.printBoard(gameState)
      assert(outputStream.toString == expectedOutput)
    }

    it("should output combinations") {
      val gameState = GameStatePlanned(
        List(
          Player("Player1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Player2", player2Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        tableDeck,
        Deck(List.empty),
        stdout = None,
        stderr = None,
        displayType = DisplayType.COMBINATIONS
      )

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        TUIManager.update(gameState)
      }
      val expectedOutput = TUIManager.printOverview(gameState)
      assert(outputStream.toString == expectedOutput)
    }

    it("should output help") {
      val gameState = GameStatePlanned(
        List(
          Player("Player1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Player2", player2Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        tableDeck,
        Deck(List.empty),
        stdout = None,
        stderr = None,
        displayType = DisplayType.HELP
      )

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        TUIManager.update(gameState)
      }
      val expectedOutput = TUIManager.printHelp()
      assert(outputStream.toString == expectedOutput)
    }

    it("should output spoiler") {
      val gameState = GameStatePlanned(
        List(
          Player("Player1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Player2", player2Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        tableDeck,
        Deck(List.empty),
        stdout = None,
        stderr = None,
        displayType = DisplayType.SPOILER
      )

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        TUIManager.update(gameState)
      }
      val expectedOutput = TUIManager.printSpoiler()
      assert(outputStream.toString == expectedOutput)
    }
  }

  describe("printBoard") {
    it("should print the board correctly (size > 8 && size mod 2 != 0)") {
      val game = GameStatePlanned(
        players = List(
          Player("Test1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Test2", player2Deck, Deck(List.empty), 2, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck.defaultDeck(),
        board = Deck(tableDeck.cards.appended(
          Deck.defaultDeck().cards(24)
        )),
        stdout = None,
        stderr = None
      )
      val actualOutput =
        "\u001b[2J\u001b[3J\u001b[1;1HCurrent player: Test1\tPoints: 0\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n" +
          "\n" +
          "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗                           \n" +
          "                  ║ May  ║ ║ May  ║ ║ May  ║ ║ May  ║ ║ Jun. ║                           \n" +
          "                  ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║                           \n" +
          "                  ║Bridge║ ║Plane ║ ║Plane ║ ║Plane ║ ║Butter║                           \n" +
          "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝                           \n" +
          "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗                                    \n" +
          "                  ║ Jun. ║ ║ Jun. ║ ║ Jun. ║ ║ Jul. ║                                    \n" +
          "                  ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║                                    \n" +
          "                  ║Bl_tan║ ║Plane ║ ║Plane ║ ║ Boar ║                                    \n" +
          "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝                                    \n" +
          "\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║\n" +
          "║Hikari║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║\n" +
          "║Crane ║ ║Po_tan║ ║Plane ║ ║Plane ║ ║Night.║ ║Po_tan║ ║Plane ║ ║Plane ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n"
      assert(TUIManager.printBoard(game) == actualOutput)
    }

    it("should print the board correctly (size > 8 && size mod 2 == 0)") {
      val game = GameStatePlanned(
        players = List(
          Player("Test1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Test2", player2Deck, Deck(List.empty), 2, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck.defaultDeck(),
        board = Deck(tableDeck.cards.appendedAll(List(
          Deck.defaultDeck().cards(24), Deck.defaultDeck().cards(25)
        ))),
        stdout = None,
        stderr = None
      )
      val actualOutput =
        "\u001b[2J\u001b[3J\u001b[1;1HCurrent player: Test1\tPoints: 0\n" +
            "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
            "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
            "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
            "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
            "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n" +
            "\n" +
            "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗                           \n" +
            "                  ║ May  ║ ║ May  ║ ║ May  ║ ║ May  ║ ║ Jun. ║                           \n" +
            "                  ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║                           \n" +
            "                  ║Bridge║ ║Plane ║ ║Plane ║ ║Plane ║ ║Butter║                           \n" +
            "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝                           \n" +
            "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗                           \n" +
            "                  ║ Jun. ║ ║ Jun. ║ ║ Jun. ║ ║ Jul. ║ ║ Jul. ║                           \n" +
            "                  ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║ ║Tanz. ║                           \n" +
            "                  ║Bl_tan║ ║Plane ║ ║Plane ║ ║ Boar ║ ║Plane ║                           \n" +
            "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝                           \n" +
            "\n" +
            "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
            "║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║\n" +
            "║Hikari║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║\n" +
            "║Crane ║ ║Po_tan║ ║Plane ║ ║Plane ║ ║Night.║ ║Po_tan║ ║Plane ║ ║Plane ║\n" +
            "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n"
      assert(TUIManager.printBoard(game) == actualOutput)
    }

    it("should print the board correctly (size < 8 && size >= 5)") {
      val game = GameStatePlanned(
        players = List(
          Player("Test1", player1Deck, Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Test2", player2Deck, Deck(List.empty), 2, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck.defaultDeck(),
        board = Deck(List(
          Deck.defaultDeck().cards(16),
          Deck.defaultDeck().cards(17),
          Deck.defaultDeck().cards(18),
          Deck.defaultDeck().cards(19),
          Deck.defaultDeck().cards(20),
          Deck.defaultDeck().cards(21)
        )),
        stdout = None,
        stderr = None
      )
      val actualOutput =
        "\u001b[2J\u001b[3J\u001b[1;1HCurrent player: Test1\tPoints: 0\n" +
            "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
            "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
            "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
            "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
            "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n" +
            "\n" +
            "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗                           \n" +
            "                  ║ May  ║ ║ May  ║ ║ May  ║ ║ May  ║                           \n" +
            "                  ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║                           \n" +
            "                  ║Bridge║ ║Plane ║ ║Plane ║ ║Plane ║                           \n" +
            "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝                           \n" +
            "                  ╔══════╗ ╔══════╗                                             \n" +
            "                  ║ Jun. ║ ║ Jun. ║                                             \n" +
            "                  ║ Tane ║ ║Tanz. ║                                             \n" +
            "                  ║Butter║ ║Bl_tan║                                             \n" +
            "                  ╚══════╝ ╚══════╝                                             \n" +
            "\n" +
            "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
            "║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║\n" +
            "║Hikari║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║\n" +
            "║Crane ║ ║Po_tan║ ║Plane ║ ║Plane ║ ║Night.║ ║Po_tan║ ║Plane ║ ║Plane ║\n" +
            "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n"
      assert(TUIManager.printBoard(game) == actualOutput)
    }

    it("print board with queued correctly") {
      val game = GameStateRandom(
        players = List(
          Player(
            name = "Test1",
            hand = player1Deck,
            side = Deck(List.empty),
            score = 0,
            calledKoiKoi = false,
            yakusToIgnore = List.empty
          ),
          Player(
            name = "Test2",
            hand = player2Deck,
            side = Deck(List.empty),
            score = 0,
            calledKoiKoi = false,
            yakusToIgnore = List.empty
          )
        ),
        deck = Deck.defaultDeck(),
        board = tableDeck,
        matched = Deck(List.empty),
        queued = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0),
        stdout = None,
        stderr = None
      )
      val actualOutput =
        "\u001b[2J\u001b[3J\u001b[1;1HCurrent player: Test1\tPoints: 0\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n" +
          "\n" +
          "╔══════╗          ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗         \n" +
          "║ Jan. ║          ║ May  ║ ║ May  ║ ║ May  ║ ║ May  ║         \n" +
          "║Hikari║          ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║         \n" +
          "║Plane ║          ║Bridge║ ║Plane ║ ║Plane ║ ║Plane ║         \n" +
          "╚══════╝          ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝         \n" +
          "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗         \n" +
          "                  ║ Jun. ║ ║ Jun. ║ ║ Jun. ║ ║ Jun. ║         \n" +
          "                  ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║         \n" +
          "                  ║Butter║ ║Bl_tan║ ║Plane ║ ║Plane ║         \n" +
          "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝         \n" +
          "\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║\n" +
          "║Hikari║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║\n" +
          "║Crane ║ ║Po_tan║ ║Plane ║ ║Plane ║ ║Night.║ ║Po_tan║ ║Plane ║ ║Plane ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n"
      assert(TUIManager.printBoard(game) == actualOutput)
    }

    it("print board with matched cards correctly") {
      val game = GameStateRandom(
        players = List(
          Player(
            name = "Test1",
            hand = player1Deck,
            side = Deck(List.empty),
            score = 0,
            calledKoiKoi = false,
            yakusToIgnore = List.empty
          ),
          Player(
            name = "Test2",
            hand = player2Deck,
            side = Deck(List.empty),
            score = 0,
            calledKoiKoi = false,
            yakusToIgnore = List.empty
          )
        ),
        deck = Deck.defaultDeck(),
        board = tableDeck,
        matched = Deck(List(Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0))),
        queued = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0),
        stdout = None,
        stderr = None
      )
      val actualOutput =
        "\u001b[2J\u001b[3J\u001b[1;1HCurrent player: Test1\tPoints: 0\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n" +
          "\n" +
          "╔══════╗          ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗         \n" +
          "║ Jan. ║          ║ May  ║ ║ May  ║ ║ May  ║ ║ May  ║         \n" +
          "║Hikari║          ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║         \n" +
          "║Plane ║          ║Bridge║ ║Plane ║ ║Plane ║ ║Plane ║         \n" +
          "╚══════╝          ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝         \n" +
          "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗          ╔══════╗\n" +
          "                  ║ Jun. ║ ║ Jun. ║ ║ Jun. ║ ║ Jun. ║          ║ Jan. ║\n" +
          "                  ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║          ║Hikari║\n" +
          "                  ║Butter║ ║Bl_tan║ ║Plane ║ ║Plane ║          ║Plane ║\n" +
          "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝          ╚══════╝\n" +
          "\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║\n" +
          "║Hikari║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║\n" +
          "║Crane ║ ║Po_tan║ ║Plane ║ ║Plane ║ ║Night.║ ║Po_tan║ ║Plane ║ ║Plane ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n"
      assert(TUIManager.printBoard(game) == actualOutput)
    }

    it("print board with two matched cards correctly") {
      val matched_deck = Deck(List(Deck.defaultDeck().cards(1),
                                   Deck.defaultDeck().cards(2)))
      val game = GameStateRandom(
        players = List(
          Player(
            name = "Test1",
            hand = player1Deck,
            side = Deck(List.empty),
            score = 0,
            calledKoiKoi = false,
            yakusToIgnore = List.empty
          ),
          Player(
            name = "Test2",
            hand = player2Deck,
            side = Deck(List.empty),
            score = 0,
            calledKoiKoi = false,
            yakusToIgnore = List.empty
          )
        ),
        deck = Deck.defaultDeck(),
        board = tableDeck,
        matched = matched_deck,
        queued = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PLAIN, false, 0),
        stdout = None,
        stderr = None
      )
      val actualOutput =
        "\u001b[2J\u001b[3J\u001b[1;1HCurrent player: Test1\tPoints: 0\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n" +
          "\n" +
          "╔══════╗          ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗          ╔══════╗\n" +
          "║ Jan. ║          ║ May  ║ ║ May  ║ ║ May  ║ ║ May  ║          ║ Jan. ║\n" +
          "║Hikari║          ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║          ║Tanz. ║\n" +
          "║Plane ║          ║Bridge║ ║Plane ║ ║Plane ║ ║Plane ║          ║Po_tan║\n" +
          "╚══════╝          ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝          ╚══════╝\n" +
          "                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗          ╔══════╗\n" +
          "                  ║ Jun. ║ ║ Jun. ║ ║ Jun. ║ ║ Jun. ║          ║ Jan. ║\n" +
          "                  ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║          ║ Kasu ║\n" +
          "                  ║Butter║ ║Bl_tan║ ║Plane ║ ║Plane ║          ║Plane ║\n" +
          "                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝          ╚══════╝\n" +
          "\n" +
          "╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗\n" +
          "║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║ ║ Feb. ║\n" +
          "║Hikari║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║\n" +
          "║Crane ║ ║Po_tan║ ║Plane ║ ║Plane ║ ║Night.║ ║Po_tan║ ║Plane ║ ║Plane ║\n" +
          "╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝\n"
      assert(TUIManager.printBoard(game) == actualOutput)
    }

    it("should handle stdout correctly") {
      val game = GameStatePlanned(
        players = List(
          Player("Test1", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Test2", Deck(List.empty), Deck(List.empty), 2, calledKoiKoi = false, yakusToIgnore = List.empty)),
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
          Player("Test1", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Test2", Deck(List.empty), Deck(List.empty), 2, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
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
      val card = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0)
      val gameState = GameStatePlanned(
        List(
          Player("Player1", Deck(List(card)), Deck(List(card)), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Player2", Deck(List(card)), Deck(List(card)), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck(List(card)),
        board = Deck(List(card)),
        stdout = None,
        stderr = None
      )

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        TUIManager.update(gameState)
      }

      val expectedOutput = TUIManager.printBoard(gameState)
      assert(outputStream.toString == expectedOutput)
    }

    it("should update the TUI correctly for an empty game state") {
      val emptyGameState = GameStatePlanned(
        List(
          Player("Player1", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Player2", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck(List.empty),
        board = Deck(List.empty),
        stdout = None,
        stderr = None
      )
      val result = TUIManager.printBoard(emptyGameState)
      assert(TUIManager.printBoard(emptyGameState) == result)
    }

    it("should update the TUI correctly for a non-empty game state") {
      val card = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0)
      val player1Deck = Deck(List(card))
      val player2Deck = Deck(List(card))
      val gameState = GameStatePlanned(
        List(
          Player("Player1", player1Deck, player1Deck, 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player("Player2", player2Deck, player2Deck, 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck(List(card)),
        board = Deck(List(card)),
        stdout = None,
        stderr = None
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
            |║ 2. continue [shortcut "con"]                                           ║
            |║    - return to the current game.                                       ║
            |║                                                                        ║
            |║ 2. match <x> <y>                                                       ║
            |║    - Matches cards at positions x and y on the board.                  ║
            |║                                                                        ║
            |║ 3. discard [<x>]                                                       ║
            |║    - discard card at given number.                                     ║
            |║    - argument x is only to be provided when discarding from hand       ║
            |║                                                                        ║
            |║ 4. new                                                                 ║
            |║    - takes player names and creates a new game from scratch            ║
            |║                                                                        ║
            |║ 5. combinations [shortcut "com"]                                       ║
            |║    - Displays the possible combinations of cards.                      ║
            |║                                                                        ║
            |║ 6. help                                                                ║
            |║    - Displays this help page.                                          ║
            |║                                                                        ║
            |║ 7. exit                                                                ║
            |║    - Exits the game.                                                   ║
            |║                                                                        ║
            |║ 8. save                                                                ║
            |║    - save the current GameState                                        ║
            |║                                                                        ║
            |║ 9. load                                                                ║
            |║    - load a GameState                                                  ║
            |╚════════════════════════════════════════════════════════════════════════╝
            |""".stripMargin

      assert(TUIManager.printHelp() == expectedHelpText)
    }
  }

  describe("printSpoiler") {
    it("should print the spoiler text correctly") {
      val expectedSpoilerText =
        "\u001b[2J\u001b[3J\u001b[1;1H" +
          """
            |╔════════════════════════════════════════════════════════════════════════╗
            |║                          Spoiler Protection                            ║
            |╠════════════════════════════════════════════════════════════════════════╣
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                  Type "continue" to advance the game                   ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |║                                                                        ║
            |╚════════════════════════════════════════════════════════════════════════╝
            |""".stripMargin

      assert(TUIManager.printSpoiler() == expectedSpoilerText)
    }
  }

  describe("printOverview") {
    it("should printOverview/ combinations correctly") {
      val gameState = GameStatePlanned(
        players = List(
          Player(
            name = "Player1",
            hand = Deck(List.empty),
            side = Deck(List(Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN, false, 0))),
            score = 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player(
            name = "Player2",
            hand = Deck(List.empty),
            side = Deck(List(Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN, false, 0))),
            score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck.defaultDeck(),
        board = Deck(List.empty),
        displayType = DisplayType.GAME,
        stdout = None,
        stderr = None
      )
      val result: String = TUIManager.printOverview(gameState)

      val strippedResult = result.replaceAll("\u001b\\[0m", "")

      val expectedOutput: String = TUIManager.clearScreen +
        s"""Gokō (五光) "Five Hikari"	10pts.	(exact yaku)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║ ║ Nov. ║ ║ Dec. ║
          |║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║ ║ Rain ║ ║Phoen.║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Shikō (四光) "Four Hikari"	8pts.	(exact yaku)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║ ║ Dec. ║
          |║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║ ║Phoen.║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Ame-Shikō (雨四光) "Rainy Four Hikari"	7pts.	(Rain + any other 3)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║ ║ Nov. ║ ║ Dec. ║
          |║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║ ║ Rain ║ ║Phoen.║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Sankō (三光) "Three Hikari"	6pts.	(Any 3 excluding Rain)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Mar. ║ ║ Aug. ║ ║ Dec. ║
          |║Hikari║ ║Hikari║ ║Hikari║ ║Hikari║
          |║Crane ║ ║Curt. ║ ║ Moon ║ ║Phoen.║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Tsukimi-zake (月見酒) "Moon Viewing"	5pts.	(exact yaku)
          |╔══════╗ ╔══════╗
          |║ Aug. ║ ║ Sep. ║
          |║Hikari║ ║ Tane ║
          |║ Moon ║ ║Sake_c║
          |╚══════╝ ╚══════╝
          |
          |Hanami-zake (花見酒) "Cherry Blossom Viewing"	5pts.	(exact yaku)
          |╔══════╗ ╔══════╗
          |║ Mar. ║ ║ Sep. ║
          |║Hikari║ ║ Tane ║
          |║Curt. ║ ║Sake_c║
          |╚══════╝ ╚══════╝
          |
          |Inoshikachō (猪鹿蝶) "Boar, Deer, Butterfly"	5pts.	(exact yaku)
          |╔══════╗ ╔══════╗ ╔══════╗
          |║ Jun. ║ ║ Jul. ║ ║ Oct. ║
          |║ Tane ║ ║ Tane ║ ║ Tane ║
          |║Butter║ ║ Boar ║ ║ Deer ║
          |╚══════╝ ╚══════╝ ╚══════╝
          |
          |Tane (タネ) 	1pt.	(Any 5 Tane, +1pt for each extra)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Feb. ║ ║ Apr. ║ ║ May  ║ ║ Jun. ║ ║ Jul. ║ ║ Aug. ║ ║ Sep. ║ ║ Oct. ║ ║ Nov. ║
          |║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║ ║ Tane ║
          |║Night.║ ║Cuckoo║ ║Bridge║ ║Butter║ ║ Boar ║ ║Geese ║ ║Sake_c║ ║ Deer ║ ║Swall.║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Akatan Aotan no Chōfuku (赤短青短の重複) "Red Poem, Blue Poem"	10pts.	(exact yaku)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Feb. ║ ║ Mar. ║ ║ Jun. ║ ║ Sep. ║ ║ Oct. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Po_tan║ ║Po_tan║ ║Po_tan║ ║Bl_tan║ ║Bl_tan║ ║Bl_tan║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Akatan (赤短) "Red Poem"	5pts.	(exact yaku)
          |╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Feb. ║ ║ Mar. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Po_tan║ ║Po_tan║ ║Po_tan║
          |╚══════╝ ╚══════╝ ╚══════╝
          |
          |Aotan (青短) "Blue Poem"	5pts.	(exact yaku)
          |╔══════╗ ╔══════╗ ╔══════╗
          |║ Jun. ║ ║ Sep. ║ ║ Oct. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Bl_tan║ ║Bl_tan║ ║Bl_tan║
          |╚══════╝ ╚══════╝ ╚══════╝
          |
          |Tanzaku (短冊) "Ribbons"	1pt.	(Any 5 Tanzaku, +1pt for each extra)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Feb. ║ ║ Mar. ║ ║ Apr. ║ ║ May  ║ ║ Jun. ║ ║ Jul. ║ ║ Sep. ║ ║ Oct. ║ ║ Nov. ║
          |║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║ ║Tanz. ║
          |║Po_tan║ ║Po_tan║ ║Po_tan║ ║Plane ║ ║Plane ║ ║Bl_tan║ ║Plane ║ ║Bl_tan║ ║Bl_tan║ ║Plane ║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |
          |Kasu (カス) 	1pt.	(Any 10 Kasu, +1pt for each extra)
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ \u001b[32m╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jan. ║ ║ Jan. ║ ║ Feb. ║ ║ Feb. ║ \u001b[32m║ Mar. ║ ║ Mar. ║ ║ Apr. ║ ║ Apr. ║ ║ May  ║ ║ May  ║
          |║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ \u001b[32m║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║
          |║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ \u001b[32m║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ \u001b[32m╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |╔══════╗ ╔══════╗ \u001b[31m╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Jun. ║ ║ Jun. ║ \u001b[31m║ Jul. ║ ║ Jul. ║ ║ Aug. ║ ║ Aug. ║ ║ Sep. ║ ║ Sep. ║ ║ Oct. ║ ║ Oct. ║
          |║ Kasu ║ ║ Kasu ║ \u001b[31m║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║
          |║Plane ║ ║Plane ║ \u001b[31m║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║
          |╚══════╝ ╚══════╝ \u001b[31m╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
          |║ Nov. ║ ║ Dec. ║ ║ Dec. ║ ║ Dec. ║
          |║ Kasu ║ ║ Kasu ║ ║ Kasu ║ ║ Kasu ║
          |║Light.║ ║Plane ║ ║Plane ║ ║Plane ║
          |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
          |""".stripMargin

      assert(strippedResult == expectedOutput)
    }
  }

  describe("colorizeOverviewCard") {
    it("should colorize the cards correctly for each player") {
      val card = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0)
      val card2 = Card(CardMonth.JANUARY, CardType.HIKARI, CardName.PHOENIX, false, 0)
      val player1 = Player("Player1", Deck(List(card)), Deck(List(card)), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
      val player2 = Player("Player2", Deck(List(card2)), Deck(List(card2)), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
      val gameState = GameStatePlanned(
        List(player1, player2),
        deck = Deck(List(card)),
        board = Deck(List(card)),
        stdout = None,
        stderr = None
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

  describe("printSummary") {
    it("should print the summary text correctly") {
      val game = GameStateSummary(
        players = List(
          Player(name = "abcdehaweafasfq3wzqtadasgatsd3wqtqa2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty),
          Player(name = "abcde", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ),
        deck = Deck(List.empty),
        board = Deck(List.empty),
        displayType = SUMMARY,
        stdout = None,
        stderr = None
      )
      val expected = TUIManager.clearScreen +
      """
          |╔═══════════════════════════════════════════════════════════════════════╗
          |║                                Summary                                ║
          |╠═══════════════════════════════════════════════════════════════════════╣
          |║                         Score of each player                          ║
          |╠═════════════════════════╦══════════════════════╦══════════════════════╣
          |║          Yaku           ║ abcdehaweafasfq3w... ║ abcde                ║
          |╠═════════════════════════╬══════════════════════╬══════════════════════╣
          |║ Gokō                    ║ 0                    ║ 0                    ║
          |║ Shikō                   ║ 0                    ║ 0                    ║
          |║ Ame-Shikō               ║ 0                    ║ 0                    ║
          |║ Sankō                   ║ 0                    ║ 0                    ║
          |║ Tsukimi-zake            ║ 0                    ║ 0                    ║
          |║ Hanami-zake             ║ 0                    ║ 0                    ║
          |║ Inoshikachō             ║ 0                    ║ 0                    ║
          |║ Tane                    ║ 0                    ║ 0                    ║
          |║ Akatan Aotan no Chōfuku ║ 0                    ║ 0                    ║
          |║ Akatan                  ║ 0                    ║ 0                    ║
          |║ Aotan                   ║ 0                    ║ 0                    ║
          |║ Tanzaku                 ║ 0                    ║ 0                    ║
          |║ Kasu                    ║ 0                    ║ 0                    ║
          |║ Teshi                   ║ 0                    ║ 0                    ║
          |║ Kuttsuki                ║ 0                    ║ 0                    ║
          |╚═════════════════════════╩══════════════════════╩══════════════════════╝
          |""".stripMargin
      TUIManager.update(game)
      assert(TUIManager.printSummary(game) === expected)
    }
  }
}