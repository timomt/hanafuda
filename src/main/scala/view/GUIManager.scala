package view

import controller.{GameController, Observer}
import javafx.application.Application
import javafx.fxml.{FXMLLoader, FXML}
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.{Label, TextField}
import javafx.stage.{Stage, Window}
import model.{DisplayType, GameState}
import javafx.event.ActionEvent
/*
object HanafudaGUI {
    def main(args: Array[String]): Unit = {
        Application.launch(classOf[GUIManager], args: _*)
    }
}

 */

class GUIManager extends Application with Observer {

    @FXML
    private var name_Player1: TextField = _
    @FXML
    private var name_Player2: TextField = _

    override def update(gameState: GameState): Unit = {
        gameState.displayType match {
            case DisplayType.GAME => switchScene("Scene_game.fxml")
            case DisplayType.COMBINATIONS => switchScene("Scene_combinations.fxml")
            case DisplayType.HELP => switchScene("Scene_help.fxml")
            case DisplayType.SPOILER => switchScene("Scene_spoiler.fxml")
        }
    }

    def setPrimaryStage(primaryStage: Stage): Unit = {
        primaryStage.setTitle("Hanafuda")
        primaryStage.show()
    }

    def switchScene(scene: String, player1: String = "", player2: String = ""): Unit = {
        val loader = new FXMLLoader(getClass.getResource(scene))
        val root: Parent = loader.load()
        val newScene = new Scene(root)
        val primaryStage = Window.getWindows.get(0).asInstanceOf[Stage]
        primaryStage.setScene(newScene)

        if (scene == "Scene_game.fxml") {
            val name1Label = loader.getNamespace.get("name1").asInstanceOf[Label]
            val name2Label = loader.getNamespace.get("name2").asInstanceOf[Label]
            name1Label.setText(player1)
            name2Label.setText(player2)
        }
    }

    override def start(primaryStage: Stage): Unit = {
        setPrimaryStage(primaryStage)
        switchScene("Welcome_Screen.fxml")
    }

    def startGame(actionEvent: ActionEvent): Unit = {
        val player1Name = name_Player1.getText
        val player2Name = name_Player2.getText
        GameController.newGame(player1Name, player2Name)
        switchScene("Scene_game.fxml", player1Name, player2Name)
    }
}