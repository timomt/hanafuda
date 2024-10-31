package model

import scala.annotation.tailrec
import scala.collection.immutable.List as name

/*
* enum MatchType
* a match can either be planned or random (match from hand or match from stack)
* */
enum MatchType {
    case PLANNED, RANDOM
}

/*
* case class Player(...)
* name:= Name of the player
* hand:= The current hand of the player
* side:= The players side Deck representing their gathered cards
* score:= The current highest possible score of combinations
* */
case class Player(name: String, hand: Deck, side: Deck, score: Int)

/*
* case class GameState(...)
* players:= The list of players (size 2), where the player at index 0 is to make a move
* deck:= The deck of remaining cards to draw
* board:= The cards currently layed out in the middle
* matched:= A deck of currently matched cards
* queued:= The Card on top of stack waiting to be matched
* */
case class GameState(players: List[Player], deck: Deck, board: Deck, matched: Deck, matchType: MatchType, queued: Option[Card], stdout: Option[String], stderr: Option[String])

/*
* object GameManager
* An object to perform operations on GameState instances
* */
object GameManager {
    /*
    * def newGame(): GameState
    * Returns a new GameState as default game setup.
    * TODO: initialize a default game according to koi-koi rules.
    * */
    @tailrec
    def newGame(firstPlayer: String, secondPlayer: String): GameState = {
        val (board, deck) = Deck.pollMultiple(Deck.defaultDeck(), 8)
        if (board.groupBy(_.month).exists(cards => cards.size == 4)) {
            newGame(firstPlayer, secondPlayer)
        } //else if () //TODO: implement 3/4 of month dealt
        else {
            val (playerList, updatedDeck) = (1 to 2).foldLeft((List.empty[Player], deck)) {
                case ((players, currentDeck), n) => {
                    val (cards, newDeck) = Deck.pollMultiple(currentDeck, 8)
                    val name = if (n == 1) firstPlayer else secondPlayer
                    (players :+ Player(name, Deck(cards), Deck(List.empty), 0), newDeck)
                }
            }
            model.GameState(playerList, updatedDeck, Deck(board), Deck(List.empty), MatchType.RANDOM, None, None, None)
        }
    }
}