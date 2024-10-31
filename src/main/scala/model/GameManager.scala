package model

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
    * Returns a new GameState as default game setup.
    * TODO: initialize a default game according to koi-koi rules.
    * */
    def newGame(firstPlayer: String, secondPlayer: String): GameState = {
        val (board, deck) = Deck.pollMultiple(Deck.defaultDeck(), 8)
        val (playerList, updatedDeck) = (1 to 2).foldLeft((List.empty[Player], deck)) {
            case ((players, currentDeck), n) => {
                val (cards, newDeck) = Deck.pollMultiple(currentDeck, 8)
                val name = if(n == 1) firstPlayer else secondPlayer
                (players :+ Player(name, Deck(cards), Deck(List.empty), 0), newDeck)
            }
        }
        model.GameState(playerList, updatedDeck, updatedDeck)
    }
    
    
    /*
    * def matchCards(...)
    * matches the two given cards.
    * */
    def matchCards(game: GameState, ownCard:Int, pickedCard:Int): GameState = {
        game
    }

    /*
    // players liste, erstes element in liste aktueller player

    /*
    * TODO: implement def nextTurn(...)
    *  Lets a player poll a card from hand or deck and match it with the cards on board.
    *  returns the updated GameState
    * */
    def nextTurn(game: GameState): GameState = {
        val newPlayers = game.players.reverse
        val currentPlayer = game.players.head

        // Karte wählen und legen/ komnbinieren
        println("Spieler: " + currentPlayer.name + " ist am Zug")
        val choosenCard = selectCard(currentPlayer)

        val input = Iterator.continually {
            println("Drücke Enter um eine Karte zu legen und eine neue zu ziehen oder gib 'combine' ein um eine Karte zu kombinieren:")
            readLine()
        }.find(input => input == "" || input == "combine").getOrElse("")

        input match {
            case "" => // karte auf den Tisch legen und eine neue ziehen
                println("Wähle eine Karte von deiner Hand (Index):")
                val cardIndex = readLine().toInt - 1
                val choosenCard = currentPlayer.hand.cards(cardIndex)
                val (newCard, newDeck) = Deck.poll(game.deck)
                val updatedHandDeck = currentPlayer.hand.copy(cards = currentPlayer.hand.cards.filterNot(_ == choosenCard) :+ newCard.get)
                val updatedBoardDeck = game.board.copy(cards = game.board.cards :+ choosenCard)
                val updatedPlayer = currentPlayer.copy(hand = updatedHandDeck)
                val updatedPlayers = newPlayers.reverse :+ updatedPlayer
                return game.copy(players = updatedPlayers, deck = newDeck, board = updatedBoardDeck)
            case "combine" =>
                var valid = checkMatch(choosenCard, game.board)
                while (valid) {
                    println("Karte passt. Drücke Enter um weiter zu kombinieren oder gib 'stop' ein um zu beenden:")
                    val combineInput = readLine()
                    combineInput match {
                        case "stop" =>
                            valid = false
                        case _ =>
                            val updatedBoardDeck = game.board.copy(cards = game.board.cards.filterNot(_ == choosenCard))
                            val updatedHandDeck = currentPlayer.hand.copy(cards = currentPlayer.hand.cards.filterNot(_ == choosenCard))
                            val updatedSideDeck = currentPlayer.side.copy(cards = currentPlayer.side.cards :+ choosenCard)
                            val updatedPlayer = currentPlayer.copy(hand = updatedHandDeck, side = updatedSideDeck)
                            val updatedPlayers = newPlayers.reverse :+ updatedPlayer
                            val updatedGame = game.copy(players = updatedPlayers, board = updatedBoardDeck)
                            println("Wähle eine weitere Karte zum Kombinieren:")
                            val newChoosenCard = selectCard(currentPlayer)
                            valid = checkMatch(newChoosenCard, updatedGame.board)
                    }
                }
            case _ =>
                println("Ungültige Eingabe. Versuche es erneut.")
        }
        val updatedPlayers = newPlayers.reverse :+ currentPlayer
        val newDeck = game.deck
        val updatedBoardDeck = game.board
        game.copy(players = updatedPlayers, deck = newDeck, board = updatedBoardDeck)
    }

    def selectCard(currentPlayer: Player): Card = {
        // welche karte möchtest du nehmen?
        println("Welche Karte wählst du?")
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
<<<<<<<< HEAD:src/main/scala/GameManager.scala
    //def evaluateScore(game: GameState): (Int, Int) = {}
========
        //def evaluateScore(game: GameState): (Int, Int) = {}*/
>>>>>>>> e071711 (building MVC architecture and writing documentation):src/main/scala/model/GameManager.scala
}