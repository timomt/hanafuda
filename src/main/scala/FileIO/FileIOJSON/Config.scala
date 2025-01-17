package FileIO.FileIOJSON
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

case class Config(
                     concurrency: String, // both, tui, gui     | default: both
                     mainMenuAnimation: Boolean, // toggle leaf animation (true, false)     | default: false
                     mainMenuTheme: String, // mockup, fall     | default: mockup
                     cardBack: String, // default, cherry, cherry-bright, cherry-dark, cherry-white, cherry-white-black, snowflake      | default: default
                     logo: String,  // logo, icon, logo-alt, logo-black, logo-white     | default: logo
                     icon: String   // icon, logo, logo-alt, logo-black, logo-white     | default: icon
                 )

object ConfigManager {
    private val configFilePath = "config.json"

    def loadConfig(): Config = {
        val configJson = new String(Files.readAllBytes(Paths.get(configFilePath)), StandardCharsets.UTF_8)
        decode[Config](configJson) match {
            case Right(config) => config
            case Left(error) => throw new RuntimeException(s"Failed to load config: $error")
        }
    }

    def saveConfig(config: Config): Unit = {
        val configJson = config.asJson.noSpaces
        Files.write(Paths.get(configFilePath), configJson.getBytes(StandardCharsets.UTF_8))
    }
}