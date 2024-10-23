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
    case HIKARI, TANE, TANZAKU, KASU
}

enum CardName {
    case CRANE, PLAIN, NIGHTINGALE, POETRY_TANZAKU, CURTAIN,
         CUCKOO, BRIDGE, BUTTERFLIES, BLUE_TANZAKU,
         BOAR, MOON, GEESE, SAKE_CUP, DEER, RAIN, SWALLOW, LIGHTNING, PHOENIX

    def unicode: String = this match {
        case PLAIN => "Plane "
        case CRANE => "Crane "
        case NIGHTINGALE => "Night."
        case POETRY_TANZAKU => "Po_tan"
        case CURTAIN => "Curt. "
        case CUCKOO => "Cuckoo"
        case BRIDGE => "Bridge"
        case BUTTERFLIES => "Butter"
        case BLUE_TANZAKU => "Bl_tan"
        case BOAR => " Boar "
        case MOON => " Moon "
        case GEESE => "Geese "
        case SAKE_CUP => "Sake_c"
        case DEER => " Deer "
        case RAIN => " Rain "
        case SWALLOW => "Swall."
        case LIGHTNING => "Light."
        case PHOENIX => "Phoen."
        case default => this.toString
    }
}

case class Card(month:CardMonth, cardType: CardType, cardName:CardName) {
    def unicode: Array[String] = {
        s"""╔══════╗
           |║$month║
           |║$cardType║
           |║$cardName║
           |╚══════╝
           |""".stripMargin.split("\n")
    }
}