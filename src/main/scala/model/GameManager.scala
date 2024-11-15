package model

import scala.annotation.tailrec

/*
* object GameManager
* An object to perform operations on GameState instances
* */
object GameManager {
    /*
    * def newGame(): GameState
    * Returns a new GameState as default game setup.
    * TODO: check for instant win
    * customBoard := used for testing purposes.
    * */
    @tailrec
    def newGame(firstPlayer: String, secondPlayer: String, firstScore: Int = 0, secondScore: Int = 0, customBoard: Option[List[Card]] = None): GameState = {
        val (polledBoard, deck) = Deck.pollMultiple(Deck.defaultDeck(), 8)
        val actualPolledBoard = customBoard.getOrElse(polledBoard)
        
        // If 4 cards of the same month are dealt, shuffle again
        if (actualPolledBoard.groupBy(_.month).exists((_, cards) => cards.size >= 4)) {
            newGame(firstPlayer, secondPlayer, firstScore, secondScore)
        }
        else {
            // If 3 cards of the same month are dealt, group them
            val groupedMonths = actualPolledBoard.groupBy(_.month).collect {
                case (month, cards) if cards.size == 3 => month
            }.toSet
            val actualBoard = actualPolledBoard.map { card =>
                if (groupedMonths.contains(card.month)) card.copy(grouped = true)
                else card
            }

            val (playerList, updatedDeck) = (1 to 2).foldLeft((List.empty[Player], deck)) {
                case ((players, currentDeck), n) => {
                    val (cards, newDeck) = Deck.pollMultiple(currentDeck, 8)
                    val name = if (n == 1) firstPlayer else secondPlayer
                    val score = if (n == 1) firstScore else secondScore
                    (players :+ Player(name, Deck(cards), Deck(List.empty), score, false), newDeck)
                }
            }
            model.GameStatePlanned(
                players = playerList,
                deck = updatedDeck,
                board = Deck(actualBoard),
                stdout = None,
                stderr = None,
                displayType = DisplayType.GAME
            )
        }
    }

    /*TODO:
    * If koikoi already called... evaluate depending on who called
    * if not then GameStatePendingKoiKoi
    * def koiKoiHandler(..)*/
    def koiKoiHandler(game: GameState): GameState = {
        if (game.players.head.calledKoiKoi) {
            ???
        } else if (game.players(1).calledKoiKoi) {
            ???
        } else {
            GameStatePendingKoiKoi(
                players = game.players.reverse,
                deck = game.deck,
                board = game.board,
                displayType = DisplayType.GAME,
                stdout = Some("You scored a yaku! You can now either finish or call koi-koi."),
                stderr = None
            )
        }
    }

    /*
    * def koiKoiCallHandler(..)
    * supposed to handle the call for koi-koi
    * */
    def koiKoiCallHandler(game: GameState): GameState = {
        ???
    }

    /*
    * def finishHandler(..)
    * supposed to handle the finish call
    * */
    def finishHandler(game: GameState): GameState = {
        ???
    }
}