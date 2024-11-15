package model

import model.DisplayType.SUMMARY

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
    def newGame(firstPlayer: String, secondPlayer: String, firstScore: Int = 0, secondScore: Int = 0, customBoard: Option[List[Card]] = None): GameStatePlanned = {
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

    /*
    * If koikoi already called... evaluate depending on who called
    * if not then GameStatePendingKoiKoi
    * def koiKoiHandler(..)
    * loser begins new game
    * *///TODO: display which yaku was won
    def koiKoiHandler(game: GameState): GameState = {
        if (game.players.head.calledKoiKoi) {   // Reversed -> fulfilled call
            val (firstS, secS) = evaluateScore(game.players, 2, 1)
            val newGame = GameManager.newGame(
                firstPlayer = game.players(1).name,
                secondPlayer = game.players.head.name,
                firstScore = secS,
                secondScore = firstS
            )
            newGame.copy(displayType = SUMMARY)
        } else if (game.players(1).calledKoiKoi) {  // failed call
            val (firstS, secS) = evaluateScore(game.players, 2, 0)
            val newGame = GameManager.newGame(
                firstPlayer = game.players(1).name,
                secondPlayer = game.players.head.name,
                firstScore = secS,
                secondScore = firstS
            )
            newGame.copy(displayType = SUMMARY)
        } else {
            GameStatePendingKoiKoi(
                players = game.players.reverse,
                deck = game.deck,
                board = game.board,
                displayType = DisplayType.GAME,
                stdout = Some(s"You scored a yaku: TODO You can now either finish or call koi-koi."),
                stderr = None
            )
        }
    }

    /*
    * def koiKoiCallHandler(..)
    * supposed to handle the call for koi-koi
    * */
    def koiKoiCallHandler(game: GameState): GameState = {
        GameStatePlanned(
            players = List(game.players.head.copy(calledKoiKoi = true), game.players(1)),
            deck = game.deck,
            board = game.board,
            displayType = DisplayType.GAME,
            stdout = Some("Continueing game."),
            stderr = None
        )
    }

    /*
    * def finishHandler(..)
    * supposed to handle the finish call
    * */
    def finishHandler(game: GameState): GameState = {
        val (firstS, secS) = evaluateScore(game.players, 1, 1)
        val newGame = GameManager.newGame(
            firstPlayer = game.players.head.name,
            secondPlayer = game.players(1).name,
            firstScore = firstS,
            secondScore = secS
        )
        newGame.copy(displayType = SUMMARY)
    }

    /*
    * */
    def evaluateScore(players: List[Player], multiplyerFirst: Int, multiplyerSec: Int): (Int, Int) = {
        val firstScore = players.head.score + multiplyerFirst * yakuCombinations.foldLeft(0) {
            case (acc, yaku) =>
                acc + yaku.evaluate(players.head)
        }
        val secondScore = players(1).score + multiplyerSec * yakuCombinations.foldLeft(0) {
            case (acc, yaku) =>
                acc + yaku.evaluate(players(1))
        }
        (firstScore, secondScore)
    }
}