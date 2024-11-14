package model

trait Combination {
    val points: Int
    def evaluateBoard(game: GameState): (Int, Int)
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
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val hikariCards = List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX),
        )
        val firstInt = if hikariCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if hikariCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object ShikoCombination extends Combination {
    override val points: Int = 8
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val shikoCards = List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX),
        )
        val firstInt = if shikoCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if shikoCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object AmeShikoCombination extends Combination {
    override val points: Int = 7
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstInt = if game.players.head.side.cards.count(_.cardType == CardType.HIKARI) >= 4
            && game.players.head.side.cards.contains(Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN))
            then points else 0
        val secondInt = if game.players(1).side.cards.count(_.cardType == CardType.HIKARI) >= 4
            && game.players(1).side.cards.contains(Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN))
            then points else 0
        (firstInt, secondInt)
    }
}

case object SankoCombination extends Combination {
    override val points: Int = 6
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstInt = if game.players.head.side.cards.count(_.cardType == CardType.HIKARI) >= 3
            && !game.players.head.side.cards.contains(Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN))
        then points else 0
        val secondInt = if game.players(1).side.cards.count(_.cardType == CardType.HIKARI) >= 3
            && !game.players(1).side.cards.contains(Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN))
        then points else 0
        (firstInt, secondInt)
    }
}

case object TsukimiZakeCombination extends Combination {
    override val points: Int = 5
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val tzCards = List(
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
        )
        val firstInt = if tzCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if tzCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object HanamiZakeCombination extends Combination {
    override val points: Int = 5
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val tzCards = List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
        )
        val firstInt = if tzCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if tzCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object InoshiKachoCombination extends Combination {
    override val points: Int = 5
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val tzCards = List(
            Card(CardMonth.JULY, CardType.TANE, CardName.BOAR),
            Card(CardMonth.OCTOBER, CardType.TANE, CardName.DEER),
            Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES)
        )
        val firstInt = if tzCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if tzCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object TaneCombination extends Combination {
    override val points: Int = 1
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstCardCount = game.players.head.side.cards.count(_.cardType == CardType.TANE)
        val secondCardCount = game.players(1).side.cards.count(_.cardType == CardType.TANE)
        val firstInt = if firstCardCount >= 5 then firstCardCount - 4 else 0
        val secondInt = if secondCardCount >= 5 then secondCardCount - 4 else 0
        (firstInt, secondInt)
    }
}

case object AkatanAotanCombination extends Combination {
    override val points: Int = 10
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val aaCards = List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU)
        )
        val firstInt = if aaCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if aaCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object AkatanCombination extends Combination {
    override val points: Int = 5
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val aCards = List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU)
        )
        val firstInt = if aCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if aCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object AotanCombination extends Combination {
    override val points: Int = 5
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val aCards = List(
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU)
        )
        val firstInt = if aCards.forall(c => game.players.head.side.cards.contains(c)) then points else 0
        val secondInt = if aCards.forall(c => game.players(1).side.cards.contains(c)) then points else 0
        (firstInt, secondInt)
    }
}

case object TanzakuCombination extends Combination {
    override val points: Int = 1
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstCardCount = game.players.head.side.cards.count(_.cardType == CardType.TANZAKU)
        val secondCardCount = game.players(1).side.cards.count(_.cardType == CardType.TANZAKU)
        val firstInt = if firstCardCount >= 5 then firstCardCount - 4 else 0
        val secondInt = if secondCardCount >= 5 then secondCardCount - 4 else 0
        (firstInt, secondInt)
    }
}

case object KasuCombination extends Combination {
    override val points: Int = 1
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstCardCount = game.players.head.side.cards.count(_.cardType == CardType.KASU)
        val secondCardCount = game.players(1).side.cards.count(_.cardType == CardType.KASU)
        val firstInt = if firstCardCount >= 10 then firstCardCount - 9 else 0
        val secondInt = if secondCardCount >= 10 then secondCardCount - 9 else 0
        (firstInt, secondInt)
    }
}
/* ------------------------------------- */

/* ------ instant win combinatins ------ */
case object TeshiCombination extends Combination {
    override val points: Int = 6
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstInt = if CardMonth.values.exists(m => game.players.head.hand.cards.count(_.month == m) == 4) then points else 0
        val secondInt = if CardMonth.values.exists(m => game.players(1).hand.cards.count(_.month == m) == 4) then points else 0
        (firstInt, secondInt)
    }
}

case object KuttsukiCombination extends Combination {
    override val points: Int = 6
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstInt = if game.players.head.hand.cards.forall(c => game.players.head.hand.cards.count(_ == c) == 2) then points else 0
        val secondInt = if game.players(1).hand.cards.forall(c => game.players(1).hand.cards.count(_ == c) == 2) then points else 0
        (firstInt, secondInt)
    }
}
/* ------------------------------------- */