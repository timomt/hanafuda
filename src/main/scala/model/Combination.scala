package model

def containsCard(cards: List[Card], card: Card): Boolean = {
    cards.exists(c => card.cardType == c.cardType && card.cardName == c.cardName && c.month == card.month)
}

trait Combination {
    val points: Int
    val unicode: String
    val unicodeShort: String
    def evaluate(player: Player): Int
}

val yakuCombinations: List[Combination] = List(
    GokoCombination, ShikoCombination, AmeShikoCombination,
    SankoCombination, TsukimiZakeCombination, HanamiZakeCombination,
    InoshiKachoCombination, TaneCombination, AkatanAotanCombination,
    AkatanCombination, AotanCombination, TanzakuCombination, KasuCombination
)

val instantWinCombinations: List[Combination] = List(
    TeshiCombination, KuttsukiCombination
)

/* ------------------------------------- */

/* ------ Yaku combinations ------ */
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

case object TaneCombination extends Combination {
    override val unicode: String = "Tane (タネ) \t1pt."
    override val unicodeShort: String = "Tane"
    override val points: Int = 1
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.TANE)
        if firstCardCount >= 5 then firstCardCount - 4 else 0
    }
}

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

case object TanzakuCombination extends Combination {
    override val points: Int = 1
    override val unicode: String = "Tanzaku (短冊) \"Ribbons\"\t1pt."
    override val unicodeShort: String = "Tanzaku"
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.TANZAKU)
        if firstCardCount >= 5 then firstCardCount - 4 else 0
    }
}

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

/* ------ instant win combinatins ------ */
case object TeshiCombination extends Combination {
    override val points: Int = 6
    override val unicode: String = "Teshi (手四) \"Being dealt 4 cards of same suit.\"\t6pts."
    override val unicodeShort: String = "Teshi"
    override def evaluate(player: Player): Int = {
        if CardMonth.values.exists(m => player.hand.cards.count(_.month == m) == 4) then points else 0
    }
}

case object KuttsukiCombination extends Combination {
    override val points: Int = 6
    override val unicode: String = "Kuttsuki (くっつき) \"Being dealt four pairs of cards with matching suits.\"\t6pts."
    override val unicodeShort: String = "Kuttsuki"
    override def evaluate(player: Player): Int = {
        if player.hand.cards.nonEmpty && player.hand.cards.forall(c => player.hand.cards.count(_.month == c.month) == 2) then points else 0
    }   // Interesting: List.forall() always returns true if List.isEmpty
}
/* ------------------------------------- */