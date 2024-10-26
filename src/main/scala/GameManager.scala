import scala.io.StdIn.readLine

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

    // players liste, erstes element in liste aktueller player

    /*
    * TODO: implement def nextTurn(...)
    *  Lets a player poll a card from hand or deck and match it with the cards on board.
    *  returns the updated GameState
    * */
    def nextTurn(game: GameState): GameState = {
        val newPlayers = game.players.reverse
        val currentPlayer = game.players.head
        val choosenCard = selectCard(currentPlayer)
        val valid = checkMatch(choosenCard, game.board)
        if (valid) {
            val updatedBoardDeck = game.board.copy(cards = game.board.cards.filterNot(_ == choosenCard))
            val updatedHandDeck = currentPlayer.hand.copy(cards = currentPlayer.hand.cards.filterNot(_ == choosenCard))
            val updatedSideDeck = currentPlayer.side.copy(cards = currentPlayer.side.cards :+ choosenCard)
            game
        } else {
            game
        }
    }

    def selectCard(currentPlayer: Player): Card = {
        //welche karte möchtest du nehmen?
        println("Welche Karte möchtest du legen/ kombinieren ?")
        //kontrollieren ob Karte kombinierbar mit karten auf tisch
        val input = readLine()
        val cardIndex = input.toInt - 1
        currentPlayer.hand.cards(cardIndex)
    }

    def checkMatch(card: Card, board: Deck): Boolean = {
        board.cards.exists((_.month == card.month))
    }

    /*
    * TODO: implement def evaluateScore(...)
    *  Evaluates the highest possible score of each player and returns a tuple of the result.
    *  The first tuple value is the score of the player of the current turn (game.players[0])*/
    //def evaluateScore(game: GameState): (Int, Int) = {}
}