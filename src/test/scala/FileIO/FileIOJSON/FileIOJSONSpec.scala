package FileIO.FileIOJSON

import FileIO.FileIOJSON.FileIOJSON
import model.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FileIOJSONSpec extends AnyFlatSpec with Matchers {

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
}