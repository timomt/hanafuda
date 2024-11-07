import org.scalatest.funsuite.AnyFunSuite
import model.{Card, CardMonth, CardName, CardType, Deck, GameState, Player, MatchType}
import view.TUIManager
import controller.GameController


class TUIManagerTest extends AnyFunSuite {

  test("printBoard should print the current game state") {
    // Create a sample game state
    val player1Cards = List(
      Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
      Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
      Card(CardMonth.MAY, CardType.TANZAKU, CardName.PLAIN),
      Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.JULY, CardType.TANZAKU, CardName.PLAIN),
      Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX)
    )

    val player2Cards = List(
      Card(CardMonth.JULY, CardType.TANE, CardName.BOAR),
      Card(CardMonth.NOVEMBER, CardType.KASU, CardName.LIGHTNING),
      Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES),
      Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN),
      Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
      Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.NOVEMBER, CardType.TANZAKU, CardName.PLAIN)
    )

    val boardCards = List(
      Card(CardMonth.JULY, CardType.TANE, CardName.BOAR),
      Card(CardMonth.NOVEMBER, CardType.KASU, CardName.LIGHTNING),
      Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES),
      Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN),
      Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
      Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.NOVEMBER, CardType.TANZAKU, CardName.PLAIN)
    )

    val matchedCards = List(
      Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
      Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
      Card(CardMonth.MAY, CardType.TANZAKU, CardName.PLAIN),
      Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN),
      Card(CardMonth.JULY, CardType.TANZAKU, CardName.PLAIN),
      Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX)
    )

val gameState = GameState(
  players = List(
    Player("Player1", hand = Deck(player2Cards), side = Deck(player1Cards), score = 0),
    Player("Player2", hand = Deck(player1Cards), side = Deck(player2Cards), score = 0)
  ),
  deck = Deck(List(player1Cards.head)),
  board = Deck(boardCards),
  matched = Deck(matchedCards),
  matchType = MatchType.RANDOM,
  queued = Some(Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN)),
  stdout = Some("Test stdout"),
  stderr = Some("Test stderr")
)

    // Call the printBoard method
    val result = TUIManager.printBoard(gameState)

    // Expected output
    val expectedOutput =
      """\u001b[2J\u001b[1;1HCurrent player: Player1
        |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
        |║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║
        |║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║
        |║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║ ║      ║
        |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
        |
        |╔══════╗          ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
        |║ Mar. ║          ║ Jul. ║ ║ Nov. ║ ║ Apr. ║ ║ Jun. ║
        |║Hikari║          ║ Tane ║ ║ Kasu ║ ║ Kasu ║ ║ Tane ║
        |║Curt. ║          ║ Boar ║ ║Light.║ ║Plane ║ ║Butter║
        |╚══════╝          ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
        |                  ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
        |                  ║ Nov. ║ ║ Feb. ║ ║ Dec. ║ ║ Nov. ║
        |                  ║Hikari║ ║Tanz. ║ ║ Kasu ║ ║Tanz. ║
        |                  ║ Rain ║ ║Po_tan║ ║Plane ║ ║Plane ║
        |                  ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
        |
        |╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗ ╔══════╗
        |║ Jun. ║ ║ Feb. ║ ║ Aug. ║ ║ May  ║ ║ Mar. ║ ║ Dec. ║ ║ Jul. ║ ║ Dec. ║
        |║Tanz. ║ ║ Kasu ║ ║Hikari║ ║Tanz. ║ ║ Kasu ║ ║ Kasu ║ ║Tanz. ║ ║Hikari║
        |║Bl_tan║ ║Plane ║ ║ Moon ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Plane ║ ║Phoen.║
        |╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝ ╚══════╝
        |[]: Test stdout
        |
        |[error]: Test stderr
        |""".stripMargin


    // Assert the result
    assert(result == expectedOutput)
  }

  test("printOverview should return a clean printed overview of card combinations") {
    // Call the printOverview method
    val result = TUIManager.printOverview(gameState)
    val expectedOutput =
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
  }
}