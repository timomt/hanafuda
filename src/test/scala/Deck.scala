import org.scalatest.flatspec.AnyFlatSpec

class DeckSpec extends AnyFlatSpec {
    "A default Deck" should "have 48 cards" in {
        val deck = Deck.defaultDeck()
        assert(deck.cards.length === 48)
    }

    it should "have 4 cards of each month" in {
        val deck = Deck.defaultDeck()
        CardMonth.values.foreach(month => assert(deck.cards.count(_.month === month) === 4))
    }

    it should "have 5 Hikari cards" in {
        val deck = Deck.defaultDeck()
        assert(deck.cards.count(_.cardType === CardType.HIKARI) === 5)
    }

    it should "have 9 Tane cards" in {
        val deck = Deck.defaultDeck()
        assert(deck.cards.count(_.cardType === CardType.TANE) === 9)
    }

    it should "have 10 Tanzaku cards, 4 of which plain, 3 poetry, 3 blue" in {
        val deck = Deck.defaultDeck()
        assert(deck.cards.count(_.cardType === CardType.TANZAKU) === 10)
        assert(deck.cards.count(card => card.cardType === CardType.TANZAKU && card.cardName === CardName.PLAIN) === 4)
        assert(deck.cards.count(card => card.cardType === CardType.TANZAKU && card.cardName === CardName.POETRY_TANZAKU) === 3)
        assert(deck.cards.count(card => card.cardType === CardType.TANZAKU && card.cardName === CardName.BLUE_TANZAKU) === 3)
    }

    it should "have 24 Kasu cards, all of them plain except lightning" in {
        val deck = Deck.defaultDeck()
        assert(deck.cards.count(_.cardType === CardType.KASU) === 24)
        assert(deck.cards.count(card => card.cardType === CardType.KASU && card.cardName === CardName.PLAIN) === 23)
        assert(deck.cards.count(card => card.cardType === CardType.KASU && card.cardName === CardName.LIGHTNING) === 1)
    }
    
    "Poll" should "return a card and a deck without that exact card" in {
        val deck = Deck.defaultDeck()
        val (card, newDeck) = Deck.poll(deck)
        assert(newDeck.cards.length === 47)
        assert(!newDeck.cards.contains(card))
        assert(card match {
            case Some(c) => (newDeck.cards :+ c).toSet === deck.cards.toSet
            case None => false
        })
    }

    it should "return None and the same deck if the deck is empty" in {
        val deck = Deck(List.empty)
        val (card, newDeck) = Deck.poll(deck)
        assert(card === None)
        assert(newDeck === deck)
    }

    "Poll Multiple" should "poll n cards from the deck" in {
        val deck = Deck.defaultDeck()
        val (cards, newDeck) = Deck.pollMultiple(deck, 5)
        assert(newDeck.cards.length === 43)
        assert(cards.length === 5)
    }
}