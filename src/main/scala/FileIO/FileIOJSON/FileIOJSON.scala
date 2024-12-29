package FileIO.FileIOJSON

import FileIO.FileIO
import model.*
import play.api.libs.json.*
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import model.GameManager.GameManagerInstance.given_GameManager

import java.io.PrintWriter
import scala.io.Source

class FileIOJSON extends FileIO {

    override def save(gameState: GameState): Boolean = {
        val json = Json.obj(
            "instanceOf" -> gameState.getClass.toString,
            "players" -> gameState.players.map { p =>
                Json.obj(
                    "name" -> p.name,
                    "hand" -> Json.obj(
                        "cards" -> p.hand.cards.map { c =>
                            Json.obj(
                                "month"    -> c.month.toString,
                                "cardType" -> c.cardType.toString,
                                "cardName" -> c.cardName.toString,
                                "index"    -> c.index,
                                "grouped"  -> c.grouped
                            )
                        }
                    ),
                    "side" -> Json.obj(
                        "cards" -> p.side.cards.map { c =>
                            Json.obj(
                                "month"    -> c.month.toString,
                                "cardType" -> c.cardType.toString,
                                "cardName" -> c.cardName.toString,
                                "index"    -> c.index,
                                "grouped"  -> c.grouped
                            )
                        }
                    ),
                    "score"        -> p.score,
                    "calledKoiKoi" -> p.calledKoiKoi,
                    "yakusToIgnore" -> p.yakusToIgnore.map { y =>
                        Json.obj("yaku" -> y.unicode)
                    }
                )
            },
            "deck" -> Json.obj(
                "cards" -> gameState.deck.cards.map { c =>
                    Json.obj(
                        "month"    -> c.month.toString,
                        "cardType" -> c.cardType.toString,
                        "cardName" -> c.cardName.toString,
                        "index"    -> c.index,
                        "grouped"  -> c.grouped
                    )
                }
            ),
            "board" -> Json.obj(
                "cards" -> gameState.board.cards.map { c =>
                    Json.obj(
                        "month"    -> c.month.toString,
                        "cardType" -> c.cardType.toString,
                        "cardName" -> c.cardName.toString,
                        "index"    -> c.index,
                        "grouped"  -> c.grouped
                    )
                }
            ),
            "stdout"       -> gameState.stdout,
            "stderr"       -> gameState.stderr,
            "displayType"  -> gameState.displayType.toString,
            "matchedDeck"  -> (if gameState.matchedDeck.isEmpty then JsNull else Json.toJson {
                gameState.matchedDeck.get.cards.map { c =>
                    Json.obj(
                        "month"    -> c.month.toString,
                        "cardType" -> c.cardType.toString,
                        "cardName" -> c.cardName.toString,
                        "index"    -> c.index,
                        "grouped"  -> c.grouped
                    )
                }
            }),
            "queuedCard" -> (if gameState.queuedCard.isEmpty then JsNull else {
                val c = gameState.queuedCard.get
                Json.obj(
                    "month"    -> c.month.toString,
                    "cardType" -> c.cardType.toString,
                    "cardName" -> c.cardName.toString,
                    "index"    -> c.index,
                    "grouped"  -> c.grouped
                )
            })
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

    implicit val gameStateUninitializedReads: Reads[GameStateUninitialized] = Json.reads[GameStateUninitialized]
    implicit val gameStateRandomReads: Reads[GameStateRandom] = (
      (JsPath \ "players").read[List[Player]] and
        (JsPath \ "deck" \ "cards").read[List[Card]].map(Deck.apply) and
        (JsPath \ "board" \ "cards").read[List[Card]].map(Deck.apply) and
        (JsPath \ "matched" \ "cards").read[List[Card]].map(Deck.apply) and
        (JsPath \ "queued").read[Card] and
        (JsPath \ "displayType").read[DisplayType] and
        (JsPath \ "stdout").readNullable[String] and
        (JsPath \ "stderr").readNullable[String]
      )(GameStateRandom.apply _)
    implicit val gameStatePendingKoiKoiReads: Reads[GameStatePendingKoiKoi] = Json.reads[GameStatePendingKoiKoi]
    implicit val gameStateSummaryReads: Reads[GameStateSummary] = Json.reads[GameStateSummary]

    override def load: GameState = {
        val source: String = Source.fromFile("gameState.json").getLines().mkString
        val json: JsValue = Json.parse(source)

        val parseResult: JsResult[GameState] = (json \ "instanceOf").validate[String].flatMap {
            case "class model.GameStateUninitialized" =>
                json.validate[GameStateUninitialized]

            case "class model.GameStateRandom" =>
                json.validate[GameStateRandom]

            case "class model.GameStatePlanned" =>
                json.validate[GameStatePlanned]

            case "class model.GameStatePendingKoiKoi" =>
                json.validate[GameStatePendingKoiKoi]

            case "class model.GameStateSummary" =>
                json.validate[GameStateSummary]

            case other =>
                JsError(s"Unknown GameState: $other")
        }

        parseResult.fold(
            errors => throw new IllegalArgumentException(s"Invalid game state JSON: $errors"),
            validGameState => validGameState
        )
    }

    implicit val displayTypeReads: Reads[DisplayType] = (json: JsValue) =>
        json.validate[String].map {
            case "GAME"         => DisplayType.GAME
            case "COMBINATIONS" => DisplayType.COMBINATIONS
            case "HELP"         => DisplayType.HELP
            case "SPOILER"      => DisplayType.SPOILER
            case "SUMMARY"      => DisplayType.SUMMARY
            case other          => throw new IllegalArgumentException(s"Unknown DisplayType: $other")
        }

    implicit val cardMonthReads: Reads[CardMonth] = (json: JsValue) =>
        json.validate[String].map(CardMonth.valueOf)

    implicit val cardTypeReads: Reads[CardType] = (json: JsValue) =>
        json.validate[String].map(CardType.valueOf)

    implicit val cardNameReads: Reads[CardName] = (json: JsValue) =>
        json.validate[String].map(CardName.valueOf)

    implicit val cardReads: Reads[Card] = Json.reads[Card]

    implicit val deckReads: Reads[Deck] = Json.reads[Deck]
    implicit val gokoCombinationReads: Reads[GokoCombination.type] = Reads(_ => JsSuccess(GokoCombination))
    implicit val shikoCombinationReads: Reads[ShikoCombination.type] = Reads(_ => JsSuccess(ShikoCombination))
    implicit val ameShikoCombinationReads: Reads[AmeShikoCombination.type] = Reads(_ => JsSuccess(AmeShikoCombination))
    implicit val sankoCombinationReads: Reads[SankoCombination.type] = Reads(_ => JsSuccess(SankoCombination))
    implicit val tsukimiZakeCombinationReads: Reads[TsukimiZakeCombination.type] = Reads(_ => JsSuccess(TsukimiZakeCombination))
    implicit val hanamiZakeCombinationReads: Reads[HanamiZakeCombination.type] = Reads(_ => JsSuccess(HanamiZakeCombination))
    implicit val inoshiKachoCombinationReads: Reads[InoshiKachoCombination.type] = Reads(_ => JsSuccess(InoshiKachoCombination))
    implicit val taneCombinationReads: Reads[TaneCombination.type] = Reads(_ => JsSuccess(TaneCombination))
    implicit val akatanAotanCombinationReads: Reads[AkatanAotanCombination.type] = Reads(_ => JsSuccess(AkatanAotanCombination))
    implicit val akatanCombinationReads: Reads[AkatanCombination.type] = Reads(_ => JsSuccess(AkatanCombination))
    implicit val aotanCombinationReads: Reads[AotanCombination.type] = Reads(_ => JsSuccess(AotanCombination))
    implicit val tanzakuCombinationReads: Reads[TanzakuCombination.type] = Reads(_ => JsSuccess(TanzakuCombination))
    implicit val kasuCombinationReads: Reads[KasuCombination.type] = Reads(_ => JsSuccess(KasuCombination))
    implicit val teshiCombinationReads: Reads[TeshiCombination.type] = Reads(_ => JsSuccess(TeshiCombination))
    implicit val kuttsukiCombinationReads: Reads[KuttsukiCombination.type] = Reads(_ => JsSuccess(KuttsukiCombination))

    implicit val combinationReads: Reads[Combination] = Reads {
        case JsString("GokoCombination") => JsSuccess(GokoCombination)
        case JsString("ShikoCombination") => JsSuccess(ShikoCombination)
        case JsString("AmeShikoCombination") => JsSuccess(AmeShikoCombination)
        case JsString("SankoCombination") => JsSuccess(SankoCombination)
        case JsString("TsukimiZakeCombination") => JsSuccess(TsukimiZakeCombination)
        case JsString("HanamiZakeCombination") => JsSuccess(HanamiZakeCombination)
        case JsString("InoshiKachoCombination") => JsSuccess(InoshiKachoCombination)
        case JsString("TaneCombination") => JsSuccess(TaneCombination)
        case JsString("AkatanAotanCombination") => JsSuccess(AkatanAotanCombination)
        case JsString("AkatanCombination") => JsSuccess(AkatanCombination)
        case JsString("AotanCombination") => JsSuccess(AotanCombination)
        case JsString("TanzakuCombination") => JsSuccess(TanzakuCombination)
        case JsString("KasuCombination") => JsSuccess(KasuCombination)
        case JsString("TeshiCombination") => JsSuccess(TeshiCombination)
        case JsString("KuttsukiCombination") => JsSuccess(KuttsukiCombination)
        case _ => JsError("Unknown Combination")
    }

    implicit val playerReads: Reads[Player] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "hand" \ "cards").read[List[Card]].map(Deck.apply) and
        (JsPath \ "side" \ "cards").read[List[Card]].map(Deck.apply) and
        (JsPath \ "score").read[Int] and
        (JsPath \ "calledKoiKoi").read[Boolean] and
        (JsPath \ "yakusToIgnore").read[List[Combination]]
      )(Player.apply _)

