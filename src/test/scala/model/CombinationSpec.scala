import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.*

class CombinationSpec extends AnyFlatSpec with Matchers {
    "All Combinations" should "return 0 for no yaku" in {
        val player = Player("TestPlayer", side = Deck(List.empty), hand = Deck(List.empty),
            score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        yakuCombinations.exists(_.evaluate(player) > 0) should be (false)
        instantWinCombinations.exists(_.evaluate(player) > 0) should be (false)
    }

    "GokoCombination" should "return 10 points for all five 20-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 0),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0),
            Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN, false, 0),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        GokoCombination.evaluate(player) should be (10)
    }

    "ShikoCombination" should "return 8 points for all 20-point cards except Ono no Michikaze" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 0),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0),
            Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        ShikoCombination.evaluate(player) should be (8)
    }

    "AmeShikoCombination" should "return 7 points for any four 20-point cards including Ono no Michikaze" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 0),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0),
            Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        AmeShikoCombination.evaluate(player) should be (7)
    }

    "SankoCombination" should "return 6 points for any three 20-point cards excluding Ono no Michikaze" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 0),
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        SankoCombination.evaluate(player) should be (6)
    }

    "TsukimiZakeCombination" should "return 5 points for Moon and Sake" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, false, 0),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        TsukimiZakeCombination.evaluate(player) should be (5)
    }

    "HanamiZakeCombination" should "return 5 points for Curtain and Sake" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, false, 0),
            Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        HanamiZakeCombination.evaluate(player) should be (5)
    }

    "InoshiKachoCombination" should "return 5 points for Boar, Deer, and Butterflies" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JULY, CardType.TANE, CardName.BOAR, false, 0),
            Card(CardMonth.OCTOBER, CardType.TANE, CardName.DEER, false, 0),
            Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        InoshiKachoCombination.evaluate(player) should be (5)
    }

    "TaneCombination" should "return 1 point for any five 10-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANE, CardName.CRANE, false, 0),
            Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE, false, 0),
            Card(CardMonth.MARCH, CardType.TANE, CardName.CURTAIN, false, 0),
            Card(CardMonth.APRIL, CardType.TANE, CardName.CUCKOO, false, 0),
            Card(CardMonth.MAY, CardType.TANE, CardName.BRIDGE, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        TaneCombination.evaluate(player) should be (1)
    }

    "AkatanAotanCombination" should "return 10 points for all three Red Poetry Tanzaku cards and all three Blue Tanzaku cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        AkatanAotanCombination.evaluate(player) should be (10)
    }

    "AkatanCombination" should "return 5 points for all three Red Poetry Tanzaku cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        AkatanCombination.evaluate(player) should be (5)
    }

    "AotanCombination" should "return 5 points for all three Blue Tanzaku cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0),
            Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        AotanCombination.evaluate(player) should be (5)
    }

    "TanzakuCombination" should "return 1 point for any five 5-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0),
            Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        TanzakuCombination.evaluate(player) should be (1)
    }

    "KasuCombination" should "return 1 point for any ten 1-point cards" in {
        val player = Player("TestPlayer", side = Deck(List(
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.MAY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.JUNE, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.AUGUST, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.SEPTEMBER, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.OCTOBER, CardType.KASU, CardName.PLAIN, false, 0)
        )), hand = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        KasuCombination.evaluate(player) should be (1)
    }

    "TeshiCombination" should "return 6 points for being dealt four cards of the same suit" in {
        val player = Player("TestPlayer", side = Deck(List.empty), hand = Deck(List(
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.JANUARY, CardType.TANE, CardName.CRANE, false, 0),
            Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, false, 0),
            Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.MARCH, CardType.TANE, CardName.CRANE, false, 0),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.MARCH, CardType.HIKARI, CardName.CRANE, false, 0)
        )), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        TeshiCombination.evaluate(player) should be (6)
    }

    "KuttsukiCombination" should "return 6 points for being dealt four pairs of cards with matching suits" in {
        val player = Player("TestPlayer", side = Deck(List.empty), hand = Deck(List(
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN, false, 0),
            Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE, false, 0),
            Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE, false, 0),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, false, 0),
            Card(CardMonth.APRIL, CardType.HIKARI, CardName.CUCKOO, false, 0),
            Card(CardMonth.APRIL, CardType.HIKARI, CardName.CUCKOO, false, 0)
        )), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)
        KuttsukiCombination.evaluate(player) should be (6)
    }
}