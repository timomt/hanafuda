package view

import controller.{GameController, Observer}
import model.Deck.defaultDeck
import model.DisplayType.HELP
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameState}
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Separator, TextField, ToolBar}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import view.TUIManager
import view.TUIManager.{printHelp, printOverview, printSpoiler, printSummary}
import scalafx.Includes.*
import scalafx.animation.{ScaleTransition, TranslateTransition}
import scalafx.event
import scalafx.event.ActionEvent
import scalafx.geometry.Pos.TopCenter
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.util.Duration
import view.SceneFactory

//TODO: sys.exit on window closing
object GUIManager extends JFXApp3 with Observer {


  override def start(): Unit = {
    GameController.add(this)

    stage = new JFXApp3.PrimaryStage {
      title = "Hanafuda"
      width = 1280
      height = 720
      resizable = false

      scene = SceneFactory.createWelcomeScene()
    }
  }


  def gameScene(gameState: GameState): Scene = {
    SceneFactory.createGameScene(gameState)
  }
  

  override def update(gameState: GameState): Unit = {
    Platform.runLater {
      val newScene = gameState.displayType match {
        case DisplayType.GAME => SceneFactory.createGameScene(gameState)
        case DisplayType.COMBINATIONS => ???
        case DisplayType.SPOILER => ???
        case DisplayType.SUMMARY => ???
        case DisplayType.HELP => ???
      }
      stage.scene = newScene
    }
  }
}
