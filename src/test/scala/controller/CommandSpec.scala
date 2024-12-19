import controller.{CommandManager, CommandManagerSaveCommand, CommandManagerSaveState, GameController, HelpCommand}
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameStatePlanned, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommandSpec extends AnyFlatSpec with Matchers {
    /* ------ CommandManager ------ */
    "CommandManagerSaveCommand" should "return some in stderr if undo called on empty stack" in {
        val cmdManager = new CommandManagerSaveCommand()
        cmdManager.undo(GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )).stderr.isDefined should be (true)
    }
    it should "execute a command and update the game state" in {
        val cmdManager = new CommandManagerSaveCommand()
        val initialState = GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        val command = new HelpCommand
        val newState = cmdManager.executeCommand(command, initialState)
        newState.displayType should be(DisplayType.HELP)
    }
    it should "undo the last executed command and revert to the previous game state" in {
        val cmdManager = new CommandManagerSaveCommand()
        val initialState = GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        val command = new HelpCommand
        cmdManager.executeCommand(command, initialState)
        val undoneState = cmdManager.undo(initialState)
        undoneState.displayType should be(DisplayType.GAME)
    }
    it should "redo the last undone command and update the game state" in {
        val cmdManager = new CommandManagerSaveCommand()
        val initialState = GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        val command = new HelpCommand
        cmdManager.executeCommand(command, initialState)
        cmdManager.undo(initialState)
        val redoneState = cmdManager.redo(initialState)
        redoneState.displayType should be(DisplayType.HELP)
    }
    it should "return some in stderr if redo called on empty stack" in {
        val cmdManager = new CommandManagerSaveCommand()
        cmdManager.redo(GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )).stderr.isDefined should be(true)
    }

    "CommandManagerSaveState" should "return some in stderr if undo called on empty stack" in {
        val cmdManager = new CommandManagerSaveState()
        cmdManager.undo(GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )).stderr.isDefined should be(true)
    }
    it should "execute a command and update the game state" in {
        val cmdManager = new CommandManagerSaveState()
        val initialState = GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        val command = new HelpCommand
        val newState = cmdManager.executeCommand(command, initialState)
        newState.displayType should be(DisplayType.HELP)
    }
    it should "undo the last executed command and revert to the previous game state" in {
        val cmdManager = new CommandManagerSaveState()
        val initialState = GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        val command = new HelpCommand
        cmdManager.executeCommand(command, initialState)
        val undoneState = cmdManager.undo(initialState)
        undoneState.displayType should be(DisplayType.GAME)
    }
    it should "redo the last undone command and update the game state" in {
        val cmdManager = new CommandManagerSaveState()
        val initialState = GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        val command = new HelpCommand
        val newState = cmdManager.executeCommand(command, initialState)
        cmdManager.undo(newState)
        val redoneState = cmdManager.redo(initialState)
        redoneState.displayType should be(DisplayType.HELP)
    }
    it should "return some in stderr if redo called on empty stack" in {
        val cmdManager = new CommandManagerSaveState()
        cmdManager.redo(GameStatePlanned(
            players = List(
                Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
            ),
            deck = Deck.defaultDeck(),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )).stderr.isDefined should be(true)
    }

    /* --------------------- */
    /* ------ Command ------ */
    "Command" should "throw exception if undo called before execute" in {
        val cmd = new HelpCommand
        intercept[IllegalStateException] {
            cmd.undo(GameStatePlanned(
                players = List(
                    Player(name = "Player1", yakusToIgnore = List.empty, hand = Deck(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN, false, 0))), side = Deck(List.empty), score = 0, calledKoiKoi = false),
                    Player(name = "Player2", yakusToIgnore = List.empty, hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false)
                ),
                deck = Deck.defaultDeck(),
                board = Deck(List.empty),
                displayType = DisplayType.GAME,
                stdout = None,
                stderr = None
            ))
        }
    }
    /* --------------------- */
}