package FileIO.FileIOXML


import FileIO.FileIO
import controller.GameController.gameState
import model.*

import scala.xml.{Elem, Node, NodeSeq, PrettyPrinter}
import java.io.{File, PrintWriter}
import scala.io.Source
import model.GameManager.GameManagerInstance.given_GameManager
import model.Combination

class FileIOXML extends FileIO {

  override def save(gameState: GameState): Boolean = {
    val xml = gameStateToXml(gameState)
    try {
      val writer = new PrintWriter(new File("gameState.xml"))
      writer.write(new PrettyPrinter(80, 2).format(xml))
      writer.close()
      true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        false
    }
  }

  override def load: GameState = {
    val source = Source.fromFile("gameState.xml").getLines().mkString
    val xml = scala.xml.XML.loadString(source)
    xmlToGameState(xml)
  }

  private def gameStateToXml(gameState: GameState): Elem = {
    <gameState>
      <instanceOf>{gameState.getClass.toString}</instanceOf>
      <players>
        {gameState.players.map(playerToXml)}
      </players>
      <deck>
        {gameState.deck.cards.map(cardToXml)}
      </deck>
      <board>
        {gameState.board.cards.map(cardToXml)}
      </board>
      <stdout>{gameState.stdout.getOrElse("")}</stdout>
      <stderr>{gameState.stderr.getOrElse("")}</stderr>
      <displayType>{gameState.displayType.toString}</displayType>
      <matchedDeck>
        {gameState.matchedDeck.map(_.cards.map(cardToXml)).getOrElse(Seq.empty)}
      </matchedDeck>
      <queuedCard>
        {gameState.queuedCard.map(cardToXml).getOrElse(NodeSeq.Empty)}
      </queuedCard>
    </gameState>
  }

  private def xmlToGameState(xml: Node): GameState = {
    val instanceOf = (xml \ "instanceOf").text
    val players = (xml \ "players" \ "player").map(xmlToPlayer).toList
    val deck = Deck((xml \ "deck" \ "card").map(xmlToCard).toList)
    val board = Deck((xml \ "board" \ "card").map(xmlToCard).toList)
    val stdout = Option((xml \ "stdout").text).filter(_.nonEmpty)
    val stderr = Option((xml \ "stderr").text).filter(_.nonEmpty)
    val displayType = DisplayType.valueOf((xml \ "displayType").text)
    val matchedDeck = Option(Deck((xml \ "matchedDeck" \ "card").map(xmlToCard).toList))
    val queuedCard = (xml \ "queuedCard" \ "card").headOption.map(xmlToCard).getOrElse(throw new NoSuchElementException("No card found in queuedCard"))

    instanceOf match {
      case "class model.GameStateUninitialized" => GameStateUninitialized(displayType, stderr)
      case "class model.GameStateRandom" => GameStateRandom(players, deck, board, matchedDeck.get, queuedCard, displayType, stdout, stderr)
      // case class GameStateRandom(players: List[Player], deck: Deck, board: Deck, matched: Deck, queued: Card, displayType: DisplayType = DisplayType.GAME,
      //                            stdout: Option[String], stderr: Option[String])(using gameManager: GameManager)
      case "class model.GameStatePlanned" => GameStatePlanned(players, deck, board, displayType, stdout, stderr)
      case "class model.GameStatePendingKoiKoi" => GameStatePendingKoiKoi(players, deck, board, displayType, stdout, stderr)
      case "class model.GameStateSummary" => GameStateSummary(players, deck, board, displayType, stdout, stderr, outOfCardsEnding = false)
      case other => throw new IllegalArgumentException(s"Unknown GameState: $other")
    }
  }

  private def playerToXml(player: Player): Elem = {
    <player>
      <name>{player.name}</name>
      <hand>
        {player.hand.cards.map(cardToXml)}
      </hand>
      <side>
        {player.side.cards.map(cardToXml)}
      </side>
      <score>{player.score}</score>
      <calledKoiKoi>{player.calledKoiKoi}</calledKoiKoi>
      <yakusToIgnore>
        {player.yakusToIgnore.map(combinationToXml)}
      </yakusToIgnore>
    </player>
  }

  private def xmlToPlayer(xml: Node): Player = {
    val name = (xml \ "name").text
    val hand = Deck((xml \ "hand" \ "card").map(xmlToCard).toList)
    val side = Deck((xml \ "side" \ "card").map(xmlToCard).toList)
    val score = (xml \ "score").text.toInt
    val calledKoiKoi = (xml \ "calledKoiKoi").text.toBoolean
    val yakusToIgnore = (xml \ "yakusToIgnore" \ "combination").map(xmlToCombination).toList
    Player(name, hand, side, score, calledKoiKoi, yakusToIgnore)
  }

  private def cardToXml(card: Card): Elem = {
    <card>
      <month>{card.month.toString}</month>
      <cardType>{card.cardType.toString}</cardType>
      <cardName>{card.cardName.toString}</cardName>
      <index>{card.index}</index>
      <grouped>{card.grouped}</grouped>
    </card>
  }

  private def xmlToCard(xml: Node): Card = {
    val month = CardMonth.valueOf((xml \ "month").text)
    val cardType = CardType.valueOf((xml \ "cardType").text)
    val cardName = CardName.valueOf((xml \ "cardName").text)
    val index = (xml \ "index").text.toInt
    val grouped = (xml \ "grouped").text.toBoolean
    Card(month, cardType, cardName, grouped, index)
  }

  private def combinationToXml(combination: Combination): Elem = {
    <combination>{combination.toString}</combination>
  }

  private def xmlToCombination(xml: Node): Combination = {
    val combinationName = (xml \ "combination").text
    val combinationClass = Class.forName(s"model.$combinationName$$")
    val combinationObject = combinationClass.getField("MODULE$").get(null).asInstanceOf[Combination]
    combinationObject
  }
}