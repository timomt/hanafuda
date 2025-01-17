package model.GameManager.GameManagerDefault
import model.GameManager.GameManager
import model.{Card, Deck, DisplayType, GameState, GameStatePendingKoiKoi, GameStatePlanned, GameStateSummary, Player, instantWinCombinations, yakuCombinations}

import scala.annotation.tailrec

class GameManagerDefault extends GameManager {

    /**
     * Returns a new GameState as default game setup.
     *
     * @param firstPlayer the name of the first player
     * @param secondPlayer the name of the second player
     * @param firstScore the initial score of the first player
     * @param secondScore the initial score of the second player
     * @param customBoard an optional custom board for testing purposes
     * @param customHandFirst an optional custom hand for the first player
     * @param customHandSec an optional custom hand for the second player
     * @return a new GameState
     */
    @tailrec
    final def newGame(firstPlayer: String, secondPlayer: String, firstScore: Int = 0, secondScore: Int = 0, customBoard: Option[List[Card]] = None, customHandFirst: Option[Deck] = None, customHandSec: Option[Deck] = None): GameState = {
        val (polledBoard, deck) = Deck.pollMultiple(Deck.defaultDeck(), 8)
        val actualPolledBoard = customBoard.getOrElse(polledBoard)

        // If 4 cards of the same month are dealt, shuffle again
        if (actualPolledBoard.groupBy(_.month).exists((_, cards) => cards.size >= 4)) {
            newGame(firstPlayer, secondPlayer, firstScore, secondScore)
        } else {
            // If 3 cards of the same month are dealt, group them -> easily detect a match of a whole month
            val groupedMonths = actualPolledBoard.groupBy(_.month).collect {
                case (month, cards) if cards.size == 3 => month
            }.toSet
            val actualBoard = actualPolledBoard.map { card =>
                if (groupedMonths.contains(card.month)) card.copy(grouped = true)
                else card
            }
            val (playerList, updatedDeck) = initializePlayers(firstPlayer, secondPlayer, firstScore, secondScore, deck, customHandFirst = customHandFirst, customHandSec = customHandSec)
            /* Check if a player has been dealt an instant win combination */
            if (instantWinCombinations.exists(_.evaluate(playerList.head) > 0)
                || instantWinCombinations.exists(_.evaluate(playerList(1)) > 0)) {
                val firstS = calculateInstantWinScore(playerList.head)
                val secS = calculateInstantWinScore(playerList(1))
                handleKoiKoi(playerList, firstS, secS, deck = updatedDeck, board = Deck(actualBoard))
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

    /**
     * Initializes players with their respective cards and scores.
     *
     * @param firstPlayer the name of the first player
     * @param secondPlayer the name of the second player
     * @param firstScore the initial score of the first player
     * @param secondScore the initial score of the second player
     * @param deck the deck of cards
     * @param customHandFirst an optional custom hand for the first player
     * @param customHandSec an optional custom hand for the second player
     * @return a tuple containing the list of players and the updated deck
     */
    def initializePlayers(firstPlayer: String, secondPlayer: String, firstScore: Int, secondScore: Int, deck: Deck, customHandFirst: Option[Deck] = None, customHandSec: Option[Deck] = None): (List[Player], Deck) = {
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
                    calledKoiKoi = false,
                    yakusToIgnore = List.empty
                ), newDeck
                )
        }
    }

    /**
     * Handles the occurance of a yaku.
     * Either no player has called koi-koi before or one of them.
     * If no player has called, ask them for a decision -> GameStatePendingKoiKoi
     * If the player that just scored has already called koi-koi before, then double their points.
     * If the opponent of the player that just scored has already called koi-koi before,
     * then double the points of the one who just scored and set their opponents' to 0.
     * @param game the current game state
     * @return the new game state
     */
    def koiKoiHandler(game: GameState): GameState = {
        /* Player called koi-koi before */
        if (game.players.head.calledKoiKoi) {
            val (firstS, secS) = evaluateScore(game.players, 2, 1)
            handleKoiKoi(game.players, firstS, secS, game.board, game.deck)
        }
        /* Opponent called koi-koi before */
        else if (game.players(1).calledKoiKoi) {
            val (firstS, secS) = evaluateScore(game.players, 2, 0)
            handleKoiKoi(game.players, firstS, secS, game.board, game.deck)
        }
        /* No one called koi-koi before */
        else {
            val yakuList = yakuCombinations.filter(c => c.evaluate(game.players.head) > 0)
            GameStatePendingKoiKoi(
                players = List(
                    game.players.head.copy(
                        yakusToIgnore = yakuList
                    ), game.players(1)
                ),
                deck = game.deck,
                board = game.board,
                displayType = DisplayType.GAME,
                stdout = Some(s"You scored a yaku: \n${yakuList.map(c => s"\t- ${c.unicode}\n").mkString("\n")}You can now either finish or call koi-koi."),
                stderr = None
            )
        }
    }

    /**
     * Handles the finishing of a game.
     * Called when the game is finished.
     *
     * @param players the list of players
     * @param firstS the score of the first player
     * @param secS the score of the second player
     * @param board the current board
     * @param deck the current deck
     * @param outOfCardsEnding true if the game ended because the deck ran out of cards
     * @return the new GameState instanceOf[GameStateSummary]
     */
    def handleKoiKoi(players: List[Player], firstS: Int, secS: Int, board: Deck, deck: Deck, outOfCardsEnding: Boolean = false): GameStateSummary = {
        GameStateSummary(
            players = List(players.head.copy(score = firstS), players(1).copy(score = secS)),
            stdout = None, stderr = None,
            deck = deck,
            board = board,
            displayType = DisplayType.SUMMARY,
            outOfCardsEnding = outOfCardsEnding
        )
    }

    /**
     * Handles the call for koi-koi.
     * Called when a player decides to continue after scoring a yaku.
     * (if koi-koi has not been previously called)
     *
     * @param game the current game state
     * @return the new game state
     */
    def koiKoiCallHandler(game: GameState): GameState = {
        GameStatePlanned(
            players = List(game.players(1), game.players.head.copy(calledKoiKoi = true)),
            deck = game.deck,
            board = game.board,
            displayType = DisplayType.GAME,
            stdout = Some("Continuing game."),
            stderr = None
        )
    }

    /**
     * Calculates the instant win score for a player.
     *
     * @param player the player
     * @return the instant win score
     */
    def calculateInstantWinScore(player: Player): Int = {
        instantWinCombinations.foldLeft(0) {
            case (acc, yaku) =>
                acc + yaku.evaluate(player)
        }
    }

    /**
     * Evaluates the score for each player.
     *
     * @param players the list of players
     * @param multiplierFirst the multiplier for the first player
     * @param multiplierSec the multiplier for the second player
     * @return a tuple containing the scores of the first and second players
     */
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

    /**
     * Checks if a player has scored a *new* yaku.
     *
     * @param player the player
     * @return true if the player has scored a new yaku, false otherwise
     */
    def playerHasScoredNewYaku(player: Player): Boolean = {
        yakuCombinations.exists(_.evaluate(player) > 0)
            && yakuCombinations.filter(_.evaluate(player) > 0) != player.yakusToIgnore
    }
}