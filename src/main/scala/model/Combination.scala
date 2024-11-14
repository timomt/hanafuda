package model

trait Combination {
    val points: Int
    def evaluateBoard(game: GameState): (Int, Int)
}
/*
val allCombinations: List[Combination] = List(
    GokoCombination, ShikoCombination, AmeShikoCombination,
    SankoCombination, TsukimiZakeCombination, HanamiZakeCombination,
    InoshiKachoCombination, TaneCombination, AkatanAotanCombination,
    AkatanCombination, AotanCombination, TanzakuCombination, KasuCombination
)

case object GokoCombination extends Combination {
    override val points: Int = 10
    override def evaluateBoard(game: GameState): (Int, Int) = {
        val firstInt = List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
        ).forall(c => game.players.head.side.cards.contains(c))

        ???
    }
}*/