package model

trait Combination {
    val points: Int
    def evaluateBoard(game: GameState): (Int, Int)
}

case class GokoCombination(points: Int) extends Combination {
    override def evaluateBoard(game: GameState): (Int, Int) = ???
}