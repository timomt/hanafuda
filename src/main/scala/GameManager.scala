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
* */
case class GameState(players: List[Player], deck: Deck, board: Deck)

/*
* object GameManager
* An object to perform operations on GameState instances
* */
object GameManager {

    /*
    * def newGame(): GameState
    * Returns a new GameState as default game setup
    * */
    def newGame(): GameState = {
        val (board, deck) = Deck.pollMultiple(Deck.defaultDeck(), 8)
        val (playerList, updatedDeck) = (1 to 2).foldLeft((List.empty[Player], deck)) {
            case ((players, currentDeck), _) => {
                val (cards, newDeck) = Deck.pollMultiple(currentDeck, 8)
                (players :+ Player("Test", Deck(cards), Deck(List.empty), 0), newDeck)
            }
        }
        GameState(playerList, updatedDeck, updatedDeck)
    }

    /*
    * TODO: implement def nextTurn(...)
    *  Lets a player poll a card from deck and match it with the cards on board.
    *  returns the updated GameState
    * */
    //def nextTurn(game: GameState): GameState = {}

    /*
    * TODO: implement def evaluateScore(...)
    *  Evaluates the highest possible score of each player and returns a tuple of the result.
    *  The first tuple value is the score of the player of the current turn (game.players[0])*/
    //def evaluateScore(game: GameState): (Int, Int) = {}
}