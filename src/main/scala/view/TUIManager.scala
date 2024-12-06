package view
import controller.Observer
import model.{Card, CardName, CardType, Deck, DisplayType, GameState, GameStateSummary, instantWinCombinations, yakuCombinations}

/*
* MVC: View
* object TUIManager
* an object to manage the text user interface.
* */
object TUIManager extends Observer {
    val clearScreen: String = "\u001b[2J\u001b[3J\u001b[1;1H"

    /*
    * def update(...)
    * updates the TUI according to the current GameState.
    * */
    override def update(gameState: GameState): Unit = {
        gameState.displayType match
            case DisplayType.GAME => print(printBoard(gameState))
            case DisplayType.COMBINATIONS => print(printOverview(gameState))
            case DisplayType.HELP => print(printHelp())
            case DisplayType.SPOILER => print(printSpoiler())
            case DisplayType.SUMMARY => print(printSummary(gameState))
    }
    
    /*
    * def printBoard(...)
    * returns a String representation of the provided GameState.
    * */
    def printBoard(game: GameState): String = {
        val card = s"""╔══════╗
                      |║      ║
                      |║      ║
                      |║      ║
                      |╚══════╝
                      |""".stripMargin.split("\n").toList
        val cardSpacer = ((" " * 8 + "\n") * 4 + " " * 8).split("\n").toList

        val topRow = List.fill(game.players(1).hand.cards.size)(card).transpose.map(_.mkString(" ")).mkString("\n")

        val bound = if game.board.cards.size > 8
            then if game.board.cards.size % 2 == 0 then game.board.cards.size/2 else  game.board.cards.size/2+1
            else 4
        val upperMiddleRow = game.board.cards.slice(0, bound).map(_.unicode)
            .prependedAll(game.queuedCard match {
                case Some(c) => List.fill(1)(cardSpacer).prepended(c.unicode)
                case None => List.fill(2)(cardSpacer)
            })
            .appendedAll(List.fill(1)(cardSpacer))
            .appendedAll(game.matchedDeck match {
                case Some(d) => d.cards.slice(0, d.cards.size/2).map(_.unicode)
                case None => List.fill(2)(cardSpacer)
            })
            .transpose.map(_.mkString(" ")).mkString("\n")

        val lowerMiddleRow = game.board.cards.slice(bound, game.board.cards.size).map(_.unicode).prependedAll(List.fill(2)(cardSpacer))
            .appendedAll(List.fill(
                if game.board.cards.size < 8
                    then if game.board.cards.size < 5 then game.board.cards.size+1 else 5 - (game.board.cards.size-bound)
                else if game.board.cards.size % 2 == 0 then 1 else 2
            )(cardSpacer))
            .appendedAll(game.matchedDeck match {
                case Some(d) => d.cards.slice(d.cards.size/2, d.cards.size).map(_.unicode)
                case None => List.fill(2)(cardSpacer)
            })
            .transpose.map(_.mkString(" ")).mkString("\n")

        val bottomRow = game.players.head.hand.cards.map(_.unicode).transpose.map(_.mkString(" ")).mkString("\n")

        val stdoutRow = game.stdout match {
            case Some(stdout) => s"\n[]: $stdout"
            case None => ""
        }

        val stderrRow = game.stderr match {
            case Some(stderr) => s"\n[error]: $stderr"
            case None => ""
        }

        clearScreen + s"Current player: ${game.players.head.name}\tPoints: ${game.players.head.score}\n"
            + topRow + "\n\n" + upperMiddleRow + "\n" + lowerMiddleRow + "\n\n" + bottomRow + stdoutRow + stderrRow + "\n"
    }

