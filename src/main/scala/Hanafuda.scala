object Hanafuda {
    @main
    def main(): Unit = {
        val game = GameManager.newGame()
        println(TUIManager.printBoard(game))
        println(GameManager.nextTurn(game))
        println(TUIManager.printBoard(game.copy(players = game.players.tail :+ game.players.head)))
        
        /*var deck = Deck.defaultDeck()
        val (cardRow, newDeck) = Deck.pollMultiple(deck, 8)
        deck = newDeck
        val lineToPrint = cardRow.map(_.unicode)
        println(lineToPrint.transpose.map(_.mkString(" ")).mkString("\n") + "\n")*/
    }
}