    implicit val gameStatePlannedReads: Reads[GameStatePlanned] = (
      (JsPath \ "players").read[List[Player]] and
        (JsPath \ "deck" \ "cards").read[List[Card]].map(Deck.apply) and
        (JsPath \ "board" \ "cards").read[List[Card]].map(Deck.apply) and
        (JsPath \ "displayType").read[DisplayType] and
        (JsPath \ "stdout").readNullable[String] and
        (JsPath \ "stderr").readNullable[String]
      )(GameStatePlanned.apply _)
    
}
/*
package FileIO.FileIOJSON

import FileIO.FileIO
import model.{AkatanAotanCombination, AkatanCombination, AmeShikoCombination, AotanCombination, Card, CardMonth, CardName, CardType, Combination, Deck, DisplayType, GameState, GameStatePendingKoiKoi, GameStatePlanned, GameStateRandom, GameStateSummary, GameStateUninitialized, GokoCombination, HanamiZakeCombination, InoshiKachoCombination, KasuCombination, KuttsukiCombination, Player, SankoCombination, ShikoCombination, TaneCombination, TanzakuCombination, TeshiCombination, TsukimiZakeCombination}
import play.api.libs.json.*
import model.GameManager.GameManagerInstance.given_GameManager
import play.api.libs.functional.syntax.toFunctionalBuilderOps

import java.io.PrintWriter
import scala.io.Source
import scala.language.postfixOps

class FileIOJSON extends FileIO {

    /**
     * Save the current GameState as JSON into gameState.json
     */
    override def save(gameState: GameState): Boolean = {
        val json = Json.obj(
            "instanceOf" -> gameState.getClass.toString,
            "players" -> gameState.players.map { p =>
                Json.obj(
                    "name" -> p.name,
                    "hand" -> Json.obj(
                        "deck" -> p.hand.cards.map { c =>
                            Json.obj(
                                "month"    -> c.month.toString,
                                "cardType" -> c.cardType.toString,
                                "cardName" -> c.cardName.toString,
                                "index"    -> c.index,
                                "grouped"  -> c.grouped
                            )
                        }
                    ),
                    "side" -> Json.obj(
                        "deck" -> p.side.cards.map { c =>
                            Json.obj(
                                "month"    -> c.month.toString,
                                "cardType" -> c.cardType.toString,
                                "cardName" -> c.cardName.toString,
                                "index"    -> c.index,
                                "grouped"  -> c.grouped
                            )
                        }
                    ),
                    "score"        -> p.score,
                    "calledKoiKoi" -> p.calledKoiKoi,
                    "yakusToIgnore" -> p.yakusToIgnore.map { y =>
                        Json.obj("yaku" -> y.unicode)
                    }
                )
            },
            "deck" -> gameState.deck.cards.map { c =>
                Json.obj(
                    "month"    -> c.month.toString,
                    "cardType" -> c.cardType.toString,
                    "cardName" -> c.cardName.toString,
                    "index"    -> c.index,
                    "grouped"  -> c.grouped
                )
            },
            "board" -> gameState.board.cards.map { c =>
                Json.obj(
                    "month"    -> c.month.toString,
                    "cardType" -> c.cardType.toString,
                    "cardName" -> c.cardName.toString,
                    "index"    -> c.index,
                    "grouped"  -> c.grouped
                )
            },
            "stdout"       -> gameState.stdout,
            "stderr"       -> gameState.stderr,
            "displayType"  -> gameState.displayType.toString,
            "matchedDeck"  -> (if gameState.matchedDeck.isEmpty then JsNull else Json.toJson {
                gameState.matchedDeck.get.cards.map { c =>
                    Json.obj(
                        "month"    -> c.month.toString,
                        "cardType" -> c.cardType.toString,
                        "cardName" -> c.cardName.toString,
                        "index"    -> c.index,
                        "grouped"  -> c.grouped
                    )
                }
            }),
            "queuedCard" -> (if gameState.queuedCard.isEmpty then JsNull else {
                val c = gameState.queuedCard.get
                Json.obj(
                    "month"    -> c.month.toString,
                    "cardType" -> c.cardType.toString,
                    "cardName" -> c.cardName.toString,
                    "index"    -> c.index,
                    "grouped"  -> c.grouped
                )
            })
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

    /**
     * Below are some example implicit Reads for your types.
     * Adjust to match your model objects' actual case class shapes.
     */
    implicit val displayTypeReads: Reads[DisplayType] = (json: JsValue) =>
        json.validate[String].map {
            case "GAME"         => DisplayType.GAME
            case "COMBINATIONS" => DisplayType.COMBINATIONS
            case "HELP"         => DisplayType.HELP
            case "SPOILER"      => DisplayType.SPOILER
            case "SUMMARY"      => DisplayType.SUMMARY
            case other          => throw new IllegalArgumentException(s"Unknown DisplayType: $other")
        }

    implicit val cardMonthReads: Reads[CardMonth] = (json: JsValue) =>
        json.validate[String].map(CardMonth.valueOf)

    implicit val cardTypeReads: Reads[CardType] = (json: JsValue) =>
        json.validate[String].map(CardType.valueOf)

    implicit val cardNameReads: Reads[CardName] = (json: JsValue) =>
        json.validate[String].map(CardName.valueOf)
    
    implicit val cardReads: Reads[Card] = Json.reads[Card]
    
    implicit val deckReads: Reads[Deck] = Json.reads[Deck]

    implicit val gokoCombinationReads: Reads[GokoCombination.type] = Reads(_ => JsSuccess(GokoCombination))
    implicit val shikoCombinationReads: Reads[ShikoCombination.type] = Reads(_ => JsSuccess(ShikoCombination))
    implicit val ameShikoCombinationReads: Reads[AmeShikoCombination.type] = Reads(_ => JsSuccess(AmeShikoCombination))
    implicit val sankoCombinationReads: Reads[SankoCombination.type] = Reads(_ => JsSuccess(SankoCombination))
    implicit val tsukimiZakeCombinationReads: Reads[TsukimiZakeCombination.type] = Reads(_ => JsSuccess(TsukimiZakeCombination))
    implicit val hanamiZakeCombinationReads: Reads[HanamiZakeCombination.type] = Reads(_ => JsSuccess(HanamiZakeCombination))
    implicit val inoshiKachoCombinationReads: Reads[InoshiKachoCombination.type] = Reads(_ => JsSuccess(InoshiKachoCombination))
    implicit val taneCombinationReads: Reads[TaneCombination.type] = Reads(_ => JsSuccess(TaneCombination))
    implicit val akatanAotanCombinationReads: Reads[AkatanAotanCombination.type] = Reads(_ => JsSuccess(AkatanAotanCombination))
    implicit val akatanCombinationReads: Reads[AkatanCombination.type] = Reads(_ => JsSuccess(AkatanCombination))
    implicit val aotanCombinationReads: Reads[AotanCombination.type] = Reads(_ => JsSuccess(AotanCombination))
    implicit val tanzakuCombinationReads: Reads[TanzakuCombination.type] = Reads(_ => JsSuccess(TanzakuCombination))
    implicit val kasuCombinationReads: Reads[KasuCombination.type] = Reads(_ => JsSuccess(KasuCombination))
    implicit val teshiCombinationReads: Reads[TeshiCombination.type] = Reads(_ => JsSuccess(TeshiCombination))
    implicit val kuttsukiCombinationReads: Reads[KuttsukiCombination.type] = Reads(_ => JsSuccess(KuttsukiCombination))

    implicit val combinationReads: Reads[Combination] = Reads {
        case JsString("GokoCombination") => JsSuccess(GokoCombination)
        case JsString("ShikoCombination") => JsSuccess(ShikoCombination)
        case JsString("AmeShikoCombination") => JsSuccess(AmeShikoCombination)
        case JsString("SankoCombination") => JsSuccess(SankoCombination)
        case JsString("TsukimiZakeCombination") => JsSuccess(TsukimiZakeCombination)
        case JsString("HanamiZakeCombination") => JsSuccess(HanamiZakeCombination)
        case JsString("InoshiKachoCombination") => JsSuccess(InoshiKachoCombination)
        case JsString("TaneCombination") => JsSuccess(TaneCombination)
        case JsString("AkatanAotanCombination") => JsSuccess(AkatanAotanCombination)
        case JsString("AkatanCombination") => JsSuccess(AkatanCombination)
        case JsString("AotanCombination") => JsSuccess(AotanCombination)
        case JsString("TanzakuCombination") => JsSuccess(TanzakuCombination)
        case JsString("KasuCombination") => JsSuccess(KasuCombination)
        case JsString("TeshiCombination") => JsSuccess(TeshiCombination)
        case JsString("KuttsukiCombination") => JsSuccess(KuttsukiCombination)
        case _ => JsError("Unknown Combination")
    }

    implicit val combinationWrites: Writes[Combination] = Writes {
        case GokoCombination => JsString("GokoCombination")
        case ShikoCombination => JsString("ShikoCombination")
        case AmeShikoCombination => JsString("AmeShikoCombination")
        case SankoCombination => JsString("SankoCombination")
        case TsukimiZakeCombination => JsString("TsukimiZakeCombination")
        case HanamiZakeCombination => JsString("HanamiZakeCombination")
        case InoshiKachoCombination => JsString("InoshiKachoCombination")
        case TaneCombination => JsString("TaneCombination")
        case AkatanAotanCombination => JsString("AkatanAotanCombination")
        case AkatanCombination => JsString("AkatanCombination")
        case AotanCombination => JsString("AotanCombination")
        case TanzakuCombination => JsString("TanzakuCombination")
        case KasuCombination => JsString("KasuCombination")
        case TeshiCombination => JsString("TeshiCombination")
        case KuttsukiCombination => JsString("KuttsukiCombination")
    }
    
    implicit val playerReads: Reads[Player] = Json.reads[Player]

    // For each GameState subtype, make sure you have a matching case class with the same fields
    implicit val gameStateUninitializedReads: Reads[GameStateUninitialized] = Json.reads[GameStateUninitialized]
    implicit val gameStateRandomReads: Reads[GameStateRandom] = (
      (JsPath \ "players").read[List[Player]] and
        (JsPath \ "deck").read[Deck] and
        (JsPath \ "board").read[Deck] and
        (JsPath \ "matched").read[Deck] and
        (JsPath \ "queued").read[Card] and
        (JsPath \ "displayType").read[DisplayType] and
        (JsPath \ "stdout").readNullable[String] and
        (JsPath \ "stderr").readNullable[String]
      )(GameStateRandom.apply _)
    implicit val gameStatePlannedReads: Reads[GameStatePlanned] = Json.reads[GameStatePlanned]
    implicit val gameStatePendingKoiKoiReads: Reads[GameStatePendingKoiKoi] = Json.reads[GameStatePendingKoiKoi]
    implicit val gameStateSummaryReads: Reads[GameStateSummary] = Json.reads[GameStateSummary]

    /**
     * Load the GameState from gameState.json
     * Checks "instanceOf" to know which subtype to deserialize.
     */
    override def load: GameState = {
        val source: String = Source.fromFile("gameState.json").getLines().mkString
        val json: JsValue = Json.parse(source)

        // Read the class name from "instanceOf" and dispatch to the correct case
        val parseResult: JsResult[GameState] = (json \ "instanceOf").validate[String].flatMap {
            case "class model.GameStateUninitialized" =>
                json.validate[GameStateUninitialized]

            case "class model.GameStateRandom" =>
                json.validate[GameStateRandom]

            case "class model.GameStatePlanned" =>
                json.validate[GameStatePlanned]

            case "class model.GameStatePendingKoiKoi" =>
                json.validate[GameStatePendingKoiKoi]

            case "class model.GameStateSummary" =>
                json.validate[GameStateSummary]

            case other =>
                JsError(s"Unknown GameState: $other")
        }

        // Throw an error if we cannot parse
        parseResult.fold(
            errors => throw new IllegalArgumentException(s"Invalid game state JSON: $errors"),
            validGameState => validGameState
        )
    }
}

 */



/*
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

 */
