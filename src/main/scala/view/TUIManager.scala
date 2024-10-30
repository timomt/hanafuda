package view
import controller.Observer
import model.{Card, CardName, CardType, Deck, GameState}

/*
* MVC: View
* object TUIManager
* an object to manage the text user interface.
* */
object TUIManager extends Observer {
    /*
    * def update(...)
    * updates the TUI according to the current GameState.
    * */
    override def update(gameState: GameState): Unit = {
        println(printBoard(gameState))
    }
    
    /*
    * def printBoard(...)
    * returns a String representation of the provided GameState.
    * */
    private def printBoard(game: GameState): String = {
        val card = s"""╔══════╗
                      |║      ║
                      |║      ║
                      |║      ║
                      |╚══════╝
                      |""".stripMargin.split("\n").toList
        val cardSpacer = ((" " * 8 + "\n") * 4 + " " * 8).split("\n").toList

        val topRow = List.fill(8)(card).transpose.map(_.mkString(" ")).mkString("\n")
        val upperMiddleRow = game.board.cards.slice(0, 4).map(_.unicode).prependedAll(List.fill(2)(cardSpacer)).transpose.map(_.mkString(" ")).mkString("\n")
        val lowerMiddleRow = game.board.cards.slice(4, 8).map(_.unicode).prependedAll(List.fill(2)(cardSpacer)).transpose.map(_.mkString(" ")).mkString("\n")
        val bottomRow = game.players.head.hand.cards.map(_.unicode).transpose.map(_.mkString(" ")).mkString("\n")

        "\u001b[2J\u001b[1;1H" + s"Current player: ${game.players.head.name}\n"
            + topRow + "\n\n" + upperMiddleRow + "\n" + lowerMiddleRow + "\n\n" + bottomRow
    }

    /*
    * def printOverview(...)
    * returns a String representation of the overview of all (un)collected cards and their value.
    * TODO: implement all rules for card combinations and display them accordingly
    * */
    def printOverview(game: GameState): String = {
        "\u001b[2J\u001b[1;1H" + "Hanafuda Overview\ncollectible,\u001b[32m mine\u001b[0m,\u001b[31m theirs\u001b[0m\n\n"
            + "Gokō (五光) \"Five Hikari\"\t10pts.\n" + Deck.defaultDeck().cards.filter(_.cardType == CardType.HIKARI).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
            + "Shikō (四光) \"Four Hikari\"\t8pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
    }

    /*
    * def colorizeOverviewCard(...)
    * returns the colorized unicode representation of given card depending on who owns it.
    * */
    private def colorizeOverviewCard(game: GameState, card: Card): List[String] = card match {
        case c if game.players.head.side.cards.contains(card) => c.unicode.map(line => s"\u001b[32m$line\u001b[0m")
        case c if game.players(1).side.cards.contains(card) => c.unicode.map(line => s"\u001b[31m$line\u001b[0m")
        case _ => card.unicode.map(line => s"\u001b[0m$line\u001b[0m")
    }
}