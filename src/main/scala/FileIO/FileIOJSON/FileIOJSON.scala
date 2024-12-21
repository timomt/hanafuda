package FileIO.FileIOJSON

import FileIO.FileIO
import model.{Card, CardMonth, CardName, CardType, Combination, Deck, DisplayType, GameState, GameStatePendingKoiKoi, GameStatePlanned, GameStateRandom, GameStateSummary, GameStateUninitialized, Player}
import play.api.libs.json.{Format, JsError, JsResult, JsValue, Json, Reads, Writes}
import model.GameManager.GameManagerInstance.given_GameManager

import java.io.PrintWriter
import scala.io.Source
import scala.language.postfixOps

class FileIOJSON extends FileIO {
    override def save(gameState: GameState): Boolean = {
        val json = Json.obj(
            "instanceOf" -> gameState.getClass.toString,
            "players" -> gameState.players.map(p =>
                Json.obj(
                    "name" -> p.name,
                    "hand" -> Json.obj(
                        "deck" -> p.hand.cards.map(c => Json.obj("month" -> c.month.toString, "cardType" -> c.cardType.toString, "cardName" -> c.cardName.toString, "index" ->  c.index, "grouped" -> c.grouped))
                    ),
                    "side" -> Json.obj(
                        "deck" -> p.side.cards.map(c => Json.obj("month" -> c.month.toString, "cardType" -> c.cardType.toString, "cardName" -> c.cardName.toString, "index" ->  c.index, "grouped" -> c.grouped))
                    ),
                    "score" -> p.score,
                    "calledKoiKoi" -> p.calledKoiKoi,
                    "yakusToIgnore" -> p.yakusToIgnore.map(y => Json.obj("yaku" -> y.unicode))
                )
            ),
            "deck" -> gameState.deck.cards.map(c => Json.obj("month" -> c.month.toString, "cardType" -> c.cardType.toString, "cardName" -> c.cardName.toString, "index" ->  c.index, "grouped" -> c.grouped)),
            "board" -> gameState.board.cards.map(c => Json.obj("month" -> c.month.toString, "cardType" -> c.cardType.toString, "cardName" -> c.cardName.toString, "index" ->  c.index, "grouped" -> c.grouped)),
            "stdout" -> gameState.stdout,
            "stderr" -> gameState.stderr,
            "displayType" -> gameState.displayType.toString,
            if gameState.matchedDeck.isEmpty then "matchedDeck" -> null else "matchedDeck" -> gameState.matchedDeck.get.cards.map(c => Json.obj("month" -> c.month.toString, "cardType" -> c.cardType.toString, "cardName" -> c.cardName.toString, "index" ->  c.index, "grouped" -> c.grouped)),
            if gameState.queuedCard.isEmpty then "queuedCard" -> null else "queuedCard" -> Json.obj("month" -> gameState.queuedCard.get.month.toString, "cardType" -> gameState.queuedCard.get.cardType.toString, "cardName" -> gameState.queuedCard.get.cardName.toString, "index" ->  gameState.queuedCard.get.index, "grouped" -> gameState.queuedCard.get.grouped)
        )
        try {
            val writer = new PrintWriter("gameState.json")
            writer.write(Json.prettyPrint(json))
            writer.close()
            true
        } catch {
            case e: Exception =>
                e.printStackTrace()
                false
        }
    }

    /*
    implicit val displayTypeReads: Reads[DisplayType] = (json: JsValue) => json.validate[String].map {
        case "GAME" => DisplayType.GAME
        case "COMBINATIONS" => DisplayType.COMBINATIONS
        case "HELP" => DisplayType.HELP
        case "SPOILER" => DisplayType.SPOILER
        case "SUMMARY" => DisplayType.SUMMARY
        case other => throw new IllegalArgumentException(s"Unknown DisplayType: $other")
    }
    implicit val cardMonthReads: Reads[CardMonth] = (json: JsValue) => json.validate[String].map(CardMonth.valueOf)
    implicit val cardTypeReads: Reads[CardType] = (json: JsValue) => json.validate[String].map(CardType.valueOf)
    implicit val cardNameReads: Reads[CardName] = (json: JsValue) => json.validate[String].map(CardName.valueOf)
    implicit val cardReads: Reads[Card] = Json.reads[Card]
    implicit val deckReads: Reads[Deck] = Json.reads[Deck]
    implicit val combinationFormat: Format[Combination] = Json.format[Combination]
    implicit val combinationReads: Reads[Combination] = Json.reads[Combination]
    implicit val listCombinationReads: Reads[List[Combination]] = Reads.list[Combination]
    implicit val playerReads: Reads[Player] = Json.reads[Player]
    implicit val gameStateUninitializedReads: Reads[GameStateUninitialized] = Json.reads[GameStateUninitialized]
    implicit val gameStateRandomReads: Reads[GameStateRandom] = Json.reads[GameStateRandom]
    implicit val gameStatePlannedReads: Reads[GameStatePlanned] = Json.reads[GameStatePlanned]
    implicit val gameStatePendingKoiKoiReads: Reads[GameStatePendingKoiKoi] = Json.reads[GameStatePendingKoiKoi]
    implicit val gameStateSummaryReads: Reads[GameStateSummary] = Json.reads[GameStateSummary]*/

    // Implement the load method
    def load: GameState = {
        ???
        /*val source: String = Source.fromFile("gameState.json").getLines.mkString
        val json: JsValue = Json.parse(source)

        (json \ "instanceOf").validate[String].flatMap {
            case "class model.GameStateUninitialized" => json.validate[GameStateUninitialized]
            case "class model.GameStateRandom" => json.validate[GameStateRandom]
            case "class model.GameStatePlanned" => json.validate[GameStatePlanned]
            case "class model.GameStatePendingKoiKoi" => json.validate[GameStatePendingKoiKoi]
            case "class model.GameStateSummary" => json.validate[GameStateSummary]
            case other => JsError(s"Unknown GameState: $other")
        }.getOrElse(throw new IllegalArgumentException("Invalid game state JSON"))*/
    }
}