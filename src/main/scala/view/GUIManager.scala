import controller.{GameController, Observer}
import model.DisplayType.HELP
import model.{Card, CardMonth, CardName, CardType, DisplayType, GameState}
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Separator, TextField, ToolBar}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import view.TUIManager
import view.TUIManager.{printHelp, printOverview, printSpoiler, printSummary}
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.geometry.Pos.TopCenter
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent

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


    def gameScene(): Scene = {
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

                def createCard(imagePath: String, isMiddleRow: Boolean): StackPane = {
                    val card = new StackPane {
                        children = new ImageView {
                            image = new Image(imagePath)
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

                    card.onMouseClicked = (event: MouseEvent) => {
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

                    card
                }


                val topRow = new HBox {
                    alignment = Pos.Center
                    spacing = 10
                    children = List.fill(8)(createCard("view/Card.png", false)) // 8 cards in the top row
                    //style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
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
                                createCard("view/Card.png", true),
                                createCard("view/Card.png", true),
                                createCard("view/Card.png", true),
                                createCard("view/Card.png", true),
                                new Region {
                                    hgrow = Priority.Always
                                } // Right Spacer
                            )
                            //style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
                        },
                        new HBox {
                            alignment = Pos.Center
                            spacing = 10
                            children = List(
                                new Region {
                                    hgrow = Priority.Always
                                }, // Left Spacer
                                createCard("view/Card.png", true),
                                createCard("view/Card.png", true),
                                createCard("view/Card.png", true),
                                createCard("view/Card.png", true),
                                new Region {
                                    hgrow = Priority.Always
                                } // Right Spacer
                            )
                            //style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
                        }
                    )
                    //style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
                }

                val bottomRow = new HBox {
                    alignment = Pos.Center
                    spacing = 10
                    children = List.fill(8)(createCard("view/Card.png", false)) // 8 cards in the bottom row
                    //style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
                }

                val cardLayout = new VBox {
                    alignment = Pos.Center
                    spacing = 20
                    children = List(topRow, middleRow, bottomRow)
                    //style = "-fx-border-color: red; -fx-border-width: 5;" // Add border
                    maxWidth = 600
                    maxHeight = 400
                }

                val combinedLayout = new HBox {
                    val singleCard = createCard("view/Card.png", false)
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
        gameState.displayType match {
            case DisplayType.GAME =>
                stage.scene = gameScene()

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