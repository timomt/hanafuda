package model

/*
* enum CardPlant
* used to differentiate the plants displayed in hanafuda playing cards.
* */
enum CardPlant {
    case Pine, Plum, Cherry, Wisteria, Iris, Peony, Bush_Clover, Susuki_Grass, Chrysanthemum, Willow, Paulownia

    /*
    * def unicode
    * returns a String representation of this enum.
    * Meant for display in TUI.
    * */
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

/*
* enum CardMonth
* a specialized version of a regular Month enum for koi-koi.
* */
enum CardMonth {
    case JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER

    /*
    * def unicode
    * returns a String representation of this enum.
    * Meant for display in TUI.*/
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

/*
* enum CardType
* used to differentiate the 4 card types in hanafuda
* */
enum CardType {
    case HIKARI, TANE, TANZAKU, KASU
    
    /*
    * def unicode
    * returns the String representation of this enum.
    * Meant for display in TUI.
    * */
    def unicode: String = this match {
        case HIKARI => "Hikari"
        case TANE => " Tane "
        case TANZAKU => "Tanz. "
        case KASU => " Kasu "
    }
}

/*
* enum CardName
* all possible specific names of hanafuda cards.
* */
enum CardName {
    case CRANE, PLAIN, NIGHTINGALE, POETRY_TANZAKU, CURTAIN,
         CUCKOO, BRIDGE, BUTTERFLIES, BLUE_TANZAKU,
         BOAR, MOON, GEESE, SAKE_CUP, DEER, RAIN, SWALLOW, LIGHTNING, PHOENIX

    /*
    * def unicode
    * returns a String representation of this enum.
    * Meant for display in TUI.
    * */
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
    }
}

/*
* case class Card(..)
* a class to represent a hanafuda playing card.
* grouped := true if 3 cards of the same month have been dealt.
* */
case class Card(month:CardMonth, cardType: CardType, cardName:CardName, grouped:Boolean = false, index: Int = 0) {
    /*
    * def unicode
    * returns a List[String] representation of the card
    * where each entry in List is one line of the String representation.
    * Meant for display in TUI.
    * */
    def unicode: List[String] = {
        s"""╔══════╗
           |║${month.unicode}║
           |║${cardType.unicode}║
           |║${cardName.unicode}║
           |╚══════╝
           |""".stripMargin.split("\n").toList
    }
}