
enum CardPlant {
    case Pine, Plum, Cherry, Wisteria, Iris, Peony, Bush_Clover, Susuki_Grass, Chrysanthemum, Willow, Paulownia

    def unicode: String = this match {
        case Pine => " Pine "
        case Plum => " Plum "
        case Cherry => "Cherry"
        case Wisteria => "Wist. "
        case Iris => " Iris "
        case Peony => "Peony "
        case Bush_Clover => " Bush "
        case Susuki_Grass => "Grass "
        case Chrysanthemum => "Chrys."
        case Willow => "Willow"
        case Paulownia => "Paul. "
    }
}

enum CardMonth {
    case JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER

    def unicode: String = this match {
        case JANUARY => " Jan. "
        case FEBRUARY => " Feb. "
        case MARCH => " Mar. "
        case APRIL => " Apr. "
        case MAY => " May  "
        case JUNE => " Jun. "
        case JULY => " Jul. "
        case AUGUST => " Aug. "
        case SEPTEMBER => " Sep. "
        case OCTOBER => " Oct. "
        case NOVEMBER => " Nov. "
        case DECEMBER => " Dec. "
    }
}

enum CardType {
    case Hikari, Tane, Tanzaku, Kasu
}

enum CardName {
    case Crane, Red_poem_tanzaku, Plain, Nightingale, Curtai,
         Cuckoo, Bridge, Red_tanzaku, Butterflies, Blue_tanzaku,
         Boar, Moon, Geese, Sake_cup, Deer, Rain, Swallow, Lightning, Phoenix

    def unicode: String = this match {
        // TODO: Implement unicode for each card name (max 6 characters)
        case Red_poem_tanzaku => "Red_PT"
        case default => this.toString
    }
}

case class Card(month:CardMonth, cardType: CardType, cardName:CardName, points:Int) {
    def unicode: Array[String] = {
        s"""╔══════╗
           |║$month║
           |║$cardType║
           |║$cardName║
           |╚══════╝
           |""".stripMargin.split("\n")
    }
}