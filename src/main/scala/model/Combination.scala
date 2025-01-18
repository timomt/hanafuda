package model

/**
 * Checks if a list of cards contains a specific card based on month, type, and name.
 *
 * @param cards the list of cards to check
 * @param card the card to check for
 * @return true if the list contains the card, false otherwise
 */
def containsCard(cards: List[Card], card: Card): Boolean = {
    cards.exists(c => card.cardType == c.cardType && card.cardName == c.cardName && c.month == card.month)
}

/**
 * Trait representing any kind of card combination, either yaku (while playing) or instant win (when dealing cards).
 */
trait Combination {
    /** Minimum amount of points rewarded for this combination. */
    val points: Int
    /** Unicode representation of this combination, used for display. */
    val unicode: String
    /** Short version of unicode representation. */
    val unicodeShort: String
    /** Returns the maximum amount of points the given player would currently be rewarded. */
    def evaluate(player: Player): Int
}

/** List of all possible yaku combinations to easily check for every combination. */
val yakuCombinations: List[Combination] = List(
    GokoCombination, ShikoCombination, AmeShikoCombination,
    SankoCombination, TsukimiZakeCombination, HanamiZakeCombination,
    InoshiKachoCombination, TaneCombination, AkatanAotanCombination,
    AkatanCombination, AotanCombination, TanzakuCombination, KasuCombination
)

/** List of all instant win combinations. */
val instantWinCombinations: List[Combination] = List(
    TeshiCombination, KuttsukiCombination
)

/* ------------------------------------- */
/* --------- Yaku Combinations ---------*/

/**
 * Case object representing the Goko combination.
 * All five 20-point cards.
 */
case object GokoCombination extends Combination {
    override val points: Int = 10
    override val unicode: String = "Gokō (五光) \"Five Hikari\"\t10pts."
    override val unicodeShort: String = "Gokō"
    override def evaluate(player: Player): Int = {
        val hikariCards = List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 1),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 9),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 29),
            Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN, false, 41),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX, false, 45),
        )
        if hikariCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Shiko combination.
 * All 20-point cards except Rain.
 */
case object ShikoCombination extends Combination {
    override val points: Int = 8
    override val unicode: String = "Shikō (四光) \"Four Hikari\"\t8pts."
    override val unicodeShort: String = "Shikō"
    override def evaluate(player: Player): Int = {
        val shikoCards = List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 1),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 9),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 29),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX, false, 45),
        )
        if shikoCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Ame-Shiko combination.
 * Any four 20-point cards, one of which is Rain.
 */
case object AmeShikoCombination extends Combination {
    override val points: Int = 7
    override val unicode: String = "Ame-Shikō (雨四光) \"Rainy Four Hikari\"\t7pts."
    override val unicodeShort: String = "Ame-Shikō"
    override def evaluate(player: Player): Int = {
        if player.side.cards.count(_.cardType == CardType.HIKARI) >= 4
            && containsCard(player.side.cards, Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN, false, 41))
        then points else 0
    }
}

/**
 * Case object representing the Sanko combination.
 * Any three 20-point cards excluding Rain.
 */
case object SankoCombination extends Combination {
    override val points: Int = 6
    override val unicode: String = "Sankō (三光) \"Three Hikari\"\t6pts."
    override val unicodeShort: String = "Sankō"
    override def evaluate(player: Player): Int = {
        if player.side.cards.count(_.cardType == CardType.HIKARI) >= 3
            && !containsCard(player.side.cards, Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN, false, 41))
        then points else 0
    }
}

/**
 * Case object representing the Tsukimi-zake combination.
 * Moon and Sake. Cumulative with Hanami-zake.
 */
case object TsukimiZakeCombination extends Combination {
    override val points: Int = 5
    override val unicode: String = "Tsukimi-zake (月見酒) \"Moon Viewing\"\t5pts."
    override val unicodeShort: String = "Tsukimi-zake"
    override def evaluate(player: Player): Int = {
        val tzCards = List(
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 29),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 33)
        )
        if tzCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Hanami-zake combination.
 * Curtain and Sake. Cumulative with Tsukimi-zake.
 */
case object HanamiZakeCombination extends Combination {
    override val points: Int = 5
    override val unicode: String = "Hanami-zake (花見酒) \"Cherry Blossom Viewing\"\t5pts."
    override val unicodeShort: String = "Hanami-zake"
    override def evaluate(player: Player): Int = {
        val tzCards = List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 9),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 33)
        )
        if tzCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Inoshikachō combination.
 * Boar, Deer, and Butterflies.
 */