    /*
    * def printHelp(...)
    * returns a String of the help page.
    * */
    def printHelp(): String = {
        val helpText =
            clearScreen +
            """
              |╔════════════════════════════════════════════════════════════════════════╗
              |║                                Hanafuda Help                           ║
              |╠════════════════════════════════════════════════════════════════════════╣
              |║ Welcome to Hanafuda! Here are the commands you can use:                ║
              |║                                                                        ║
              |║ 1. start <firstPlayer> <secondPlayer>                                  ║
              |║    - Starts a new game with the given player names.                    ║
              |║                                                                        ║
              |║ 2. continue [shortcut "con"]                                           ║
              |║    - return to the current game.                                       ║
              |║                                                                        ║
              |║ 2. match <x> <y>                                                       ║
              |║    - Matches cards at positions x and y on the board.                  ║
              |║                                                                        ║
              |║ 3. discard [<x>]                                                       ║
              |║    - discard card at given number.                                     ║
              |║    - argument x is only to be provided when discarding from hand       ║
              |║                                                                        ║
              |║ 4. new                                                                 ║
              |║    - takes player names and creates a new game from scratch            ║
              |║                                                                        ║
              |║ 5. combinations [shortcut "com"]                                       ║
              |║    - Displays the possible combinations of cards.                      ║
              |║                                                                        ║
              |║ 6. help                                                                ║
              |║    - Displays this help page.                                          ║
              |║                                                                        ║
              |║ 7. exit                                                                ║
              |║    - Exits the game.                                                   ║
              |║                                                                        ║
              |╚════════════════════════════════════════════════════════════════════════╝
              |""".stripMargin
        helpText
    }

    def printSpoiler(): String = {
        val helpText =
            clearScreen +
              """
                |╔════════════════════════════════════════════════════════════════════════╗
                |║                          Spoiler Protection                            ║
                |╠════════════════════════════════════════════════════════════════════════╣
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                  Type "continue" to advance the game                   ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |║                                                                        ║
                |╚════════════════════════════════════════════════════════════════════════╝
                |""".stripMargin
        helpText
    }

    def printSummary(game: GameState): String = {
        def formatPlayerName(name: String): String = {
            if (name.length > 20) name.take(17) + "..." else name.padTo(20, ' ')
        }
        val summaryHeader = clearScreen +
            s"""
               |╔═══════════════════════════════════════════════════════════════════════╗
               |║                                Summary                                ║
               |╠═══════════════════════════════════════════════════════════════════════╣
               |║                         Score of each player                          ║
               |╠═════════════════════════╦══════════════════════╦══════════════════════╣
               |║          Yaku           ║ ${formatPlayerName(game.players.head.name)} ║ ${formatPlayerName(game.players(1).name)} ║
               |╠═════════════════════════╬══════════════════════╬══════════════════════╣
               |""".stripMargin

        val combinationRows = yakuCombinations.appendedAll(instantWinCombinations).map { combo =>
            val player1Score = if (game.asInstanceOf[GameStateSummary].outOfCardsEnding) 0 else combo.evaluate(game.players.head)
            val player2Score = if (game.asInstanceOf[GameStateSummary].outOfCardsEnding) 0 else combo.evaluate(game.players(1))
            f"║ ${combo.unicodeShort}%-23s ║ $player1Score%-20d ║ $player2Score%-20d ║"
        }.mkString("\n")

        val summaryFooter =
            """
              |╚═════════════════════════╩══════════════════════╩══════════════════════╝
              |""".stripMargin

        summaryHeader + combinationRows + summaryFooter
    }

