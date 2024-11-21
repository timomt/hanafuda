import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameManager, GameStatePendingKoiKoi, GameStatePlanned, GameStateRandom, Player}

class GameControllerSpec extends AnyFlatSpec with Matchers {
    "GameController" should "do nothing on invalid input before start" in {
        GameController.processInput("continue")
        GameController.gameState.isDefined should be (false)
    }

    it should "process input 'start Player1 Player2'" in {
        GameController.processInput("start Player1 Player2")
        GameController.gameState.get.players.head.name should be("Player1")
        GameController.gameState.get.players(1).name should be("Player2")
    }

    it should "return stderr on invalid input after start" in {
        GameController.gameState = Some(GameManager.newGame("Player1", "Player2"))
        GameController.notifyObservers(GameController.gameState.get)
        GameController.processInput("gkqapwd")
        GameController.gameState.get.stderr.isDefined should be (true)
    }

    it should "handle pending koikoi correctly (koi-koi) input" in {
        GameController.gameState = Some(GameStatePendingKoiKoi(
            players = List(
                Player(name = "Player1", hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)
            )),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        ))
        GameController.processInput("koi-koi")
        GameController.gameState.get.isInstanceOf[GameStatePlanned] should be (true)
    }

    it should "handle pending koikoi correctly (finish) input" in {
        GameController.gameState = Some(GameStatePendingKoiKoi(
            players = List(
                Player(name = "Player1", hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)
            )),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        ))
        GameController.processInput("finish")
        GameController.gameState.get.displayType should be (DisplayType.SUMMARY)
    }

    it should "handle pending koikoi correctly (_) input" in {
        GameController.gameState = Some(GameStatePendingKoiKoi(
            players = List(
                Player(name = "Player1", hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)
            )),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        ))
        GameController.processInput("gowawodjasd")
        GameController.gameState.get.stderr.isDefined should be (true)
    }

    it should "initialize a new game" in {
        GameController.gameState = Some(GameManager.newGame("Player1", "Player2"))
        GameController.notifyObservers(GameController.gameState.get)
        GameController.gameState.isDefined should be (true)
    }

    it should "process input 'help'" in {
        GameController.gameState = Some(GameManager.newGame("Player1", "Player2"))
        GameController.notifyObservers(GameController.gameState.get)
        GameController.processInput("help")
        GameController.gameState.get.displayType should be (DisplayType.HELP)
    }

    it should "process input 'continue'" in {
        GameController.gameState = Some(GameManager.newGame("Player1", "Player2"))
        GameController.notifyObservers(GameController.gameState.get)
        GameController.processInput("continue")
        GameController.gameState.get.displayType should be (DisplayType.GAME)
    }

    it should "process input 'match 1 2'" in {
    GameController.gameState = Some(GameStatePlanned(
        players = List(
            Player(name = "Player1", hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
            Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
        ),
        deck = Deck.defaultDeck(),
        board = Deck(List(
            Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)
        )),
        displayType = DisplayType.GAME,
        stdout = None,
        stderr = None
    ))
        GameController.processInput("match 1 1")
        GameController.gameState.get.players.head.hand.cards should be (List.empty)
        GameController.gameState.get.players(1).hand.cards should be (List.empty)
    }

    it should "process input 'match 1'" in {
      GameController.gameState = Some(GameStateRandom(
          players = List(
              Player(name = "Player1", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
              Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
          ),
          deck = Deck.defaultDeck(),
          board = Deck(List(
              Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)
          )),
          displayType = DisplayType.GAME,
          stdout = None,
          stderr = None,
          matched = Deck(List.empty),
          queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)
      ))
      GameController.processInput("match 1")
      GameController.gameState.get.board.cards should be (List.empty)
      GameController.gameState.get.stderr.isEmpty should be (true)
    }

    it should "process input 'discard 1'" in {
      GameController.gameState = Some(GameStatePlanned(
          players = List(
              Player(name = "Player1", hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
              Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
          ),
          deck = Deck.defaultDeck(),
          board = Deck(List.empty),
          displayType = DisplayType.GAME,
          stdout = None,
          stderr = None
      ))
      GameController.processInput("discard 1")
      GameController.gameState.get.players.head.hand.cards should be(List.empty)
      GameController.gameState.get.board.cards should be (List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)))
      GameController.gameState.get.stderr.isEmpty should be(true)
    }

    it should "process input 'discard'" in {
      GameController.gameState = Some(GameStateRandom(
          players = List(
              Player(name = "Player1", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
              Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
          ),
          deck = Deck.defaultDeck(),
          board = Deck(List.empty),
          displayType = DisplayType.GAME,
          stdout = None,
          stderr = None,
          matched = Deck(List.empty),
          queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)
      ))
          GameController.processInput("discard")
          GameController.gameState.get.board.cards should be(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN)))
          GameController.gameState.get.stderr.isEmpty should be(true)
    }

    it should "process input 'new'" in {
        GameController.gameState = Some(GameManager.newGame("Player1", "Player2"))
        GameController.notifyObservers(GameController.gameState.get)
        GameController.processInput("new")
        GameController.gameState.get.players.map(_.name) should contain allOf("Player1", "Player2")
    }

    it should "process input 'combinations'" in {
        GameController.gameState = Some(GameManager.newGame("Player1", "Player2"))
        GameController.notifyObservers(GameController.gameState.get)
        GameController.processInput("combinations")
        GameController.gameState.get.displayType should be (DisplayType.COMBINATIONS)
    }
}