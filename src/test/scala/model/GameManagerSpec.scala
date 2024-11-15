package model

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
        val game = GameManager.newGame(" ", " ",
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
        val game = GameManager.newGame(" ", " ",
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
}