package view

import controller.{GameController, Observer}
import model.Deck.defaultDeck
import model.{Card, DisplayType, GameState}
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.geometry.Pos.TopCenter
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextField, ToolBar}
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize, HBox, Pane, Priority, Region, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import scalafx.scene.input.MouseEvent

object SceneFactory {

  private val cardCache: Map[Int, Image] = (for (i <- 0 until 48) yield {
    val card = defaultDeck().cards(i)
    card.index -> new Image(getClass.getResourceAsStream(s"/img/card/${card.index}.png"))
  }).toMap
  

  private val welcomeScene = new Scene()
  private val gameScene = new Scene()
  private val endScene = new Scene()

  def createWelcomeScene(): Scene = {
    new Scene() {
      val rootPane = new StackPane {
        background = new Background(Array(
          new BackgroundImage(
            new Image("view/Hintergrund_test.png"),
            BackgroundRepeat.NoRepeat,
            BackgroundRepeat.NoRepeat,
            BackgroundPosition.Center,
            new BackgroundSize(
              100, 100, true, true, false, true
            )
          )
        ))

        val textField_p1 = new TextField {
          prefWidth = 200
          maxWidth = 400
          promptText = "Name Player 1"
        }

        val textField_p2 = new TextField {
          prefWidth = 200
          maxWidth = 400
          promptText = "Name Player 2"
        }

        val logo = new ImageView {
          image = new Image("view/KoiKoi_Logo.png")
          fitWidth = 200
          preserveRatio = true
          alignmentInParent = TopCenter
          effect = new DropShadow {
            color = Color.Black
            radius = 10
            spread = 0.2
          }
        }

        val vbox = new VBox {
          alignment = Pos.Center
          spacing = 100
          children = List(
            new HBox {
              alignment = Pos.Center
              spacing = 100
              children = List(textField_p1, textField_p2)
            },
            new Button("Start Game") {
              onAction = _ => {
                val player1Name = textField_p1.text.value
                val player2Name = textField_p2.text.value
                if (player1Name.isEmpty || player2Name.isEmpty) {
                  if (player1Name.isEmpty) {
                    textField_p1.promptText = "Enter Player 1 name"
                  }
                  if (player2Name.isEmpty) {
                    textField_p2.promptText = "Enter Player 2 name"
                  }
                } else {
                  GameController.processInput(s"start $player1Name $player2Name")
                }
              }
            }
          )
        }

        children = List(logo, vbox)
      }
      root = rootPane
    }
  }

