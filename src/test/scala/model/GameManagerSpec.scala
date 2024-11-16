package model

import model.GameManager.*
import org.scalatest.flatspec.AnyFlatSpec

class GameManagerSpec extends AnyFlatSpec {
    "newGame()" should "return a game initialized with the given player names" in {
        val firstName = "5r2313^2_23sad"
        val secondName = "51280qehd0_.1q2du9132312"
        val game = GameManager.newGame(firstName, secondName)
        assert(game.players.head.name === firstName)
        assert(game.players(1).name === secondName)
    }
    
    it should "not deal a full month" in {
        val game = GameManager.newGame(" ", " ", 0, 0,
            Some(List(Card(CardMonth.JULY, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR))))
        assert(game.board.cards.groupBy(_.month).forall((_, list) => list.size < 4))
    }
    
    it should "group 3 cards of the same month together" in {
        val game = GameManager.newGame(" ", " ", 0, 0,
            Some(List(Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.JULY, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.JULY, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.JANUARY, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.JANUARY, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.BOAR))))
        assert(game.board.cards.forall(c => c.month == CardMonth.MARCH && c.grouped || c.month != CardMonth.MARCH && !c.grouped))
    }

    it should "handle instant win conditions" in {
        val game = GameManager.newGame(
            firstPlayer = "FirstPlayer",
            secondPlayer = "SecondPlayer",
            customHandFirst = Some(Deck(List(
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
                Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN)
            ))),
        )
        assert(game.players.head.score === TeshiCombination.points)
        assert(game.displayType === DisplayType.SUMMARY)
    }

    "initializePlayers" should "correctly initialize a list of 2 Players" in {
        val (playerList, updatedDeck) = initializePlayers(
            firstPlayer = "firstPlayer",
            secondPlayer = "secondPlayer",
            firstScore = 1,
            secondScore = 2,
            deck = Deck.defaultDeck())
        assert(playerList.head.hand.cards.size === 8
            && playerList.head.hand.cards.size === playerList(1).hand.cards.size)
        assert(updatedDeck.cards.size === 48 - 2*8)
        assert(playerList.head.score === 1 && playerList(1).score === 2)
        assert(playerList.forall(_.side === Deck(List.empty)))
    }

    "koiKoiHandler" should "return instance of GameStatePendingKoiKoi if koi-koi not already called" in {
        val game = GameStateRandom(
            players = List(
                Player(name = "Player1", score = 0, hand = Deck(List.empty), side = Deck(List(
                    Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
                    Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
                )), calledKoiKoi = false),
                Player(name = "Player2", score = 0, hand = Deck(List.empty), side = Deck(List.empty), calledKoiKoi = false),
            ),
            deck = Deck(List.empty),
            board = Deck(List.empty),
            matched = Deck(List.empty),
            displayType = DisplayType.GAME,
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
            stdout = None,
            stderr = None
        )
        val newGame = GameManager.koiKoiHandler(game)
        assert(newGame.isInstanceOf[GameStatePendingKoiKoi])
        assert(newGame.stdout === Some(s"You scored a yaku: Hanami-zake (花見酒) \"Cherry Blossom Viewing\"\t5pts.\n You can now either finish or call koi-koi."))
        assert(newGame.stderr.isEmpty)
    }
    it should "double the points of the caller in case of a successful call" in {
        val game = GameStateRandom(
            players = List(
                Player(name = "Player1", score = 10, hand = Deck(List.empty), side = Deck(List(
                    Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
                    Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
                )), calledKoiKoi = true),
                Player(name = "Player2", score = 5, hand = Deck(List.empty), side = Deck(List.empty), calledKoiKoi = false),
            ),
            deck = Deck(List.empty),
            board = Deck(List.empty),
            matched = Deck(List.empty),
            displayType = DisplayType.GAME,
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
            stdout = None,
            stderr = None
        )
        val newGame = GameManager.koiKoiHandler(game)
        assert(newGame.players.head.score === game.players.head.score + 2*HanamiZakeCombination.points)
        assert(newGame.players(1).score === game.players(1).score)
    }
    it should "set the points of the caller to 0 and double the points of opponent if call failed" in {
        val game = GameStateRandom(
            players = List(
                Player(name = "Player1", score = 10, hand = Deck(List.empty), side = Deck(List(
                    Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
                    Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
                )), calledKoiKoi = false),
                Player(name = "Player2", score = 5, hand = Deck(List.empty), side = Deck(List(
                    Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
                    Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
                )), calledKoiKoi = true),
            ),
            deck = Deck(List.empty),
            board = Deck(List.empty),
            matched = Deck(List.empty),
            displayType = DisplayType.GAME,
            queued = Card(CardMonth.JULY, CardType.HIKARI, CardName.PLAIN),
            stdout = None,
            stderr = None
        )
        val newGame = GameManager.koiKoiHandler(game)
        assert(newGame.players.head.score === game.players.head.score + 2 * HanamiZakeCombination.points)
        assert(newGame.players(1).score === game.players(1).score)
    }

    "koiKoiCallHandler" should "continue planned game with calledKoiKoi attribute set" in {
        val game = GameStatePendingKoiKoi(
            players = List(
                Player(
                    name = "FirstPlayer", hand = Deck(List.empty), side = Deck(List.empty),
                    score = 0, calledKoiKoi = false
                ),
                Player(
                    name = "SecondPlayer", hand = Deck(List.empty), side = Deck(List.empty),
                    score = 0, calledKoiKoi = false
                )
            ),
            deck = Deck(List.empty),
            board = Deck(List.empty),
            displayType = DisplayType.GAME,
            stdout = None,
            stderr = None
        )
        val newGame = GameManager.koiKoiCallHandler(game)
        assert(newGame.isInstanceOf[GameStatePlanned])
        assert(newGame.players.head === Player(
            name = "SecondPlayer", hand = Deck(List.empty), side = Deck(List.empty),
            score = 0, calledKoiKoi = false
        ))
        assert(newGame.players(1) === Player(
            name = "FirstPlayer", hand = Deck(List.empty), side = Deck(List.empty),
            score = 0, calledKoiKoi = true
        ))
    }
}