import com.google.inject.{AbstractModule, TypeLiteral}
import controller.{Command, CommandManager, CommandManagerSaveCommand, CommandManagerSaveState}
import net.codingwell.scalaguice.ScalaModule

class HanafudaModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
        bind[CommandManager].to[CommandManagerSaveState]
    }
}