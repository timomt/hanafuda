package FileIO.FileIOJSON

import FileIO.FileIOJSON.FileIOJSON
import model.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import model.GameManager.GameManagerInstance.given_GameManager

import java.io.PrintWriter

class FileIOJSONSpec extends AnyFlatSpec with Matchers {

  "Json.obj" should "create correct JSON for Card" in {
    val card = Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
    val json = Json.obj(
      "month" -> card.month.toString,
      "cardType" -> card.cardType.toString,
      "cardName" -> card.cardName.toString,
      "index" -> card.index,
      "grouped" -> card.grouped
    )
    val expectedJson = Json.parse(
      """
            {
              "month": "MARCH",
              "cardType": "TANE",
              "cardName": "PLAIN",
              "index": 0,
              "grouped": true
            }
          """
    )
    json shouldBe expectedJson
  }

  "FileIO/FileIOJSON" should "save and load GameState correctly for GamestatePlanned" in {
    val fileIO = new FileIOJSON

    val game = GameStatePlanned(
      players = List(
        Player("test 1", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty),
        Player("test 2", Deck(List.empty), Deck(List.empty), 0, calledKoiKoi = false, yakusToIgnore = List.empty)
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(
        Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
        Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0),
        Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, true, 0)
      )),
      stdout = None,
      stderr = None
    )

    fileIO.save(game) shouldBe true

    val loadedGameState = fileIO.load

    loadedGameState shouldBe game
  }

  it should "save and load GameState correctly for GameStateRandom" in {
    val fileIO = new FileIOJSON

    val game = GameStateRandom(
      players = List(
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
          side = Deck(List.empty),
          score = 0,
          calledKoiKoi = false
        ),
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List.empty),
          side = Deck(List.empty),
          score = 0,
          calledKoiKoi = false
        )
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0))),
      matched = Deck(List.empty),
      queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0),
      stdout = None,
      stderr = None
    )

    fileIO.save(game) shouldBe true

    val loadedGameState = fileIO.load

    loadedGameState shouldBe game
  }

  it should "save and load GameState correctly for GameStatePendingKoiKoi" in {
    val fileIO = new FileIOJSON

    val game = GameStatePendingKoiKoi(
      players = List(
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
          side = Deck(List.empty),
          score = 0,
          calledKoiKoi = false
        ),
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List.empty),
          side = Deck(List.empty),
          score = 0,
          calledKoiKoi = false
        )
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0))),
      stdout = None,
      stderr = None
    )

    fileIO.save(game) shouldBe true

    val loadedGameState = fileIO.load

    loadedGameState shouldBe game
  }

  it should "save and load GameState correctly for GameStateSummary" in {
    val fileIO = new FileIOJSON

    val game = GameStateSummary(
      players = List(
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
          side = Deck(List.empty),
          score = 0,
          calledKoiKoi = false
        ),
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List.empty),
          side = Deck(List.empty),
          score = 0,
          calledKoiKoi = false
        )
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0))),
      displayType = DisplayType.SUMMARY,
      stdout = None,
      stderr = None,
      outOfCardsEnding = false
    )
    fileIO.save(game) shouldBe true

    val loadedGameState = fileIO.load

    loadedGameState shouldBe game
  }

  it should "throw an exception for unknown GameState" in {
    val fileIO = new FileIOJSON

    val invalidJson = Json.parse(
      """
          {
            "instanceOf": "UnknownGameState"
          }
        """
    )

    val writer = new PrintWriter("gameState.json")
    writer.write(Json.prettyPrint(invalidJson))
    writer.close()

    an[IllegalArgumentException] should be thrownBy fileIO.load
  }
}