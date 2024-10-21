
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

enum CardAnimal {
    case Crane, Cuckoo, Boar, Deer, Butterflies, Moon, Geese, Sake_cup, Swallow, Lightning, Phoenix
}

enum CardObject{
    case Red_poem_tanzaku, Plain, Nightingale, Bridge, Red_tanzaku, Blue_tanzaku, Rain
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
    case Bright, Animal, Ribbon, Chaff
}

enum CardName {
    case Crane, Red_poem_tanzaku, Plain, Nightingale, Curtai,
         Cuckoo, Bridge, Red_tanzaku, Butterflies, Blue_tanzaku,
         Boar, Moon, Geese, Sake_cup, Deer, Rain, Swallow, Lightning, Phoenix

    def unicode: String = this match {
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

case class CardCustom(top:String, middle:String, bottom:String) {
    def unicode: Array[String] = {
        s"""╔══════╗
           |║$top║
           |║$middle║
           |║$bottom║
           |╚══════╝
           |""".stripMargin.split("\n")
    }
}

// TODO: Implement createStack function that returns an array of 48 valid cards
/* def createStack(): Array[Card] = {

} */