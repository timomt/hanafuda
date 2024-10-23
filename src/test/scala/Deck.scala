import org.scalatest.flatspec.AnyFlatSpec

class DeckSpec extends AnyFlatSpec {
    "A Deck" should "have 48 cards" in {
        val deck = Deck()
        assert(deck.cards.length === 48)
    }

    it should "have 4 cards of each month" in {
        val deck = Deck()
        CardMonth.values.foreach(month => assert(deck.cards.count(_.month === month) === 4))
    }

    it should "have 5 Hikari cards" in {
        val deck = Deck()
        assert(deck.cards.count(_.cardType === CardType.HIKARI) === 5)
    }

    it should "have 9 Tane cards" in {
        val deck = Deck()
        assert(deck.cards.count(_.cardType === CardType.TANE) === 9)
    }

    it should "have 10 Tanzaku cards, 4 of which plain, 3 poetry, 3 blue" in {
        val deck = Deck()
        assert(deck.cards.count(_.cardType === CardType.TANZAKU) === 10)
        assert(deck.cards.count(card => card.cardType === CardType.TANZAKU && card.cardName === CardName.PLAIN) === 4)
        assert(deck.cards.count(card => card.cardType === CardType.TANZAKU && card.cardName === CardName.POETRY_TANZAKU) === 3)
        assert(deck.cards.count(card => card.cardType === CardType.TANZAKU && card.cardName === CardName.BLUE_TANZAKU) === 3)
    }

    it should "have 24 Kasu cards, all of them plain except lightning" in {
        val deck = Deck()
        assert(deck.cards.count(_.cardType === CardType.KASU) === 24)
        assert(deck.cards.count(card => card.cardType === CardType.KASU && card.cardName === CardName.PLAIN) === 23)
        assert(deck.cards.count(card => card.cardType === CardType.KASU && card.cardName === CardName.LIGHTNING) === 1)
    }
}