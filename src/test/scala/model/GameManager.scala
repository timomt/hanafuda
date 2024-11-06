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
        val game = GameManager.newGame(" ", " ")
        assert(game.board.cards.groupBy(_.month).forall((_, list) => list.size < 4))
    }
    
    it should "group 3 cards of the same month together" in {
        
    }
    
    it should "end if a player is dealt a yaku" in {
        
    }
}