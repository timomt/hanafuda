
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import main.scala.Card

class CardPlantSpec extends AnyFunSuite with Matchers {
  test("CardPlant should return correct unicode for each plant") {
    CardPlant.Pine.unicode should be (" Pine ")
    CardPlant.Plum.unicode should be (" Plum ")
    CardPlant.Cherry.unicode should be ("Cherry")
    CardPlant.Wisteria.unicode should be ("Wist. ")
    CardPlant.Iris.unicode should be (" Iris ")
    CardPlant.Peony.unicode should be ("Peony ")
    CardPlant.Bush_Clover.unicode should be (" Bush ")
    CardPlant.Susuki_Grass.unicode should be ("Grass ")
    CardPlant.Chrysanthemum.unicode should be ("Chrys.")
    CardPlant.Willow.unicode should be ("Willow")
    CardPlant.Paulownia.unicode should be ("Paul. ")
  }
}

class CardMonthSpec extends AnyFunSuite with Matchers {
  test("CardMonth should return correct unicode for each month") {
    CardMonth.JANUARY.unicode should be (" Jan. ")
    CardMonth.FEBRUARY.unicode should be (" Feb. ")
    CardMonth.MARCH.unicode should be (" Mar. ")
    CardMonth.APRIL.unicode should be (" Apr. ")
    CardMonth.MAY.unicode should be (" May  ")
    CardMonth.JUNE.unicode should be (" Jun. ")
    CardMonth.JULY.unicode should be (" Jul. ")
    CardMonth.AUGUST.unicode should be (" Aug. ")
    CardMonth.SEPTEMBER.unicode should be (" Sep. ")
    CardMonth.OCTOBER.unicode should be (" Oct. ")
    CardMonth.NOVEMBER.unicode should be (" Nov. ")
    CardMonth.DECEMBER.unicode should be (" Dec. ")
  }
}

class CardNameSpec extends AnyFunSuite with Matchers {
  test("CardName should return correct unicode for each card name") {
    CardName.CRANE.unicode should be ("Crane ")
    CardName.PLAIN.unicode should be ("Plane ")
    CardName.NIGHTINGALE.unicode should be ("Night.")
    CardName.POETRY_TANZAKU.unicode should be ("Po_tan")
    CardName.CURTAIN.unicode should be ("Curt. ")
    CardName.CUCKOO.unicode should be ("Cuckoo")
    CardName.BRIDGE.unicode should be ("Bridge")
    CardName.BUTTERFLIES.unicode should be ("Butter")
    CardName.BLUE_TANZAKU.unicode should be ("Bl_tan")
    CardName.BOAR.unicode should be (" Boar ")
    CardName.MOON.unicode should be (" Moon ")
    CardName.GEESE.unicode should be ("Geese ")
    CardName.SAKE_CUP.unicode should be ("Sake_c")
    CardName.DEER.unicode should be (" Deer ")
    CardName.RAIN.unicode should be (" Rain ")
    CardName.SWALLOW.unicode should be ("Swall.")
    CardName.LIGHTNING.unicode should be ("Lightn")
    CardName.PHOENIX.unicode should be ("Phoen.")
  }
}

class CardSpec extends AnyFunSuite with Matchers {
  test 
  }
}