  def createGameScene(gameState: GameState): Scene = {
    new Scene {
      val rootPane = new StackPane {
        background = new Background(Array(
          new BackgroundImage(
            new Image("view/hintergrund_Board.jpg"), // Replace with your image path
            BackgroundRepeat.NoRepeat, // X-axis repeat
            BackgroundRepeat.NoRepeat, // Y-axis repeat
            BackgroundPosition.Center, // Center the image
            new BackgroundSize(
              100, 100, true, true, false, true // Scale image to fit
            )
          )
        ))

        // Toolbar Buttons
        val undoButton = new Button("Undo")
        undoButton.onAction = _ => {
          GameController.processInput("undo")
        }

        val combinationsButton = new Button("Combinations")
        combinationsButton.onAction = _ => {
          GameController.processInput("combinations")
        }

        val matchButton = new Button("Match")
        matchButton.onAction = _ => {
          if (highlightedTopOrBottomCard.isDefined && highlightedMiddleCard.isDefined) {
            val bottomRowIndex = gameState.players.head.hand.cards.indexOf(highlightedTopOrBottomCard.get) + 1
            val middleCardIndex = gameState.deck.cards.indexOf(highlightedTopOrBottomCard.get) + 1
            GameController.processInput(s"match $bottomRowIndex $middleCardIndex")
          } else {
            GameController.processInput("match")
          }
        }

        val discardButton = new Button("Discard")
        discardButton.onAction = _ => {
          if (highlightedTopOrBottomCard.isDefined) {
            val topCardIndex = gameState.players.head.hand.cards.indexOf(highlightedTopOrBottomCard.get) + 1
            GameController.processInput(s"discard $topCardIndex")
          } else {
            //TODO: match witch card on the poll if no card is selected
            GameController.processInput("discard")
          }
        }

        val cardInfoTextField = new TextField {
          prefWidth = 400
          maxWidth = 600
          promptText = "Card Info"
          editable = false
        }

        val helpButton = new Button("Help")
        helpButton.onAction = _ => {
          GameController.processInput("help")
        }

        val redoButton = new Button("Redo")
        redoButton.onAction = _ => {
          GameController.processInput("redo")
        }

        // Toolbar Configuration
        val leftSpacer = new Region {
          hgrow = Priority.Always
        }
        val rightSpacer = new Region {
          hgrow = Priority.Always
        }

        val toolbar = new ToolBar {
          alignmentInParent = Pos.BottomCenter
          prefWidth = 1280
          background = Background.EMPTY
          items = List(
            leftSpacer,
            new HBox {
              alignment = Pos.Center
              spacing = 50
              children = List(
                undoButton,
                combinationsButton,
                matchButton,
                discardButton,
                helpButton,
                redoButton,
                cardInfoTextField
              )
            },
            rightSpacer // Push content to the center
          )
        }


        var highlightedTopOrBottomCard: Option[Card] = None
        var highlightedMiddleCard: Option[Card] = None

        def onMouseClicked(card: Card, cardStackPane: StackPane, isMiddleRow: Boolean, defaultScale: Double, highlightedScale: Double, defaultEffect: DropShadow, highlightedEffect: DropShadow, cardInfo: String): Unit = {
          if (cardStackPane.scaleX.value == defaultScale) {
            cardStackPane.scaleX = highlightedScale
            cardStackPane.scaleY = highlightedScale
            cardStackPane.effect = highlightedEffect

            cardInfoTextField.text = cardInfo

            if (isMiddleRow) {
              highlightedMiddleCard = Some(card)
            } else {
              highlightedTopOrBottomCard = Some(card)
            }
          } else {
            cardStackPane.scaleX = defaultScale
            cardStackPane.scaleY = defaultScale
            cardStackPane.effect = defaultEffect

            if (isMiddleRow) {
              highlightedMiddleCard = None
            } else {
              highlightedTopOrBottomCard = None
            }
          }
        }

        matchButton.onAction = _ => {
          if (highlightedTopOrBottomCard.isDefined && highlightedMiddleCard.isDefined) {
            val bottomRowIndex = gameState.players.head.hand.cards.indexOf(highlightedTopOrBottomCard.get) + 1
            val middleCardIndex = gameState.board.cards.indexOf(highlightedMiddleCard.get) + 1
            GameController.processInput(s"match $bottomRowIndex $middleCardIndex")
          } else {
            GameController.processInput("match")
          }
        }

        def createCard(isMiddleRow: Boolean, card: Card): StackPane = {
          val cardImage = cardCache(card.index)
          val cardStackPane = new StackPane {
            children = new ImageView {
              image = cardImage
              fitWidth = 50
              fitHeight = 100
              preserveRatio = true
            }
            minWidth = 50
            minHeight = 100
            effect = new DropShadow {
              color = Color.Black
              radius = 10
              spread = 0.2
            }
          }

          val defaultScale = 1.0
          val highlightedScale = 1.2

          val defaultEffect = new DropShadow {
            color = Color.Black
            radius = 10
            spread = 0.2
          }
          val highlightedEffect = new DropShadow {
            color = Color.Black
            radius = 20
            spread = 0.4
          }
          val cardInfo = s"Card: ${card.cardName}, Type: ${card.cardType}, Month: ${card.month}, " +
            s"Index: ${if (isMiddleRow) gameState.board.cards.indexOf(card) + 1 else gameState.players.head.hand.cards.indexOf(card) + 1}"
          cardStackPane.onMouseClicked = _ => onMouseClicked(card, cardStackPane, isMiddleRow, defaultScale, highlightedScale, defaultEffect, highlightedEffect, cardInfo)

          cardStackPane
        }


        val topRow = new HBox {
          alignment = Pos.Center
          spacing = 10
          children = gameState.players(1).hand.cards.map(card => createCard(false, card)) // Display player's full hand

          style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
        }

        val middleRow = new VBox {
          alignment = Pos.Center
          spacing = 0

          val halfSize = (gameState.board.cards.length + 1) / 2

          children = List(
            new HBox {
              alignment = Pos.Center
              spacing = 10
              children = (0 until halfSize).map { i =>
                if (gameState.board.cards.length > i) createCard(true, gameState.board.cards(i)) else new Region()
              }
              style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
            },
            new HBox {
              alignment = Pos.Center
              spacing = 10
              children = (halfSize until gameState.deck.cards.length).map { i =>
                if (gameState.board.cards.length > i) createCard(true, gameState.board.cards(i)) else new Region()
              }
              style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
            }
          )
          style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
        }

        val bottomRow = new HBox {
          alignment = Pos.Center
          spacing = 10
          children = gameState.players.head.hand.cards.map(card => createCard(false, card)) // Display player's hand
          style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
        }


        val matchedRow = new HBox {
          alignment = Pos.Center
          spacing = 10
          if (gameState.matchedDeck.isDefined) {
            children = gameState.matchedDeck.get.cards.map(card => createCard(false, card)) // Display matched cards
          }
          style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
        }


        val cardLayout = new VBox {
          alignment = Pos.Center
          spacing = 20
          children = List(topRow, middleRow, bottomRow)
          style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
          maxWidth = 600
          maxHeight = 400
        }

        val combinedLayout = new HBox {
          val singleCardRow = gameState.queuedCard.map(createCard(false, _)).getOrElse(new Region())
          alignment = Pos.Center
          spacing = 10
          children = List(
            singleCardRow,
            cardLayout,
            matchedRow
          )
        }
        children = List(combinedLayout, toolbar)
      }
      root = rootPane
    }
  }

