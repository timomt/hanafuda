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
    * customBoard := used for testing purposes.
    * */
    @tailrec
    def newGame(firstPlayer: String, secondPlayer: String, firstScore: Int = 0, secondScore: Int = 0, customBoard: Option[List[Card]] = None, customHandFirst: Option[Deck] = None, customHandSec: Option[Deck] = None): GameStatePlanned = {
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

            val (playerList, updatedDeck) = initializePlayers(firstPlayer, secondPlayer, firstScore, secondScore, deck, customHandFirst = customHandFirst, customHandSec = customHandSec)
            if (instantWinCombinations.exists(_.evaluate(playerList.head) > 0)
                || instantWinCombinations.exists(_.evaluate(playerList(1)) > 0)) {
                val firstS = calculateInstantWinScore(playerList.head)
                val secS = calculateInstantWinScore(playerList(1))
                handleKoiKoi(playerList, firstS, secS)
            } else {
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
    }

    /*
    * Initializes players with their respective cards and scores.
    * */
    def initializePlayers(firstPlayer: String, secondPlayer: String, firstScore: Int, secondScore: Int, deck: Deck,  customHandFirst: Option[Deck] = None, customHandSec: Option[Deck] = None): (List[Player], Deck) = {
        (1 to 2).foldLeft((List.empty[Player], deck)) {
            case ((players, currentDeck), n) =>
                val (cards, newDeck) = Deck.pollMultiple(currentDeck, 8)
                val name = if (n == 1) firstPlayer else secondPlayer
                val score = if (n == 1) firstScore else secondScore
                val hand = if (n == 1) customHandFirst else customHandSec
                (players :+ Player(
                    name = name,
                    hand = if hand.isDefined then hand.get else Deck(cards),
                    side = Deck(List.empty),
                    score = score,
                    calledKoiKoi = false), newDeck
                )
        }
    }

    /*
    * If koikoi already called... evaluate depending on who called
    * if not then GameStatePendingKoiKoi
    * def koiKoiHandler(..)
    * loser begins new game
    * */
    def koiKoiHandler(game: GameState): GameState = {
        if (game.players.head.calledKoiKoi) {   // fulfilled call
            val (firstS, secS) = evaluateScore(game.players, 2, 1)
            handleKoiKoi(game.players, firstS, secS)
        } else if (game.players(1).calledKoiKoi) {  // failed call
            val (firstS, secS) = evaluateScore(game.players, 2, 0)
            handleKoiKoi(game.players, firstS, secS)
        } else {
            val yakuList = yakuCombinations.filter(c => c.evaluate(game.players.head) > 0).map(_.unicode)
            GameStatePendingKoiKoi(
                players = game.players,
                deck = game.deck,
                board = game.board,
                displayType = DisplayType.GAME,
                stdout = Some(s"You scored a yaku: \n${yakuList.map(_ + "\n").mkString("\n")} You can now either finish or call koi-koi."),
                stderr = None
            )
        }
    }

    /*
    * Handles the koi-koi call and returns the new game state.
    * */
    def handleKoiKoi(players: List[Player], firstS: Int, secS: Int): GameStatePlanned = {
        val newGame = GameManager.newGame(
            firstPlayer = players.head.name,
            secondPlayer = players(1).name,
            firstScore = firstS,
            secondScore = secS
        )
        newGame.copy(displayType = SUMMARY)
    }

    /*
    * def koiKoiCallHandler(..)
    * supposed to handle the call for koi-koi
    * */
    def koiKoiCallHandler(game: GameState): GameState = {
        GameStatePlanned(
            players = List(game.players(1), game.players.head.copy(calledKoiKoi = true)),
            deck = game.deck,
            board = game.board,
            displayType = DisplayType.GAME,
            stdout = Some("Continueing game."),
            stderr = None
        )
    }

    /*
    * Calculates the instant win score for a player.
    * */
    private def calculateInstantWinScore(player: Player): Int = {
        instantWinCombinations.foldLeft(0) {
            case (acc, yaku) =>
                acc + yaku.evaluate(player)
        }
    }

    /*
    * def evaluateScore()
    * returns a tuple of the maximum score of each player multiplied with the give multipliers */
    def evaluateScore(players: List[Player], multiplierFirst: Int, multiplierSec: Int): (Int, Int) = {
        val firstScore = players.head.score + multiplierFirst * yakuCombinations.foldLeft(0) {
            case (acc, yaku) =>
                acc + yaku.evaluate(players.head)
        }
        val secondScore = players(1).score + multiplierSec * yakuCombinations.foldLeft(0) {
            case (acc, yaku) =>
                acc + yaku.evaluate(players(1))
        }
        (firstScore, secondScore)
    }
}