    /*
    * def printOverview(...)
    * returns a String representation of the overview of all (un)collected cards and their value.
    * *///TODO: kasu is not correct, test further
    def printOverview(game: GameState): String = {
        val goko = "Gokō (五光) \"Five Hikari\"\t10pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(_.cardType == CardType.HIKARI).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val shiko = "Shikō (四光) \"Four Hikari\"\t8pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val ameShiko = "Ame-Shikō (雨四光) \"Rainy Four Hikari\"\t7pts.\t(Rain + any other 3)\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val sanko = "Sankō (三光) \"Three Hikari\"\t6pts.\t(Any 3 excluding Rain)\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val tsukimiZake = "Tsukimi-zake (月見酒) \"Moon Viewing\"\t5pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.MOON || c.cardName == CardName.SAKE_CUP).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val hanamiZake = "Hanami-zake (花見酒) \"Cherry Blossom Viewing\"\t5pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.CURTAIN || c.cardName == CardName.SAKE_CUP).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val inoshikacho = "Inoshikachō (猪鹿蝶) \"Boar, Deer, Butterfly\"\t5pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.BOAR || c.cardName == CardName.DEER || c.cardName == CardName.BUTTERFLIES).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val tane = "Tane (タネ) \t1pt.\t(Any 5 Tane, +1pt for each extra)\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.TANE).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val akatanAotan = "Akatan Aotan no Chōfuku (赤短青短の重複) \"Red Poem, Blue Poem\"\t10pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(c => c.cardName == CardName.POETRY_TANZAKU || c.cardName == CardName.BLUE_TANZAKU).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val akatan = "Akatan (赤短) \"Red Poem\"\t5pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(_.cardName == CardName.POETRY_TANZAKU).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val aotan = "Aotan (青短) \"Blue Poem\"\t5pts.\t(exact yaku)\n" + Deck.defaultDeck().cards.filter(_.cardName == CardName.BLUE_TANZAKU).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val tanzaku = "Tanzaku (短冊) \"Ribbons\"\t1pt.\t(Any 5 Tanzaku, +1pt for each extra)\n" + Deck.defaultDeck().cards.filter(c => c.cardType == CardType.TANZAKU).map(c => colorizeOverviewCard(game, c)).transpose.map(_.mkString(" ")).mkString("\n") + "\n\n"
        val kasu = {
            val initialState = (game.players.head, game.players(1), List.empty[List[String]])
            val (_, _, colorizedCards) = Deck.defaultDeck().cards.filter(_.cardType == CardType.KASU).foldLeft(initialState) {
                case ((tempHead, tempTail, acc), card) =>
                    if (tempHead.side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName)) {
                        val colorizedCard = card.unicode.map(line => s"\u001b[32m$line\u001b[0m")
                        val updatedHead = tempHead.copy(side = Deck(tempHead.side.cards.patch(tempHead.side.cards.indexOf(card), Nil, 1)))
                        (updatedHead, tempTail, acc :+ colorizedCard)
                    } else if (tempTail.side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName)) {
                        val colorizedCard = card.unicode.map(line => s"\u001b[31m$line\u001b[0m")
                        val updatedTail = tempTail.copy(side = Deck(tempTail.side.cards.patch(tempTail.side.cards.indexOf(card), Nil, 1)))
                        (tempHead, updatedTail, acc :+ colorizedCard)
                    } else {
                        val colorizedCard = card.unicode.map(line => s"\u001b[0m$line\u001b[0m")
                        (tempHead, tempTail, acc :+ colorizedCard)
                    }
            }
            "Kasu (カス) \t1pt.\t(Any 10 Kasu, +1pt for each extra)\n" +
                colorizedCards.grouped(10)
                    .map(_.transpose.map(_.mkString(" ")).mkString("\n"))
                    .mkString("\n") + "\n"
        }
        val overview = clearScreen + goko + shiko + ameShiko + sanko + tsukimiZake + hanamiZake + inoshikacho + tane + akatanAotan + akatan + aotan + tanzaku + kasu
        overview
    }
    
    /*
    * def colorizeOverviewCard(...)
    * returns the colorized unicode representation of given card depending on who owns it.
    * */
    def colorizeOverviewCard(game: GameState, card: Card): List[String] = card match {
        case c if game.players.head.side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName) => c.unicode.map(line => s"\u001b[32m$line\u001b[0m")
        case c if game.players(1).side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName) => c.unicode.map(line => s"\u001b[31m$line\u001b[0m")
        case _ => card.unicode.map(line => s"\u001b[0m$line\u001b[0m")
    }
}