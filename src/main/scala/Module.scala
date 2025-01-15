import FileIO.FileIOJSON
import FileIO.FileIO
import _root_.FileIO.FileIOXML.FileIOXML
import com.google.inject.{AbstractModule, TypeLiteral}
import controller.CommandManager.CommandManager
import controller.CommandManager.CommandManagerSaveCommand.CommandManagerSaveCommand
import controller.CommandManager.CommandManagerSaveState.CommandManagerSaveState
import net.codingwell.scalaguice.ScalaModule

class HanafudaModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
        bind[CommandManager].to[CommandManagerSaveState]
        bind[FileIO].to[FileIO.FileIOJSON.FileIOJSON]
    }
}