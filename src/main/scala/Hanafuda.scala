import scala.util.Random
object Hanafuda {
    @main
    def main(): Unit = {
        // Create a deck of cards
        val deck = Deck()
        
        /*val card = Card(CardMonth.JANUARY, CardType.Hikari, CardName.Red_poem_tanzaku, 10)
        val cardEmpty = CardCustom(" "*6, " "*6, " "*6)
        val numberOfCards = 8
        val separator = " "
        
        // Opponent cards
        val opponentRow = Array.fill(8)(cardEmpty.unicode)
        println(opponentRow.transpose.map(_.mkString(separator)).mkString("\n") + "\n")*/

        // Public cards
        /* val firstRow = Array.fill(5)((("*" * 8 + "\n") * 5).split("\n"))
         firstRow(0) = ((" " * 8 + "\t" + "\n") * 5).split("\n")
         val secondRow = Array.fill(5)((("*" * 8 + "\n") * 5).split("\n"))
         secondRow(0) = ("*" * 8 + "\t" + "\n*Card- *\t\n*stack *\t\n" + ("*" * 8 + "\t\n") * 2).split("\n")
     
         // Player cards
         val cardLines = (1 to numberOfCards).map { _ =>
             val randomMonth = months(Random.nextInt(months.length))
             val randomType = cardType(Random.nextInt(cardType.length))
             val randomPlant = cardPlant(Random.nextInt(cardPlant.length))
             Card.cardFront(randomMonth, randomType, randomPlant)
         }
     
         // Concatenate corresponding lines of each card horizontally
         val horizontalCards = cardLines.transpose.map(_.mkString(separator)).mkString("\n")
     
         // Print the board row by row
         print("\u001b[2J\u001b[H") // Clear the terminal and move to first line
         println("\n" + opponentRow.transpose.map(_.mkString(separator)).mkString("\n") + "\n")
         println(firstRow.transpose.map(_.mkString(separator)).mkString("\n") + "\n")
         println(secondRow.transpose.map(_.mkString(separator)).mkString("\n") + "\n")
         println(horizontalCards)*/
    }
}