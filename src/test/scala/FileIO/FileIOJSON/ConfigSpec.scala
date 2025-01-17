import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import FileIO.FileIOJSON.{Config, ConfigManager}
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

class ConfigSpec extends AnyFlatSpec with Matchers {

  "ConfigManager" should "load a valid config from a JSON file" in {
    val json =
      """
        |{
        |  "concurrency": "both",
        |  "mainMenuAnimation": false,
        |  "mainMenuTheme": "mockup",
        |  "cardBack": "default",
        |  "logo": "logo",
        |  "icon": "icon"
        |}
        |""".stripMargin

    Files.write(Paths.get("config.json"), json.getBytes(StandardCharsets.UTF_8))

    val config = ConfigManager.loadConfig()
    config.concurrency shouldEqual "both"
    config.mainMenuAnimation shouldEqual false
    config.mainMenuTheme shouldEqual "mockup"
    config.cardBack shouldEqual "default"
    config.logo shouldEqual "logo"
    config.icon shouldEqual "icon"
  }

  /*
  it should "save a config to a JSON file" in {
    val config = Config("tui", true, "fall", "cherry", "logo-alt", "logo-white")
    ConfigManager.saveConfig(config)

    val configJson = new String(Files.readAllBytes(Paths.get("config.json")), StandardCharsets.UTF_8)
    val expectedJson = """{"concurrency":"tui","mainMenuAnimation":true,"mainMenuTheme":"fall","cardBack":"cherry","logo":"logo-alt","icon":"logo-white"}"""
    configJson shouldEqual expectedJson
  }
   */

 /*
  it should "throw an exception if the config file is invalid" in {
    val invalidJson = """{ "invalid": "json" }"""
    Files.write(Paths.get("config.json"), invalidJson.getBytes(StandardCharsets.UTF_8))

    an[RuntimeException] should be thrownBy {
      ConfigManager.loadConfig()
    }
  }
  */
}