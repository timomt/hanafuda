package view
import controller.Observer
import model.{Card, CardName, CardType, Deck, GameState}

/*
* MVC: View
* object TUIManager
* an object to manage the text user interface.
* */
object TUIManager extends Observer {
    val clearScreen: String = "\u001b[2J\u001b[3J\u001b[1;1H"
    
    //TODO: spoiler protection
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
    * */ //TODO: fix unlimited board size
    def printBoard(game: GameState): String = {
        val card = s"""╔══════╗
                      |║      ║
                      |║      ║
                      |║      ║
                      |╚══════╝
                      |""".stripMargin.split("\n").toList
        val cardSpacer = ((" " * 8 + "\n") * 4 + " " * 8).split("\n").toList

        val topRow = List.fill(game.players(1).hand.cards.size)(card).transpose.map(_.mkString(" ")).mkString("\n")

        val upperMiddleRow = game.board.cards.slice(0, 4).map(_.unicode)
            .prependedAll(game.queuedCard match {
                case Some(c) => List.fill(1)(cardSpacer).prepended(c.unicode)
                case None => List.fill(2)(cardSpacer)
            })
            .appendedAll(List.fill(5 - game.board.cards.slice(0, 4).size)(cardSpacer))
            .appendedAll(game.matchedDeck match {
                case Some(d) => d.cards.slice(0, d.cards.size/2).map(_.unicode)
                case None => List.fill(2)(cardSpacer)
            })
            .transpose.map(_.mkString(" ")).mkString("\n")

        val lowerMiddleRow = game.board.cards.slice(4, 8).map(_.unicode).prependedAll(List.fill(2)(cardSpacer))
            .appendedAll(List.fill(5 - game.board.cards.slice(4, 8).size)(cardSpacer))
            .appendedAll(game.matchedDeck match {
                case Some(d) => d.cards.slice(d.cards.size/2, d.cards.size).map(_.unicode)
                case None => List.fill(2)(cardSpacer)
            })
            .transpose.map(_.mkString(" ")).mkString("\n")

        val bottomRow = game.players.head.hand.cards.map(_.unicode).transpose.map(_.mkString(" ")).mkString("\n")

        val stdoutRow = game.stdout match {
            case Some(stdout) => s"\n[]: $stdout\n"
            case None => ""
        }

        val stderrRow = game.stderr match {
            case Some(stderr) => s"\n[error]: $stderr\n"
            case None => ""
        }

        clearScreen + s"Current player: ${game.players.head.name}\n"
            + topRow + "\n\n" + upperMiddleRow + "\n" + lowerMiddleRow + "\n\n" + bottomRow + stdoutRow + stderrRow
    }

    /*
    * def printHelp(...)
    * returns a String of the help page.
    * */
    def printHelp(): String = {
        val helpText =
            clearScreen + """
              |╔════════════════════════════════════════════════════════════════════════╗
              |║                                Hanafuda Help                           ║
              |╠════════════════════════════════════════════════════════════════════════╣
              |║ Welcome to Hanafuda! Here are the commands you can use:                ║
              |║                                                                        ║
              |║ 1. start <firstPlayer> <secondPlayer>                                  ║
              |║    - Starts a new game with the given player names.                    ║
              |║                                                                        ║
              |║ 2. continue                                                            ║
              |║    - return to the current game.                                       ║
              |║                                                                        ║
              |║ 2. match <x> <y>                                                       ║
              |║    - Matches cards at positions x and y on the board.                  ║
              |║                                                                        ║
              |║ 3. test colors                                                         ║
              |║    - Tests the colors of the cards.                                    ║
              |║                                                                        ║
              |║ 4. combinations                                                        ║
              |║    - Displays the possible combinations of cards.                      ║
              |║                                                                        ║
              |║ 5. help                                                                ║
              |║    - Displays this help page.                                          ║
              |║                                                                        ║
              |║ 6. exit                                                                ║
              |║    - Exits the game.                                                   ║
              |║                                                                        ║
              |╚════════════════════════════════════════════════════════════════════════╝
              |""".stripMargin
        helpText
    }

    /*
    * def printOverview(...)
    * returns a String representation of the overview of all (un)collected cards and their value.
    * TODO: display fitting card combination
    * */
    def printOverview(game: GameState): String = {
        val goko = "Gokō (五光) \"Five Hikari\"\t10pts.\n" + Deck.defaultDeck().cards.filter(_.cardType == CardType.HIKARI).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val shiko = "Shikō (四光) \"Four Hikari\"\t8pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val ameShiko = "Ame-Shikō (雨四光) \"Rainy Four Hikari\"\t7pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.PHOENIX).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val sanko = "Sankō (三光) \"Three Hikari\"\t6pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN && c.cardName != CardName.LIGHTNING && c.cardName != CardName.PHOENIX).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val tsukimiZake = "Tsukimi-zake (月見酒) \"Moon Viewing\"\t5pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.MOON || c.cardName == CardName.SAKE_CUP).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val hanamiZake = "Hanami-zake (花見酒) \"Cherry Blossom Viewing\"\t5pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.CURTAIN || c.cardName == CardName.SAKE_CUP).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val inoshikacho = "Inoshikachō (猪鹿蝶) \"Boar, Deer, Butterfly\"\t5pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.BOAR || c.cardName == CardName.DEER || c.cardName == CardName.BUTTERFLIES).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val tane = "Tane (タネ) \"plain\"\t1pt.\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.NIGHTINGALE || c.cardName == CardName.BRIDGE || c.cardName == CardName.CUCKOO || c.cardName == CardName.SAKE_CUP || c.cardName == CardName.BOAR).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val akatanAotan = "Akatan Aotan no Chōfuku (赤短青短の重複) \"Red Poem, Blue Poem\"\t10pts.\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.POETRY_TANZAKU || c.cardName == CardName.BLUE_TANZAKU).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val akatan = "Akatan (赤短) \"Red Poem\"\t5pts.\n" + Deck.defaultDeck().cards.filter(_.cardName == CardName.POETRY_TANZAKU).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val aotan = "Aotan (青短) \"Blue Poem\"\t5pts.\n" + Deck.defaultDeck().cards.filter(_.cardName == CardName.BLUE_TANZAKU).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val tanzaku = "Tanzaku (短冊) \"Ribbons\"\t1pt.\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.POETRY_TANZAKU || c.cardName == CardName.BLUE_TANZAKU).take(4).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val kasu = "Kasu (カス) \" \"\t1pt.\n" + Deck.defaultDeck().cards.filter(_.cardType == CardType.KASU).take(10).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n"

        val overview = clearScreen + goko + shiko + ameShiko + sanko + tsukimiZake + hanamiZake + inoshikacho + tane + akatanAotan + akatan + aotan + tanzaku + kasu
        overview
    }
    
    /*
    * def colorizeOverviewCard(...)
    * returns the colorized unicode representation of given card depending on who owns it.
    * */
    def colorizeOverviewCard(game: GameState, card: Card): List[String] = card match {
        case c if game.players.head.side.cards.contains(card) => c.unicode.map(line => s"\u001b[32m$line\u001b[0m")
        case c if game.players(1).side.cards.contains(card) => c.unicode.map(line => s"\u001b[31m$line\u001b[0m")
        case _ => card.unicode.map(line => s"\u001b[0m$line\u001b[0m")
    }
}