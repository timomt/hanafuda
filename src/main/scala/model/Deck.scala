package model

import scala.util.Random

/*
* case class Deck(...)
* a deck of hanafuda playing cards.
* */
case class Deck(cards: List[Card])

/*
* object Deck
* providing static methods for case class Deck
* */
object Deck {
    /*
    * def defaultDeck()
    * returns a new Deck filled with the default hanafuda cards.
    * */
    def defaultDeck(): Deck = Deck(List(
        /* Index 0 reserved for card back */
        Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE, index = 1),
        Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, index = 2),
        Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN, index = 3),
        Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN, index = 4),

        Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE, index = 5),
        Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU, index = 6),
        Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN, index = 7),
        Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN, index = 8),

        Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN, index = 9),
        Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU, index = 10),
        Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN, index = 11),
        Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN, index = 12),

        Card(CardMonth.APRIL, CardType.TANE, CardName.CUCKOO, index = 13),
        Card(CardMonth.APRIL, CardType.TANZAKU, CardName.PLAIN, index = 14),
        Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN, index = 15),
        Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN, index = 16),

        Card(CardMonth.MAY, CardType.TANE, CardName.BRIDGE, index = 17),
        Card(CardMonth.MAY, CardType.TANZAKU, CardName.PLAIN, index = 18),
        Card(CardMonth.MAY, CardType.KASU, CardName.PLAIN, index = 19),
        Card(CardMonth.MAY, CardType.KASU, CardName.PLAIN, index = 20),

        Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES, index = 21),
        Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU, index = 22),
        Card(CardMonth.JUNE, CardType.KASU, CardName.PLAIN, index = 23),
        Card(CardMonth.JUNE, CardType.KASU, CardName.PLAIN, index = 24),

        Card(CardMonth.JULY, CardType.TANE, CardName.BOAR, index = 25),
        Card(CardMonth.JULY, CardType.TANZAKU, CardName.PLAIN, index = 26),
        Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN, index = 27),
        Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN, index = 28),

        Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON, index = 29),
        Card(CardMonth.AUGUST, CardType.TANE, CardName.GEESE, index = 30),
        Card(CardMonth.AUGUST, CardType.KASU, CardName.PLAIN, index = 31),
        Card(CardMonth.AUGUST, CardType.KASU, CardName.PLAIN, index = 32),

        Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP, index = 33),
        Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, index = 34),
        Card(CardMonth.SEPTEMBER, CardType.KASU, CardName.PLAIN, index = 35),
        Card(CardMonth.SEPTEMBER, CardType.KASU, CardName.PLAIN, index = 36),

        Card(CardMonth.OCTOBER, CardType.TANE, CardName.DEER, index = 37),
        Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU, index = 38),
        Card(CardMonth.OCTOBER, CardType.KASU, CardName.PLAIN, index = 39),
        Card(CardMonth.OCTOBER, CardType.KASU, CardName.PLAIN, index = 40),

        Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN, index = 41),
        Card(CardMonth.NOVEMBER, CardType.TANE, CardName.SWALLOW, index = 42),
        Card(CardMonth.NOVEMBER, CardType.TANZAKU, CardName.PLAIN, index = 43),
        Card(CardMonth.NOVEMBER, CardType.KASU, CardName.LIGHTNING, index = 44),

        Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX, index = 45),
        Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN, index = 46),
        Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN, index = 47),
        Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN, index = 48)
    ))
    
    /*
    * def poll(...)
    * returns a random card drawn from the provided deck 
    * and a new deck without this specific card.
    * */
    def poll(deck: Deck): (Option[Card], Deck) = {
        if (deck.cards.isEmpty) {
            (None, deck)
        } else {
            val rand = Random.nextInt(deck.cards.length)
            val card = deck.cards(rand)
            (Some(card), Deck(deck.cards.patch(rand, List.empty, 1)))
        }
    }

    /*
    * def pollMultiple(..)
    * extension of poll() for convenient use.
    * */
    def pollMultiple(deck: Deck, n: Int): (List[Card], Deck) = {
        (1 to n).foldLeft((List.empty, deck)) { case ((cards, currentDeck), _) =>
            val (polledCard, polledDeck) = poll(currentDeck)
            polledCard match {
                case Some(c) => (cards :+ c, polledDeck)
                case None => (cards, polledDeck)
            }
        }
    }
}