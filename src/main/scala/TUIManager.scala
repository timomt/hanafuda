/*
* object TUIManager
* an object to manage the text user interface
* */
object TUIManager {
    /*
    * def printBoard(...)
    * returns a String representation of the provided GameState*/
    def printBoard(game: GameState): String = {
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

        "\u001b[2J\u001b[1;1H" + topRow + "\n\n" + upperMiddleRow + "\n" + lowerMiddleRow + "\n\n" + bottomRow
    }
}