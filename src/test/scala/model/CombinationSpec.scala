import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.*

class CombinationSpec extends AnyFlatSpec with Matchers {
    "All Combinations" should "return 0 for no yaku" in {
        val player = Player("TestPlayer", side = Deck(List.empty), hand = Deck(List.empty),
            score = 0, calledKoiKoi = false)
        yakuCombinations.exists(_.evaluate(player) > 0) should be (false)
        instantWinCombinations.exists(_.evaluate(player) > 0) should be (false)
    }

    "GokoCombination" should "return 10 points for all five 20-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        GokoCombination.evaluate(player) should be (10)
    }

    "ShikoCombination" should "return 8 points for all 20-point cards except Ono no Michikaze" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        ShikoCombination.evaluate(player) should be (8)
    }

    "AmeShikoCombination" should "return 7 points for any four 20-point cards including Ono no Michikaze" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        AmeShikoCombination.evaluate(player) should be (7)
    }

    "SankoCombination" should "return 6 points for any three 20-point cards excluding Ono no Michikaze" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        SankoCombination.evaluate(player) should be (6)
    }

    "TsukimiZakeCombination" should "return 5 points for Moon and Sake" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        TsukimiZakeCombination.evaluate(player) should be (5)
    }

    "HanamiZakeCombination" should "return 5 points for Curtain and Sake" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        HanamiZakeCombination.evaluate(player) should be (5)
    }

    "InoshiKachoCombination" should "return 5 points for Boar, Deer, and Butterflies" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JULY, CardType.TANE, CardName.BOAR),
            Card(CardMonth.OCTOBER, CardType.TANE, CardName.DEER),
            Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        InoshiKachoCombination.evaluate(player) should be (5)
    }

    "TaneCombination" should "return 1 point for any five 10-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANE, CardName.CRANE),
            Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE),
            Card(CardMonth.MARCH, CardType.TANE, CardName.CURTAIN),
            Card(CardMonth.APRIL, CardType.TANE, CardName.CUCKOO),
            Card(CardMonth.MAY, CardType.TANE, CardName.BRIDGE)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        TaneCombination.evaluate(player) should be (1)
    }

    "AkatanAotanCombination" should "return 10 points for all three Red Poetry Tanzaku cards and all three Blue Tanzaku cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        AkatanAotanCombination.evaluate(player) should be (10)
    }

    "AkatanCombination" should "return 5 points for all three Red Poetry Tanzaku cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        AkatanCombination.evaluate(player) should be (5)
    }

    "AotanCombination" should "return 5 points for all three Blue Tanzaku cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        AotanCombination.evaluate(player) should be (5)
    }

    "TanzakuCombination" should "return 1 point for any five 5-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        TanzakuCombination.evaluate(player) should be (1)
    }

    "KasuCombination" should "return 1 point for any ten 1-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.MAY, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.JUNE, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.AUGUST, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.SEPTEMBER, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.OCTOBER, CardType.KASU, CardName.PLAIN)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false)
        KasuCombination.evaluate(player) should be (1)
    }

    "TeshiCombination" should "return 6 points for being dealt four cards of the same suit" in {
        val player = Player("TestPlayer", side = Deck(List.empty), hand = Deck(List(
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.JANUARY, CardType.TANE, CardName.CRANE),
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE)
        )), score = 0, calledKoiKoi = false)
        TeshiCombination.evaluate(player) should be (6)
    }

    "KuttsukiCombination" should "return 6 points for being dealt four pairs of cards with matching suits" in {
        val player = Player("TestPlayer", side = Deck(List.empty), hand = Deck(List(
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN),
            Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE),
            Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU),
            Card(CardMonth.APRIL, CardType.HIKARI, CardName.CUCKOO),
            Card(CardMonth.APRIL, CardType.HIKARI, CardName.CUCKOO)
        )), score = 0, calledKoiKoi = false)
        KuttsukiCombination.evaluate(player) should be (6)
    }
}