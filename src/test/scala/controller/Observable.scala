import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import controller.{Observable, Observer}
import model.{GameManager, GameState}

class ObservableSpec extends AnyWordSpec with Matchers {

    "An Observable" should {

        "allow observers to be added" in {
            val observableTest = new Observable
            val observer = new Observer {
                def update(gameState: GameState): Unit = {}
            }
            observableTest.add(observer)
            observableTest.subscribers should contain(observer)
        }

        "allow observers to be removed" in {
            val observableTest = new Observable
            val observer = new Observer {
                def update(gameState: GameState): Unit = {}
            }
            observableTest.add(observer)
            observableTest.remove(observer)
            observableTest.subscribers should not contain observer
        }

        "notify all observers of a new GameState" in {
            val observableTest = new Observable
            var updated = false
            val observer = new Observer {
                def update(gameState: GameState): Unit = {
                    updated = true
                }
            }
            observableTest.add(observer)
            observableTest.notifyObservers(GameManager.newGame("", ""))
            updated shouldBe true
        }
    }
}