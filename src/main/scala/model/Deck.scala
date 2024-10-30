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
        Card(CardMonth.JANUARY, CardType.HIKARI, CardName.CRANE),
        Card(CardMonth.JANUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
        Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.JANUARY, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.FEBRUARY, CardType.TANE, CardName.NIGHTINGALE),
        Card(CardMonth.FEBRUARY, CardType.TANZAKU, CardName.POETRY_TANZAKU),
        Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.FEBRUARY, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.MARCH, CardType.HIKARI, CardName.CURTAIN),
        Card(CardMonth.MARCH, CardType.TANZAKU, CardName.POETRY_TANZAKU),
        Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.MARCH, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.APRIL, CardType.TANE, CardName.CUCKOO),
        Card(CardMonth.APRIL, CardType.TANZAKU, CardName.PLAIN),
        Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.APRIL, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.MAY, CardType.TANE, CardName.BRIDGE),
        Card(CardMonth.MAY, CardType.TANZAKU, CardName.PLAIN),
        Card(CardMonth.MAY, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.MAY, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.JUNE, CardType.TANE, CardName.BUTTERFLIES),
        Card(CardMonth.JUNE, CardType.TANZAKU, CardName.BLUE_TANZAKU),
        Card(CardMonth.JUNE, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.JUNE, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.JULY, CardType.TANE, CardName.BOAR),
        Card(CardMonth.JULY, CardType.TANZAKU, CardName.PLAIN),
        Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.JULY, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.AUGUST, CardType.HIKARI, CardName.MOON),
        Card(CardMonth.AUGUST, CardType.TANE, CardName.GEESE),
        Card(CardMonth.AUGUST, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.AUGUST, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.SEPTEMBER, CardType.TANE, CardName.SAKE_CUP),
        Card(CardMonth.SEPTEMBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
        Card(CardMonth.SEPTEMBER, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.SEPTEMBER, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.OCTOBER, CardType.TANE, CardName.DEER),
        Card(CardMonth.OCTOBER, CardType.TANZAKU, CardName.BLUE_TANZAKU),
        Card(CardMonth.OCTOBER, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.OCTOBER, CardType.KASU, CardName.PLAIN),

        Card(CardMonth.NOVEMBER, CardType.HIKARI, CardName.RAIN),
        Card(CardMonth.NOVEMBER, CardType.TANE, CardName.SWALLOW),
        Card(CardMonth.NOVEMBER, CardType.TANZAKU, CardName.PLAIN),
        Card(CardMonth.NOVEMBER, CardType.KASU, CardName.LIGHTNING),

        Card(CardMonth.DECEMBER, CardType.HIKARI, CardName.PHOENIX),
        Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN),
        Card(CardMonth.DECEMBER, CardType.KASU, CardName.PLAIN)
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