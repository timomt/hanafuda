package model

/**
 * Enum representing the different plants displayed in hanafuda playing cards.
 */
enum CardPlant {
    case Pine, Plum, Cherry, Wisteria, Iris, Peony, Bush_Clover, Susuki_Grass, Chrysanthemum, Willow, Paulownia

    /**
     * Returns a String representation of this enum.
     * Meant for display in TUI.
     *
     * @return the unicode representation of the plant
     */
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

/**
 * Enum representing the months in hanafuda playing cards.
 * BACK is reserved for the visual card back in GUI.
 */
enum CardMonth {
    case JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER, BACK

    /**
     * Returns a String representation of this enum.
     * Meant for display in TUI.
     *
     * @return the unicode representation of the month
     */
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
        case _ => ""
    }
}

/**
 * Enum representing the different card types in hanafuda.
 * BACK is reserved for the visual card back in GUI.
 */
enum CardType {
    case HIKARI, TANE, TANZAKU, KASU, BACK

    /**
     * Returns the String representation of this enum.
     * Meant for display in TUI.
     *
     * @return the unicode representation of the card type
     */
    def unicode: String = this match {
        case HIKARI => "Hikari"
        case TANE => " Tane "
        case TANZAKU => "Tanz. "
        case KASU => " Kasu "
        case _ => ""
    }
}

/**
 * Enum representing all possible specific names of hanafuda cards.
 * BACK is reserved for the visual card back in GUI.
 */
enum CardName {
    case CRANE, PLAIN, NIGHTINGALE, POETRY_TANZAKU, CURTAIN,
    CUCKOO, BRIDGE, BUTTERFLIES, BLUE_TANZAKU,
    BOAR, MOON, GEESE, SAKE_CUP, DEER, RAIN, SWALLOW, LIGHTNING, PHOENIX, BACK

    /**
     * Returns a String representation of this enum.
     * Meant for display in TUI.
     *
     * @return the unicode representation of the card name
     */
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
        case _ => ""
    }
}

/**
 * Class representing a hanafuda playing card.
 *
 * @param month the month of the card
 * @param cardType the type of the card
 * @param cardName the name of the card
 * @param grouped true if 3 cards of the same month have been dealt
 * @param index the index of the card
 */
case class Card(month: CardMonth, cardType: CardType, cardName: CardName, grouped: Boolean = false, index: Int) {
    /**
     * Returns a List[String] representation of the card
     * where each entry in the List is one line of the String representation.
     * Meant for display in TUI.
     *
     * @return the unicode representation of the card
     */
    def unicode: List[String] = {
        s"""╔══════╗
           |║${month.unicode}║
           |║${cardType.unicode}║
           |║${cardName.unicode}║
           |╚══════╝
           |""".stripMargin.split("\n").toList
    }
}