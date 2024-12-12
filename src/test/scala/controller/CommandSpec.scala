import controller.{CommandManager, GameController, HelpCommand}
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameStatePlanned, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommandSpec extends AnyFlatSpec with Matchers {
    /* ------ CommandManager ------ */
    "CommandManager" should "return some in stderr if undo called on empty stack" in {
        val cmdManager = new CommandManager()
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
    it should "return some in stderr if redo called on empty stack" in {
        val cmdManager = new CommandManager()
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