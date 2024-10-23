package test

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import main.scala.Card

class CardSpec extends AnyFunSuite with Matchers {
  test("CardPlant should return correct unicode for each plant") {
    CardPlant.Pine.unicode should be (" Pine ")
    CardPlant.Plum.unicode should be (" Plum ")
    CardPlant.Cherry.unicode should be ("Cherry")
  }
}

