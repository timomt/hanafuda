object Hanafuda {
    @main
    def main(): Unit = {
        var deck = Deck.defaultDeck()
        val (cardRow, newDeck) = Deck.pollMultiple(deck, 8)
        deck = newDeck
        val lineToPrint = cardRow.map(_.unicode)
        println(lineToPrint.transpose.map(_.mkString(" ")).mkString("\n") + "\n")

    }
}