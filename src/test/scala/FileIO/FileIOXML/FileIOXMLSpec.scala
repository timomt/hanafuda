import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.*
import FileIO.FileIOXML.FileIOXML
import controller.GameController.gameState
import model.GameManager.GameManagerInstance.given_GameManager

import java.io.PrintWriter
import java.io.File

class FileIOXMLSpec extends AnyFlatSpec with Matchers {

  "FileIOXML" should "save and load GameStateRandom correctly" in {
    val player1 = Player("Alice", Deck(List(Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 2))), Deck(List()), 0, false, List())
    val player2 = Player("Bob", Deck(List(Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 6))), Deck(List()), 0, false, List())
    val game = GameStateRandom(
      players = List(
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
          side = Deck(List(
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0)
          )),
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
      matched = Deck(List(Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 0))),
      queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0),
      stdout = None,
      stderr = None
    )

    val fileIO = new FileIOXML

    fileIO.save(game) should be(true)

    val loadedGameState = fileIO.load

    loadedGameState shouldEqual game
  }

  it should "save and load GameStatePlanned correctly" in {
    val player1 = Player("Alice", Deck(List(Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 2))), Deck(List()), 0, false, List())
    val player2 = Player("Bob", Deck(List(Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 6))), Deck(List()), 0, false, List())
    val game = GameStatePlanned(
      players = List(
        Player(yakusToIgnore = List.empty,
          name = "",
          hand = Deck(List(Card(CardMonth.JANUARY, CardType.TANE, CardName.PLAIN, false, 0))),
          side = Deck(List(
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0)
          )),
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

    val fileIO = new FileIOXML

    fileIO.save(game) should be(true)

    val loadedGameState = fileIO.load

    loadedGameState shouldEqual game
  }

  it should "save and load GameStateUninitialized correctly" in {
    val game = GameStateUninitialized(DisplayType.GAME, Some("Error message"))

    val fileIO = new FileIOXML

    fileIO.save(game) should be(true)

    val loadedGameState = fileIO.load

    loadedGameState shouldEqual game
  }

  it should "save and load GameStatePendingKoiKoi correctly" in {
    val player1 = Player("Alice", Deck(List(Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 2))), Deck(List()), 0, false, List())
    val player2 = Player("Bob", Deck(List(Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 6))), Deck(List()), 0, false, List())
    val game = GameStatePendingKoiKoi(
      players = List(player1, player2),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0))),
      stdout = None,
      stderr = None
    )

    val fileIO = new FileIOXML

    fileIO.save(game) should be(true)

    val loadedGameState = fileIO.load

    loadedGameState shouldEqual game
  }

  it should "save and load GameStateSummary correctly" in {
    val player1 = Player("test1", Deck(List(Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 2))), Deck(List()), 0, false, List())
    val player2 = Player("test2", Deck(List(Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 6))), Deck(List()), 0, false, List())
    val game = GameStateSummary(
      players = List(player1, player2),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.MARCH, CardType.TANE, CardName.PLAIN, false, 0))),
      displayType = DisplayType.SUMMARY,
      stdout = None,
      stderr = None,
      outOfCardsEnding = true
    )

    val fileIO = new FileIOXML

    fileIO.save(game) should be(true)

    val loadedGameState = fileIO.load

    loadedGameState shouldEqual game
  }
}

