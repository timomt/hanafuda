
import org.scalatest.matchers.must.Matchers.{be, noException}
import org.scalatest.wordspec.AnyWordSpec

class HanafudaSpec extends AnyWordSpec {
    "Main" should {
        "run without exceptions" in {
            noException should be thrownBy {
                Hanafuda.main()
            }
        }
    }
}