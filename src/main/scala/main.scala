import java.time.Month
import scala.util.Random

@main
def main(): Unit = {
    enum CardType {
        case Bright, Animal, Ribbon, Chaff
    }

    case class Card(month:Month, cardType: CardType, points:Int, unicode:String)

    val numberOfCards = 8
    val separator = " "
    val months = List(" Jan. ", " Feb. ", "March ", "April ", " May  ", " June ", " July ", " Aug. ", " Sep. ", " Oct. ", " Nov. ", " Dec. ")
    val cardType = List("Bright", "Animal", "Ribbon", "Chaff ")
    val cardPlant = List(" Pine ", " Plum ", "Cherry", "Wist. ", " Iris ", "Peony ", " Bush ", "Susuki", "Chrys.", "Maple ", "Willow", "Paul. ")

    // Idea: Create Array of Cards to be printed, split by line, then transpose array to get horizontal cards
    val cardFront = (month:String, cardType:String, cardPlant:String) => {
        s"""╔══════╗
           |║$month║
           |║$cardType║
           |║Hello║
           |╚══════╝
           |""".stripMargin.split("\n")
    }
    val cardBack = cardFront(" "*6, " "*6, " "*6)

    // Opponent cards
    val opponentRow = Array.fill(8)(cardBack)

    // Public cards
    val firstRow = Array.fill(5)((("*" * 8 + "\n") * 5).split("\n"))
    firstRow(0) = ((" " * 8 + "\t" + "\n") * 5).split("\n")
    val secondRow = Array.fill(5)((("*" * 8 + "\n") * 5).split("\n"))
    secondRow(0) = ("*"* 8 + "\t" + "\n*Card- *\t\n*stack *\t\n" + ("*" * 8 +"\t\n")*2).split("\n")

    // Player cards
    val cardLines = (1 to numberOfCards).map { _ =>
        val randomMonth = months(Random.nextInt(months.length))
        val randomType = cardType(Random.nextInt(cardType.length))
        val randomPlant = cardPlant(Random.nextInt(cardPlant.length))
        cardFront(randomMonth, randomType, randomPlant)
    }

    // Concatenate corresponding lines of each card horizontally
    val horizontalCards = cardLines.transpose.map(_.mkString(separator)).mkString("\n")

    // Print the board row by row

    print("\u001b[2J\u001b[H") // Clear the terminal and move to first line
    println("\n" + opponentRow.transpose.map(_.mkString(separator)).mkString("\n") + "\n")
    println(firstRow.transpose.map(_.mkString(separator)).mkString("\n") + "\n")
    println(secondRow.transpose.map(_.mkString(separator)).mkString("\n") + "\n")
    println(horizontalCards)
}