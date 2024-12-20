package model.GameManager

import model.*

trait GameManager {
    def newGame(firstPlayer: String, secondPlayer: String, firstScore: Int = 0, secondScore: Int = 0, customBoard: Option[List[Card]] = None, customHandFirst: Option[Deck] = None, customHandSec: Option[Deck] = None): GameState
    def initializePlayers(firstPlayer: String, secondPlayer: String, firstScore: Int, secondScore: Int, deck: Deck, customHandFirst: Option[Deck] = None, customHandSec: Option[Deck] = None): (List[Player], Deck)
    def koiKoiHandler(game: GameState): GameState
    def handleKoiKoi(players: List[Player], firstS: Int, secS: Int, board: Deck, deck: Deck, outOfCardsEnding: Boolean = false): GameStateSummary
    def koiKoiCallHandler(game: GameState): GameState
    def calculateInstantWinScore(player: Player): Int
    def evaluateScore(players: List[Player], multiplierFirst: Int, multiplierSec: Int): (Int, Int)
    def playerHasScoredNewYaku(player: Player): Boolean
}
