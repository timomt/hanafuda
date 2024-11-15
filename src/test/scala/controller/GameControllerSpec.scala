import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameManager, GameState, GameStateRandom, Player}

class GameControllerSpec extends AnyFlatSpec with Matchers {

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
    val game = GameStateRandom(
      players = List(
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        ),
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        )
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))),
      matched = Deck(List.empty),
      queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
      stdout = None,
      stderr = None
    )
    GameController.gameState = Some(game)
    GameController.processInput("match 1 2")
    GameController.gameState.get.players.head.hand.cards should have size 8
    GameController.gameState.get.players(1).hand.cards should have size 8
  }

  it should "process input 'match 1'" in {
    val game = GameStateRandom(
      players = List(
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        ),
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        )
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))),
      matched = Deck(List.empty),
      queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
      stdout = None,
      stderr = None
    )
    GameController.gameState = Some(game)
    GameController.processInput("match 1")
    GameController.gameState.get.players.head.hand.cards should have size 8
    GameController.gameState.get.players(1).hand.cards should have size 8
  }

  it should "process input 'discard 1'" in {
    val game = GameStateRandom(
      players = List(
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        ),
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        )
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))),
      matched = Deck(List.empty),
      queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
      stdout = None,
      stderr = None
    )
    GameController.gameState = Some(game)
    GameController.processInput("discard 1")
    GameController.gameState.get.players.head.hand.cards should have size 8
  }

  it should "process input 'discard'" in {
    val game = GameStateRandom(
      players = List(
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        ),
        Player(
          name = "",
          hand = Deck(player2Deck.cards),
          side = Deck(List.empty),
          score = 0
        )
      ),
      deck = Deck.defaultDeck(),
      board = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))),
      matched = Deck(List.empty),
      queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
      stdout = None,
      stderr = None
    )
    GameController.gameState = Some(game)
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
}