case object InoshiKachoCombination extends Combination {
    override val points: Int = 5
    override val unicode: String = "Inoshikachō (猪鹿蝶) \"Boar, Deer, Butterfly\"\t5pts."
    override val unicodeShort: String = "Inoshikachō"
    override def evaluate(player: Player): Int = {
        val tzCards = List(
            Card(CardMonth.JULY, CardType.TANE, CardName.BOAR, false, 25),
            Card(CardMonth.OCTOBER, CardType.TANE, CardName.DEER, false, 37),
            Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES, false, 21)
        )
        if tzCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Tane combination.
 * Any five 10-point cards, i.e. any combination of animal cards.
 * One additional point is awarded for each additional 10-point card.
 */
case object TaneCombination extends Combination {
    override val unicode: String = "Tane (タネ) \t1pt."
    override val unicodeShort: String = "Tane"
    override val points: Int = 1
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.TANE)
        if firstCardCount >= 5 then firstCardCount - 4 else 0
    }
}

/**
 * Case object representing the Akatan Aotan no Chōfuku combination.
 * All three Red Poetry Tanzaku cards and all three Blue Tanzaku cards
 * (i.e. the combination of Aka-tan and Ao-tan).
 */
case object AkatanAotanCombination extends Combination {
    override val unicode: String = "Akatan Aotan no Chōfuku (赤短青短の重複) \"Red Poem, Blue Poem\"\t10pts."
    override val unicodeShort: String = "Akatan Aotan no Chōfuku"
    override val points: Int = 10
    override def evaluate(player: Player): Int = {
        val aaCards = List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 2),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 6),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 10),
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 22),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 34),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 38)
        )
        if aaCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Akatan combination.
 * All three Red Poetry Tanzaku cards.
 */
case object AkatanCombination extends Combination {
    override val points: Int = 5
    override val unicode: String = "Akatan (赤短) \"Red Poem\"\t5pts."
    override val unicodeShort: String = "Akatan"
    override def evaluate(player: Player): Int = {
        val aCards = List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 2),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 6),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 10)
        )
        if aCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Aotan combination.
 * All three Blue Tanzaku cards.
 */
case object AotanCombination extends Combination {
    override val points: Int = 5
    override val unicode: String = "Aotan (青短) \"Blue Poem\"\t5pts."
    override val unicodeShort: String = "Aotan"
    override def evaluate(player: Player): Int = {
        val aCards = List(
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 22),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 34),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 38)
        )
        if aCards.forall(c => containsCard(player.side.cards, c)) then points else 0
    }
}

/**
 * Case object representing the Tanzaku combination.
 * Any five 5-point cards, all of which are Tanzaku cards.
 * One additional point is awarded for each additional 5-point card.
 */
case object TanzakuCombination extends Combination {
    override val points: Int = 1
    override val unicode: String = "Tanzaku (短冊) \"Ribbons\"\t1pt."
    override val unicodeShort: String = "Tanzaku"
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.TANZAKU)
        if firstCardCount >= 5 then firstCardCount - 4 else 0
    }
}

/**
 * Case object representing the Kasu combination.
 * Any ten 1-point cards, all of which are normal cards, also known as Chaff cards.
 * One additional point is awarded for each additional 1-point card.
 */
case object KasuCombination extends Combination {
    override val points: Int = 1
    override val unicode: String = "Kasu (カス) \t1pt."
    override val unicodeShort: String = "Kasu"
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.KASU)
        if firstCardCount >= 10 then firstCardCount - 9 else 0
    }
}

/* ------------------------------------- */
/* ----- Instant win Combinations ------ */

/**
 * Case object representing the Teshi combination.
 * Being dealt four cards of the same suit.
 */
case object TeshiCombination extends Combination {
    override val points: Int = 6
    override val unicode: String = "Teshi (手四) \"Being dealt 4 cards of same suit.\"\t6pts."
    override val unicodeShort: String = "Teshi"
    override def evaluate(player: Player): Int = {
        if player.hand.cards.size == 8 && CardMonth.values.exists(m => player.hand.cards.count(_.month == m) == 4) then points else 0
    }
}

/**
 * Case object representing the Kuttsuki combination.
 * Being dealt four pairs of cards with matching suits.
 */
case object KuttsukiCombination extends Combination {
    override val points: Int = 6
    override val unicode: String = "Kuttsuki (くっつき) \"Being dealt four pairs of cards with matching suits.\"\t6pts."
    override val unicodeShort: String = "Kuttsuki"
    override def evaluate(player: Player): Int = {
        if player.hand.cards.size == 8 && player.hand.cards.forall(c => player.hand.cards.count(_.month == c.month) == 2) then points else 0
    }   // Interesting debugging fun fact: List.forall() always returns true if List.isEmpty
}

/* ------------------------------------- */