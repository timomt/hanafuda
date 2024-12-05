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

//TODO: sys.exit on window closing
object GUIManager extends JFXApp3 with Observer {

    // Cache card images to avoid reloading them every time
    private val cardCache: Map[Int, Image] = (for (i <- 0 until 48) yield {
        val card = defaultDeck().cards(i)
        card.index -> new Image(getClass.getResourceAsStream(s"/img/card/${card.index}.png"))
    }).toMap

    // Cache background images to avoid reloading them every time
    private val pictureCache: Map[String, Image] = Map(
        "welcomeBackground" -> new Image(getClass.getResourceAsStream("/view/hintergrund_Board.jpg")),
        "gameBackground" -> new Image(getClass.getResourceAsStream("/view/Hintergrund_test.png")),
    )

    // function to create a button with an image and text
    def setButtonWithImageAndText(button: Button, imagePath: String, buttonText: String): Unit = {
        val buttonImage = new ImageView(new Image(imagePath)) {
            fitWidth = 100 // Set desired width
            fitHeight = 100 // Set desired height
            preserveRatio = true
        }

        val buttonLabel = new Text(buttonText) {
            style = "-fx-font-size: 14px; -fx-fill: white;" // Customize text style
        }

        val buttonGraphic = new StackPane {
            children = Seq(buttonImage, buttonLabel)
        }

        button.graphic = buttonGraphic
        button.style = "-fx-background-color: transparent;"
    }

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
        GameController.add(this)
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
                undoButton.onAction = (e: ActionEvent) => {
                    GameController.processInput("undo")
                }

                val combinationsButton = new Button("Combinations")
                combinationsButton.onAction = (e: ActionEvent) => {
                    GameController.processInput("combinations")
                }

                val matchButton = new Button("Match")
                matchButton.onAction = (e: ActionEvent) => {
                    if (highlightedTopOrBottomCard.isDefined && highlightedMiddleCard.isDefined) {
                        val bottomRowIndex = gameState.players.head.hand.cards.indexOf(highlightedTopOrBottomCard.get) + 1
                        val middleCardIndex = gameState.deck.cards.indexOf(highlightedTopOrBottomCard.get) + 1
                        GameController.processInput(s"match $bottomRowIndex $middleCardIndex")
                    } else {
                        GameController.processInput("match")
                    }
                }

                val discardButton = new Button("Discard")
                discardButton.onAction = (e: ActionEvent) => {
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
                helpButton.onAction = (e: ActionEvent) => {
                    GameController.processInput("help")
                }

                val redoButton = new Button("Redo")
                redoButton.onAction = (e: ActionEvent) => {
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

                matchButton.onAction = (e: ActionEvent) => {
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
                    cardStackPane.onMouseClicked = (event: MouseEvent) => onMouseClicked(card, cardStackPane, isMiddleRow, defaultScale, highlightedScale, defaultEffect, highlightedEffect, cardInfo)

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

    def combinationsScene(gameState: GameState): Scene = {
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

    def spoilerScene(gameState: GameState): Scene = {
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

    def summaryScene(gameState: GameState): Scene = {
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

    def helpScene(gameState: GameState): Scene = {
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
                    stage.scene = combinationsScene(gameState)

                case DisplayType.SPOILER =>
                    stage.scene = spoilerScene(gameState)

                case DisplayType.SUMMARY =>
                    stage.scene = summaryScene(gameState)

                case DisplayType.HELP =>
                    stage.scene = helpScene(gameState)
            }
        }
    }
}