  def createCombinationsWindow(gameState: GameState): StackPane = {
    // This just should be a window that shows the combinations on top of the game scene
    new StackPane {
      background = new Background(Array(
        new BackgroundImage(
          new Image("view/hintergrund_Board.jpg"),
          BackgroundRepeat.NoRepeat,
          BackgroundRepeat.NoRepeat,
          BackgroundPosition.Center,
          new BackgroundSize(
            100, 100, true, true, false, true // Scale image to fit
          )
        )
      ))
    }
  }

  /*
  def createSpoilerScene(gameState: GameState): StackPane = {
    // This should just invoce that all cards are turned around

  }
   */

  def createSummaryScene(gameState: GameState): Scene = {
    // This should be the end screen that shows the winner and the points
    new Scene {
      val rootPane = new StackPane {
        background = new Background(Array(
          new BackgroundImage(
            new Image("view/hintergrund_Board.jpg"), // Replace with your image path
            BackgroundRepeat.NoRepeat, // X-axis repeat
            BackgroundRepeat.NoRepeat, // Y-axis repeat
            BackgroundPosition.Center, // Center the image
            new BackgroundSize(
              100, 100, true, true, false, true // Scale image to fit
            )
          )
        ))
      }
      root = rootPane
    }
  }

  def createHelpWindow(gameState: GameState): StackPane = {
    // This should be a window that shows the rules of the game on top of the game scene
    new StackPane {
      background = new Background(Array(
        new BackgroundImage(
          new Image("view/hintergrund_Board.jpg"), // Replace with your image path
          BackgroundRepeat.NoRepeat, // X-axis repeat
          BackgroundRepeat.NoRepeat, // Y-axis repeat
          BackgroundPosition.Center, // Center the image
          new BackgroundSize(
            100, 100, true, true, false, true // Scale image to fit
          )
        )
      ))
    }
  }
  
}