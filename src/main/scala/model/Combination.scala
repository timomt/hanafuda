package model

trait Combination {
    val points: Int
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
    override def evaluate(player: Player): Int = {
        val hikariCards = List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX),
        )
        if hikariCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object ShikoCombination extends Combination {
    override val points: Int = 8
    override def evaluate(player: Player): Int = {
        val shikoCards = List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX),
        )
        if shikoCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object AmeShikoCombination extends Combination {
    override val points: Int = 7
    override def evaluate(player: Player): Int = {
        if player.side.cards.count(_.cardType == CardType.HIKARI) >= 4
            && player.side.cards.contains(Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN))
            then points else 0
    }
}

case object SankoCombination extends Combination {
    override val points: Int = 6
    override def evaluate(player: Player): Int = {
        if player.side.cards.count(_.cardType == CardType.HIKARI) >= 3
            && !player.side.cards.contains(Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN))
        then points else 0
    }
}

case object TsukimiZakeCombination extends Combination {
    override val points: Int = 5
    override def evaluate(player: Player): Int = {
        val tzCards = List(
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
        )
        if tzCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object HanamiZakeCombination extends Combination {
    override val points: Int = 5
    override def evaluate(player: Player): Int = {
        val tzCards = List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
        )
        if tzCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object InoshiKachoCombination extends Combination {
    override val points: Int = 5
    override def evaluate(player: Player): Int = {
        val tzCards = List(
            Card(CardMonth.JULY, CardType.TANE, CardName.BOAR),
            Card(CardMonth.OCTOBER, CardType.TANE, CardName.DEER),
            Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES)
        )
        if tzCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object TaneCombination extends Combination {
    override val points: Int = 1
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.TANE)
        if firstCardCount >= 5 then firstCardCount - 4 else 0
    }
}

case object AkatanAotanCombination extends Combination {
    override val points: Int = 10
    override def evaluate(player: Player): Int = {
        val aaCards = List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU)
        )
        if aaCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object AkatanCombination extends Combination {
    override val points: Int = 5
    override def evaluate(player: Player): Int = {
        val aCards = List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU)
        )
        if aCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object AotanCombination extends Combination {
    override val points: Int = 5
    override def evaluate(player: Player): Int = {
        val aCards = List(
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU)
        )
        if aCards.forall(c => player.side.cards.contains(c)) then points else 0
    }
}

case object TanzakuCombination extends Combination {
    override val points: Int = 1
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.TANZAKU)
        if firstCardCount >= 5 then firstCardCount - 4 else 0
    }
}

case object KasuCombination extends Combination {
    override val points: Int = 1
    override def evaluate(player: Player): Int = {
        val firstCardCount = player.side.cards.count(_.cardType == CardType.KASU)
        if firstCardCount >= 10 then firstCardCount - 9 else 0
    }
}
/* ------------------------------------- */

/* ------ instant win combinatins ------ */
case object TeshiCombination extends Combination {
    override val points: Int = 6
    override def evaluate(player: Player): Int = {
        if CardMonth.values.exists(m => player.hand.cards.count(_.month == m) == 4) then points else 0
    }
}

case object KuttsukiCombination extends Combination {
    override val points: Int = 6
    override def evaluate(player: Player): Int = {
        if player.hand.cards.forall(c => player.hand.cards.count(_ == c) == 2) then points else 0
    }
}
/* ------------------------------------- */