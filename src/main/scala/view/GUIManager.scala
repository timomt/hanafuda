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

object GUIManager extends JFXApp3 with Observer {

    override def start(): Unit = {
        GameController.add(this)
        stage = new JFXApp3.PrimaryStage {
            title = "Hanafuda"
            width = 1280
            height = 720
            resizable = false

            scene = new Scene() {
                // Pane to add content
                val rootPane = new StackPane {
                    background = new Background(Array(
                        new BackgroundImage(
                            new Image("view/Hintergrund_test.png"), // Replace with your image path
                            BackgroundRepeat.NoRepeat, // X-axis repeat
                            BackgroundRepeat.NoRepeat, // Y-axis repeat
                            BackgroundPosition.Center, // Center the image
                            new BackgroundSize(
                                100, 100, true, true, false, true // Scale image to fit
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
                        // drop shadow effect
                        effect = new DropShadow {
                            color = Color.Black
                            radius = 10
                            spread = 0.2
                        }
                    }

                    // Pane components
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
                                onAction = (e: ActionEvent) => {
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
                                        DisplayType.GAME
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
    }


    def gameScene(gameState: GameState): Scene = {
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
                val combinationsButton = new Button("Combinations")
                combinationsButton.onAction = (e: ActionEvent) => {
                    GameController.processInput("combinations")
                }

                val summaryButton = new Button("Summary")
                summaryButton.onAction = (e: ActionEvent) => {
                    GameController.processInput("summary")
                }

                val helpButton = new Button("Help")
                helpButton.onAction = (e: ActionEvent) => {
                    GameController.processInput("help")
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
                                combinationsButton,
                                summaryButton,
                                helpButton
                            )
                        },
                        rightSpacer // Push content to the center
                    )
                }

                var highlightedTopOrBottomCard: Option[StackPane] = None
                var highlightedMiddleCard: Option[StackPane] = None

                def onMouseEntered(card: StackPane, highlightedScale: Double, highlightedEffect: DropShadow, event: MouseEvent): Unit = {
                    val scaleTransition = new ScaleTransition(Duration(200), card) {
                        toX = highlightedScale
                        toY = highlightedScale
                    }

                    val translateTransition = new TranslateTransition(Duration(200), card) {
                        toX = (event.sceneX - card.layoutX.value) * 0.05
                        toY = (event.sceneY - card.layoutY.value) * 0.05
                    }

                    card.effect = highlightedEffect
                    scaleTransition.play()
                    translateTransition.play()
                }

                def onMouseMoved(card: StackPane, event: MouseEvent): Unit = {
                    val translateTransition = new TranslateTransition(Duration(100), card) {
                        toX = (event.sceneX - card.layoutX.value) * 0.05
                        toY = (event.sceneY - card.layoutY.value) * 0.05
                    }
                    translateTransition.play()
                }

                def onMouseExited(card: StackPane, defaultScale: Double, defaultEffect: DropShadow): Unit = {
                    val scaleTransition = new ScaleTransition(Duration(200), card) {
                        toX = defaultScale
                        toY = defaultScale
                    }

                    val translateTransition = new TranslateTransition(Duration(200), card) {
                        toX = 0
                        toY = 0
                    }

                    card.effect = defaultEffect
                    scaleTransition.play()
                    translateTransition.play()
                }

                def onMouseClicked(card: StackPane, isMiddleRow: Boolean, defaultScale: Double, highlightedScale: Double, defaultEffect: DropShadow, highlightedEffect: DropShadow): Unit = {
                    if (card.scaleX.value == defaultScale) {
                        card.scaleX = highlightedScale
                        card.scaleY = highlightedScale
                        card.effect = highlightedEffect

                        if (isMiddleRow) {
                            highlightedMiddleCard.foreach { c =>
                                c.scaleX = defaultScale
                                c.scaleY = defaultScale
                                c.effect = defaultEffect
                            }
                            highlightedMiddleCard = Some(card)
                        } else {
                            highlightedTopOrBottomCard.foreach { c =>
                                c.scaleX = defaultScale
                                c.scaleY = defaultScale
                                c.effect = defaultEffect
                            }
                            highlightedTopOrBottomCard = Some(card)
                        }
                    } else {
                        // Reset to default
                        card.scaleX = defaultScale
                        card.scaleY = defaultScale
                        card.effect = defaultEffect

                        if (isMiddleRow) {
                            highlightedMiddleCard = None
                        } else {
                            highlightedTopOrBottomCard = None
                        }
                    }
                }

                def createCard(isMiddleRow: Boolean, card: Card): StackPane = {
                    val cardImage = new Image(getClass.getResourceAsStream(s"/img/card/${card.index}.png"))
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

                    //cardStackPane.onMouseEntered = (event: MouseEvent) => onMouseEntered(cardStackPane, highlightedScale, highlightedEffect, event)
                    //cardStackPane.onMouseMoved = (event: MouseEvent) => onMouseMoved(cardStackPane, event)
                    //cardStackPane.onMouseExited = (_: MouseEvent) => onMouseExited(cardStackPane, defaultScale, defaultEffect)
                    cardStackPane.onMouseClicked = (event: MouseEvent) => onMouseClicked(cardStackPane, isMiddleRow, defaultScale, highlightedScale, defaultEffect, highlightedEffect)

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
                    children = List(
                        new HBox {
                            alignment = Pos.Center
                            spacing = 10
                            children = List(
                                new Region {
                                    hgrow = Priority.Always
                                }, // Left Spacer
                                if (gameState.deck.cards.length > 0) createCard(true, gameState.deck.cards(0)) else new Region(),
                                if (gameState.deck.cards.length > 1) createCard(true, gameState.deck.cards(1)) else new Region(),
                                if (gameState.deck.cards.length > 2) createCard(true, gameState.deck.cards(2)) else new Region(),
                                if (gameState.deck.cards.length > 3) createCard(true, gameState.deck.cards(3)) else new Region(),
                                new Region {
                                    hgrow = Priority.Always
                                } // Right Spacer
                            )
                            style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
                        },
                        new HBox {
                            alignment = Pos.Center
                            spacing = 10
                            children = List(
                                new Region {
                                    hgrow = Priority.Always
                                }, // Left Spacer
                                if (gameState.deck.cards.length > 4) createCard(true, gameState.deck.cards(4)) else new Region(),
                                if (gameState.deck.cards.length > 5) createCard(true, gameState.deck.cards(5)) else new Region(),
                                if (gameState.deck.cards.length > 6) createCard(true, gameState.deck.cards(6)) else new Region(),
                                if (gameState.deck.cards.length > 7) createCard(true, gameState.deck.cards(7)) else new Region(),
                                new Region {
                                    hgrow = Priority.Always
                                } // Right Spacer
                            )
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

                val cardLayout = new VBox {
                    alignment = Pos.Center
                    spacing = 20
                    children = List(topRow, middleRow, bottomRow)
                    style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
                    maxWidth = 600
                    maxHeight = 400
                }

                val combinedLayout = new HBox {
                    val singleCard = gameState.queuedCard.map(createCard(false, _)).getOrElse(new Region())
                    alignment = Pos.Center
                    spacing = 10
                    children = List(
                        singleCard,
                        cardLayout
                    )
                }

                // Adding elements to the scene
                children = List(combinedLayout, toolbar)
                //StackPane.setAlignment(layout, Pos.Center)
            }
            root = rootPane
        }
    }

    def combinationsScene(): Scene = {
        new Scene {
            val button = new Button("Back")
            button.layoutX = 200
            button.layoutY = 150
            button.onAction = (e:ActionEvent) => {
                GameController.processInput("continue")
            }
            content = List(button)
        }
    }

    def spoilerScene(): Scene = {
        new Scene {
            val button = new Button("Continue the game")
            button.layoutX = 200
            button.layoutY = 150
            button.onAction = (e:ActionEvent) => {
                GameController.processInput("continue")
            }
            content = List(button)
        }
    }

    def summaryScene(): Scene = {
        new Scene {
            val button = new Button("Back")
            button.layoutX = 200
            button.layoutY = 150
            button.onAction = (e:ActionEvent) => {
                GameController.processInput("continue")
            }
            content = List(button)
        }
    }

    def helpScene(): Scene = {
        new Scene(600, 600) {
            val button = new Button("Back")
            button.layoutX = 200
            button.layoutY = 150
            button.onAction = (e:ActionEvent) => {
                GameController.processInput("continue")
            }
            content = List(button)
        }
    }

    override def update(gameState: GameState): Unit = {
        Platform.runLater {
            gameState.displayType match {
                case DisplayType.GAME =>
                    stage.scene = gameScene(gameState)

                case DisplayType.COMBINATIONS =>
                    stage.scene = combinationsScene()

                case DisplayType.SPOILER =>
                    stage.scene = spoilerScene()

                case DisplayType.SUMMARY =>
                    stage.scene = summaryScene()

                case DisplayType.HELP =>
                    stage.scene = helpScene()
            }
        }
    }
}