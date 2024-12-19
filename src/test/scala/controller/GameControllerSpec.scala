import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import controller.{CommandManagerSaveCommand, GameController}
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameManager, GameStatePendingKoiKoi, GameStatePlanned, GameStateRandom, GameStateUninitialized, Player}

class GameControllerSpec extends AnyFlatSpec with Matchers {
    "GameController" should "do nothing on invalid input before start" in {
        GameController.commandManager = new CommandManagerSaveCommand
        GameController.processInput("continue")
        GameController.gameState.stderr.isDefined should be (true)
    }

    it should "process input 'start Player1 Player2'" in {
        GameController.processInput("start Player1 Player2")
        GameController.gameState.players.head.name should be("Player1")
        GameController.gameState.players(1).name should be("Player2")
    }

    it should "return stderr on invalid input after start" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.processInput("gkqapwd")
        GameController.gameState.stderr.isDefined should be (true)
    }

    it should "handle pending koikoi correctly (koi-koi) input" in {
        GameController.gameState = GameStatePendingKoiKoi(
            players = List(
                Player(name = "Player1", hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, yakusToIgnore = List.empty, calledKoiKoi = false),
                Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)
            )),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        GameController.processInput("koi-koi")
        GameController.gameState.isInstanceOf[GameStatePlanned] should be (true)
    }

    it should "handle pending koikoi correctly (finish) input" in {
        GameController.gameState = GameStatePendingKoiKoi(
            players = List(
                Player(name = "Player1", hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, yakusToIgnore = List.empty, calledKoiKoi = false),
                Player(name = "Player2", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)
            )),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        GameController.processInput("finish")
        GameController.gameState.displayType should be (DisplayType.SUMMARY)
    }

    it should "handle pending koikoi correctly (_) input" in {
        GameController.gameState = GameStatePendingKoiKoi(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List(
                Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)
            )),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        GameController.processInput("gowawodjasd")
        GameController.gameState.stderr.isDefined should be (true)
    }

    it should "initialize a new game" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.gameState.isInstanceOf[GameStateUninitialized] should be (false)
    }

    it should "process input 'help'" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.processInput("help")
        GameController.gameState.displayType should be (DisplayType.HELP)
    }

    it should "process input 'continue'" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.processInput("continue")
        GameController.gameState.displayType should be (DisplayType.GAME)
    }

    it should "process input 'match 1 2'" in {
    GameController.gameState = GameStatePlanned(
        players = List(
            Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
            Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
        ),
        deck = Deck.defaultDeck(),
        board = Deck(List(
            Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)
        )),
        displayType = DisplayType.GAME,
        stdout = None,
        stderr = None
    )
        GameController.processInput("match 1 1")
        GameController.gameState.players.head.hand.cards should be (List.empty)
        GameController.gameState.players(1).hand.cards should be (List.empty)
    }

    it should "process input 'match 1'" in {
      GameController.gameState = GameStateRandom(
          players = List(
              Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
              Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
          ),
          deck = Deck.defaultDeck(),
          board = Deck(List(
              Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)
          )),
          displayType = DisplayType.GAME,
          stdout = None,
          stderr = None,
          matched = Deck(List.empty),
          queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)
      )
      GameController.processInput("match 1")
      GameController.gameState.board.cards should be (List.empty)
      GameController.gameState.stderr.isEmpty should be (true)
    }

    it should "process input 'discard 1'" in {
      GameController.gameState = GameStatePlanned(
          players = List(
              Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
              Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
          ),
          deck = Deck.defaultDeck(),
          board = Deck(List.empty),
          displayType = DisplayType.GAME,
          stdout = None,
          stderr = None
      )
      GameController.processInput("discard 1")
      GameController.gameState.players.head.hand.cards should be(List.empty)
      GameController.gameState.board.cards should be (List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)))
      GameController.gameState.stderr.isEmpty should be(true)
    }

    it should "process input 'discard'" in {
      GameController.gameState = GameStateRandom(
          players = List(
              Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
              Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
          ),
          deck = Deck.defaultDeck(),
          board = Deck(List.empty),
          displayType = DisplayType.GAME,
          stdout = None,
          stderr = None,
          matched = Deck(List.empty),
          queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)
      )
          GameController.processInput("discard")
          GameController.gameState.board.cards should be(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0)))
          GameController.gameState.stderr.isEmpty should be(true)
    }

    it should "process input 'new'" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.processInput("new")
        GameController.gameState.players.map(_.name) should contain allOf("Player1", "Player2")
    }

    it should "process input 'combinations'" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.processInput("combinations")
        GameController.gameState.displayType should be (DisplayType.COMBINATIONS)
    }

    it should "undo moves correctly" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.processInput("combinations")
        GameController.processInput("help")
        GameController.gameState.displayType should be (DisplayType.HELP)
        GameController.processInput("undo")
        GameController.gameState.displayType should be (DisplayType.COMBINATIONS)
        GameController.processInput("undo")
        GameController.gameState.displayType should be (DisplayType.GAME)
    }

    it should "redo moves correctly" in {
        GameController.gameState = GameManager.newGame("Player1", "Player2")
        GameController.notifyObservers(GameController.gameState)
        GameController.processInput("combinations")
        GameController.processInput("help")
        GameController.gameState.displayType should be(DisplayType.HELP)
        GameController.processInput("undo")
        GameController.gameState.displayType should be(DisplayType.COMBINATIONS)
        GameController.processInput("redo")
        GameController.gameState.displayType should be(DisplayType.HELP)
    }
}