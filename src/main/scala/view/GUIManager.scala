package view

import controller.{GameController, Observer}
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameState, GameStatePlanned, GameStateRandom, GameStateUninitialized}
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{HPos, Insets, Pos, VPos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, ScrollPane, TextField, ToolBar}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.Includes.*
import scalafx.event
import scalafx.event.ActionEvent
import scalafx.geometry.Pos.TopCenter
import scalafx.scene.effect.{DropShadow, GaussianBlur}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.stage.Screen
import view.ComponentDecoraters.{BasicTextField, StyleDecorator}

/**
 * MVC: View
 * Object to manage the graphical user interface.
 */
object GUIManager extends JFXApp3 with Observer {
    /**
     * The viewport height.
     */
    private var vh: Double = 0
    
    /**
     * The viewport width.
     */
    private var vw: Double = 0

    /**
     * Cache for card images to reduce IO significantly.
     */
    private var cardCache: Map[Int, Image] = Map()
    
    /**
     * The currently selected card from the player's hand.
     */
    private var selectedHandCard: Option[Card] = None

    /**
     * The currently selected card from the board.
     */
    private var selectedBoardCard: Option[Card] = None

    /**
     * The pane containing the currently selected card from the player's hand.
     */
    private var selectedHandCardPane: Option[StackPane] = None

    /**
     * The pane containing the currently selected card from the board.
     */
    private var selectedBoardCardPane: Option[StackPane] = None
    //TODO: scale selected cards correctly without blurr

    /**
     * Initializes the GUIManager.
     */
    override def start(): Unit = {
        /* Subscribing to GameController Observable */
        GameController.add(this)

        /* Initialize vars */
        /* Is necessary here because ScalaFX properties cannot be called outside of ScalaFX Thread */
        vh = Screen.primary.visualBounds.height
        vw = Screen.primary.visualBounds.width
        cardCache = (for (i <- 0 until 49) yield {
            i -> new Image(getClass.getResourceAsStream(s"/img/card/$i.png"),
                requestedWidth = vw*0.055, requestedHeight = vw*0.055*1.5, preserveRatio = true, smooth = true)   // Hanafuda card size ratio is 2/3
        }).toMap
        /* ---------------- */

        stage = new JFXApp3.PrimaryStage {
            title = "Hanafuda"
            width = vw
            height = vh
            resizable = false
            icons += new Image(getClass.getResourceAsStream("/img/logo/koikoi.png"))
            onCloseRequest = _ => {
                GameController.processInput("exit")
            }
        }
        stage.scene = sceneUninitialized()
    }

    /* ------------------------------------------------------- */
    /* --------------- Scenes for update() -------------------- */

    /**
     * Creates the scene for the uninitialized state.
     *
     * @return the scene for the uninitialized state
     */
    def sceneUninitialized(): Scene = new Scene {
        val rootPane: StackPane = new StackPane {
            background = new Background(Array(
                new BackgroundImage(
                    new Image("/img/background/main_menu.png",
                        requestedWidth = vw, requestedHeight = vh,
                        preserveRatio = true, smooth = true, backgroundLoading = false),
                    BackgroundRepeat.NoRepeat,
                    BackgroundRepeat.NoRepeat,
                    BackgroundPosition.Center,
                    new BackgroundSize(
                        vw, vh, true, true, false, true
                    )
                )
            ))
            val textField_p1: TextField = createStyledTextField("First player name")
            textField_p1.prefWidth = vw * 0.1
            textField_p1.prefHeight = vh * 0.03
            val textField_p2: TextField = createStyledTextField("Second Player name")
            textField_p2.prefWidth = vw * 0.1
            textField_p2.prefHeight = vh * 0.03
            val logo: ImageView = new ImageView {
                padding = Insets(vh * 0.2, 0, 0, 0)
                image = new Image("/img/logo/koikoi.png")
                fitWidth = vw * 0.15
                preserveRatio = true
                alignmentInParent = TopCenter
                effect = new DropShadow {
                    color = Color.Black
                    radius = 10
                    spread = 0.2
                }
            }

            // Pane components
            val vbox = new VBox {
                alignment = Pos.Center
                spacing = vh * 0.075
                val startButton: Button = createGameTaskbarButton("Start", (e: ActionEvent) => {
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
                })
                startButton.prefWidth = vw * 0.1
                children = List(
                    new HBox {
                        alignment = Pos.Center
                        spacing = vw * 0.01
                        children = List(textField_p1, textField_p2)
                    }, startButton
                )
            }
            children = List(logo, vbox)
        }
        root = rootPane
    }

    /**
     * Creates the scene for the game state.
     *
     * @param gameState the current game state
     * @return the scene for the game state
     */
    def gameScene(gameState: GameState): Scene = {
        new Scene {
            val rootPane: StackPane = new StackPane {
                background = new Background(Array(
                    new BackgroundImage(
                        new Image("/img/background/board.png"),
                        BackgroundRepeat.NoRepeat,
                        BackgroundRepeat.NoRepeat,
                        BackgroundPosition.Center,
                        new BackgroundSize(
                            vw, vh, true, true, false, true
                        )
                    )
                ))

                def onMouseClicked(card: Card, cardStackPane: StackPane, isBoardCard: Boolean, defaultScale: Double, highlightedScale: Double, defaultEffect: DropShadow, highlightedEffect: DropShadow): Unit = {
                    if (isBoardCard || gameState.isInstanceOf[GameStatePlanned] && gameState.players.head.hand.cards.contains(card)) {
                        val (selectedCard, selectedCardPane) = if (isBoardCard) (selectedBoardCard, selectedBoardCardPane) else (selectedHandCard, selectedHandCardPane)
                        val (setSelectedCard, setSelectedCardPane) =
                            if (isBoardCard) ((c: Option[Card]) => selectedBoardCard = c, (p: Option[StackPane]) => selectedBoardCardPane = p)
                            else ((c: Option[Card]) => selectedHandCard = c, (p: Option[StackPane]) => selectedHandCardPane = p)

                        if (selectedCard.contains(card)) {
                            cardStackPane.scaleX = defaultScale
                            cardStackPane.scaleY = defaultScale
                            cardStackPane.effect = defaultEffect
                            setSelectedCard(None)
                            setSelectedCardPane(None)
                        } else {
                            selectedCardPane.foreach { pane =>
                                pane.scaleX = defaultScale
                                pane.scaleY = defaultScale
                                pane.effect = defaultEffect
                            }
                            setSelectedCard(Some(card))
                            setSelectedCardPane(Some(cardStackPane))
                            cardStackPane.scaleX = highlightedScale
                            cardStackPane.scaleY = highlightedScale
                            cardStackPane.effect = highlightedEffect
                        }
                    }
                }

                def createCard(isBoardCard: Boolean, card: Card): StackPane = {
                    val cardImage = cardCache(card.index)
                    val cardStackPane = new StackPane {
                        children = new ImageView {
                            image = cardImage
                            preserveRatio = true
                        }
                        effect = new DropShadow {
                            color = Color.Black
                            radius = 10
                            spread = 0.2
                        }
                    }
                    val defaultScale = 1.0
                    val highlightedScale = 1.05
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
                    cardStackPane.onMouseClicked = (event: MouseEvent) => onMouseClicked(card, cardStackPane, isBoardCard, defaultScale, highlightedScale, defaultEffect, highlightedEffect)
                    cardStackPane
                }

                val topRow: HBox = new HBox {
                    alignment = Pos.Center
                    spacing = vw*0.005
                    children = gameState.players(1).hand.cards.map(card => createCard(false, Card(CardMonth.BACK, CardType.BACK, CardName.BACK, false, 0))) // Display player's full hand
                }

                val middleRow: GridPane = new GridPane {
                    alignment = Pos.Center
                    hgap = vw * 0.005
                    vgap = vh * 0.03 * 0.5

                    val halfSize: Int = (gameState.board.cards.length + 1) / 2

                    for (i <- 0 until halfSize) {
                        val card = if (gameState.board.cards.length > i) createCard(true, gameState.board.cards(i)) else new Region()
                        add(card, i, 0)
                        GridPane.setHalignment(card, HPos.CENTER)
                        GridPane.setValignment(card, VPos.CENTER)
                    }

                    for (i <- halfSize until gameState.board.cards.length) {
                        val card = if (gameState.board.cards.length > i) createCard(true, gameState.board.cards(i)) else new Region()
                        add(card, i - halfSize, 1)
                        GridPane.setHalignment(card, HPos.CENTER)
                        GridPane.setValignment(card, VPos.CENTER)
                    }
                }

                val bottomRow: HBox = new HBox {
                    alignment = Pos.Center
                    spacing = vw*0.005
                    children = gameState.players.head.hand.cards.map(card => createCard(false, card)) // Display player's hand
                }

                val matchedRow: HBox = new HBox {
                    alignment = Pos.Center
                    spacing = vw*0.005
                    if (gameState.matchedDeck.isDefined) {
                        children = gameState.matchedDeck.get.cards.map(card => createCard(false, card)) // Display matched cards
                    } 
                }

                val cardLayout: VBox = new VBox {
                    alignment = Pos.Center
                    spacing = vh*0.03
                    children = List(topRow, middleRow, bottomRow)
                }

                val combinedLayout: HBox = new HBox {
                    val singleCardRow: Region = gameState.queuedCard.map(createCard(false, _)).getOrElse(new Region())
                    singleCardRow.padding = Insets(0, vw*0.05, 0, 0)
                    alignment = Pos.Center
                    spacing = vw*0.005
                    children = List(
                        singleCardRow,
                        cardLayout,
                        matchedRow
                    )
                }
                children = List(combinedLayout, createGameTaskbar(gameState))
            }
            root = rootPane
        }
    }

    /**
     * Creates the scene for the combinations display.
     *
     * @param gameState the current game state
     * @return the scene for the combinations display
     */
    def combinationsScene(gameState: GameState): Scene = {
        new Scene {
            val rootPane = new StackPane {
                def createCard(card: Card): StackPane = {
                    val cardImage = cardCache(card.index)
                    new StackPane {
                        children = new ImageView {
                            image = cardImage
                            fitWidth = vw * 0.055 * 0.5
                            fitHeight = vw * 0.055 * 1.5 * 0.5
                            smooth = true
                            preserveRatio = true
                        }
                        effect = new DropShadow {
                            color = Color.Black
                            radius = 10
                            spread = 0.2
                        }
                        padding = Insets(0)
                    }
                }

                def createCombinationRow(title: String, cards: Seq[Card]): VBox = {
                    new VBox {
                        alignment = Pos.Center
                        spacing = 0.025 * vh
                        children = List(
                            new Label(title) {
                                style = "-fx-font-size: 16px; -fx-text-fill: black;"
                            },
                            new FlowPane {
                                alignment = Pos.Center
                                hgap = 0.005 * vh
                                vgap = 0.005 * vh
                                children = cards.map(createCard)
                            }
                        )
                    }
                }

                val combinations = List(
                    createCombinationRow("Gokō (五光) \"Five Hikari\" 10pts.", Deck.defaultDeck().cards.filter(_.cardType == CardType.HIKARI)),
                    createCombinationRow("Shikō (四光) \"Four Hikari\" 8pts.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN)),
                    createCombinationRow("Ame-Shikō (雨四光) \"Rainy Four Hikari\" 7pts.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI)),
                    createCombinationRow("Sankō (三光) \"Three Hikari\" 6pts.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN)),
                    createCombinationRow("Tsukimi-zake (月見酒) \"Moon Viewing\" 5pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.MOON || c.cardName == CardName.SAKE_CUP)),
                    createCombinationRow("Hanami-zake (花見酒) \"Cherry Blossom Viewing\" 5pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.CURTAIN || c.cardName == CardName.SAKE_CUP)),
                    createCombinationRow("Inoshikachō (猪鹿蝶) \"Boar, Deer, Butterfly\" 5pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.BOAR || c.cardName == CardName.DEER || c.cardName == CardName.BUTTERFLIES)),
                    createCombinationRow("Tane (タネ) 1pt.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.TANE)),
                    createCombinationRow("Akatan Aotan no Chōfuku (赤短青短の重複) \"Red Poem, Blue Poem\" 10pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.POETRY_TANZAKU || c.cardName == CardName.BLUE_TANZAKU)),
                    createCombinationRow("Akatan (赤短) \"Red Poem\" 5pts.", Deck.defaultDeck().cards.filter(_.cardName == CardName.POETRY_TANZAKU)),
                    createCombinationRow("Aotan (青短) \"Blue Poem\" 5pts.", Deck.defaultDeck().cards.filter(_.cardName == CardName.BLUE_TANZAKU)),
                    createCombinationRow("Tanzaku (短冊) \"Ribbons\" 1pt.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.TANZAKU)),
                    createCombinationRow("Kasu (カス) 1pt.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.KASU))
                )

                val combinationsLayout = new ScrollPane {
                    content = new FlowPane {
                        alignment = Pos.TopCenter
                        hgap = vw * 0.01
                        vgap = vh * 0.02
                        children = combinations
                        padding = Insets(0.05*vh, 0, 0, 0)
                        background = new Background(Array(
                            new BackgroundImage(
                                new Image("/img/background/board.png",
                                    requestedWidth = vw, requestedHeight = vh,
                                    preserveRatio = false, smooth = true, backgroundLoading = false),
                                BackgroundRepeat.NoRepeat,
                                BackgroundRepeat.NoRepeat,
                                BackgroundPosition.Center,
                                new BackgroundSize(
                                    vw, vh, false, false, false, false
                                )
                            )
                        ))
                    }
                    fitToWidth = true
                    fitToHeight = true
                    hbarPolicy = ScrollPane.ScrollBarPolicy.Never
                    vbarPolicy = ScrollPane.ScrollBarPolicy.AsNeeded
                    vgrow = Priority.Always
                }
                val taskbarChild = createGameTaskbar(gameState)
                children = List(
                    new VBox {
                        alignment = Pos.Center
                        vgrow = Priority.Always
                        children = List(taskbarChild, combinationsLayout)
                    }
                )
            }
            root = rootPane
        }
    }

    /**
     * Creates the scene for the spoiler protection display.
     *
     * @param gameState the current game state
     * @return the scene for the spoiler protection display
     */
    def spoilerScene(gameState: GameState): Scene = {
        new Scene {
            val rootPane: StackPane = new StackPane {
                background = new Background(Array(
                    new BackgroundImage(
                        new Image("/img/background/board.png"),
                        BackgroundRepeat.NoRepeat,
                        BackgroundRepeat.NoRepeat,
                        BackgroundPosition.Center,
                        new BackgroundSize(
                            vw, vh, true, true, false, true
                        )
                    )
                ))

                def createCard(isBoardCard: Boolean, card: Card): StackPane = {
                    val cardImage = cardCache(card.index)
                    val cardStackPane = new StackPane {
                        children = new ImageView {
                            image = cardImage
                            preserveRatio = true
                        }
                        effect = new DropShadow {
                            color = Color.Black
                            radius = 10
                            spread = 0.2
                        }
                    }
                    val defaultScale = 1.0
                    val highlightedScale = 1.05
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
                    cardStackPane
                }

                val topRow: HBox = new HBox {
                    alignment = Pos.Center
                    spacing = vw * 0.005
                    children = gameState.players(1).hand.cards.map(card => createCard(false, Card(CardMonth.BACK, CardType.BACK, CardName.BACK, false, 0))) // Display player's full hand
                }

                val middleRow: GridPane = new GridPane {
                    alignment = Pos.Center
                    hgap = vw * 0.005
                    vgap = vh * 0.03 * 0.5

                    val halfSize: Int = (gameState.board.cards.length + 1) / 2

                    for (i <- 0 until halfSize) {
                        val card = if (gameState.board.cards.length > i) createCard(true, gameState.board.cards(i)) else new Region()
                        add(card, i, 0)
                        GridPane.setHalignment(card, HPos.CENTER)
                        GridPane.setValignment(card, VPos.CENTER)
                    }

                    for (i <- halfSize until gameState.board.cards.length) {
                        val card = if (gameState.board.cards.length > i) createCard(true, gameState.board.cards(i)) else new Region()
                        add(card, i - halfSize, 1)
                        GridPane.setHalignment(card, HPos.CENTER)
                        GridPane.setValignment(card, VPos.CENTER)
                    }
                }
                val bottomRow: HBox = new HBox {
                    alignment = Pos.Center
                    spacing = vw * 0.005
                    children = gameState.players(1).hand.cards.map(card => createCard(false, Card(CardMonth.BACK, CardType.BACK, CardName.BACK, false, 0))) // Display player's full hand
                }

                val cardLayout: VBox = new VBox {
                    alignment = Pos.Center
                    spacing = vh * 0.03
                    children = List(topRow, middleRow, bottomRow)
                }

                val combinedLayout: HBox = new HBox {
                    val singleCardRow: Region = gameState.queuedCard.map(createCard(false, _)).getOrElse(new Region())
                    singleCardRow.padding = Insets(0, vw * 0.05, 0, 0)
                    alignment = Pos.Center
                    spacing = vw * 0.005
                    children = List(
                        singleCardRow,
                        cardLayout,
                    )
                }
                children = List(combinedLayout, createGameTaskbar(gameState))
            }
            root = rootPane
        }

    }

    /**
     * Creates the scene for the summary display.
     *
     * @param gameState the current game state
     * @return the scene for the summary display
     */
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

    /**
     * Creates the scene for the help display.
     *
     * @param gameState the current game state
     * @return the scene for the help display
     */
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
    
    /* ------------------------------------------------------- */

    /**
     * Creates a ToolBar for the game display.
     *
     * @param gameState the current game state
     * @return the ToolBar for the game display
     */
    def createGameTaskbar(gameState: GameState): ToolBar = {
        val button1 = createGameTaskbarButton("Help", (e: ActionEvent) => {
            GameController.processInput("help")
        })
        val button2 = createGameTaskbarButton("Match", (e: ActionEvent) => {
            gameState match
                case _: GameStatePlanned if selectedHandCard.isDefined && selectedBoardCard.isDefined =>
                    val handCardIndex = gameState.players.head.hand.cards.indexOf(selectedHandCard.get) + 1
                    val boardCardIndex = gameState.board.cards.indexOf(selectedBoardCard.get) + 1
                    GameController.processInput(s"match $handCardIndex $boardCardIndex")
                case _: GameStateRandom if selectedBoardCard.isDefined =>
                    GameController.processInput(s"match ${gameState.board.cards.indexOf(selectedBoardCard.get) + 1}")
                case _ =>
            //TODO error reporting
        })
        val button3 = createGameTaskbarButton("Discard", (e: ActionEvent) => {
            gameState match
                case _: GameStatePlanned if selectedHandCard.isDefined =>
                    GameController.processInput(s"discard ${gameState.players.head.hand.cards.indexOf(selectedHandCard.get) + 1}")
                case _: GameStateRandom =>
                    GameController.processInput("discard")
                case _ =>
            //TODO error reporting
        })
        val button4 = createGameTaskbarButton("Combinations", (e: ActionEvent) => {
            GameController.processInput(if gameState.displayType == DisplayType.COMBINATIONS then "con" else "com")
        })
        val button5 = createGameTaskbarButton("Undo", (e: ActionEvent) => {
            GameController.processInput("undo")
        })
        val button6 = createGameTaskbarButton("Redo", (e: ActionEvent) => {
            GameController.processInput("redo")
        })
        val button7 = createGameTaskbarButton("Exit", (e: ActionEvent) => {
            GameController.processInput("exit")
        })

        val leftSpacer = new Region {
            hgrow = Priority.Always
        }
        val rightSpacer = new Region {
            hgrow = Priority.Always
        }
        new ToolBar {
            alignmentInParent = Pos.BottomCenter
            padding = Insets(10)
            items = List(
                leftSpacer,
                new HBox {
                    alignment = Pos.Center
                    spacing = 20
                    children = List(button1, button2, button3, button4, button5, button6, button7)
                },
                rightSpacer
            )
            style = "-fx-background-color: #231F20;"
        }
    }

    /**
     * Creates a styled Button with the given text and action to trigger on click.
     *
     * @param text the text to display on the button
     * @param action the action to perform when the button is clicked
     * @return the styled Button
     */
    def createGameTaskbarButton(text: String, action: ActionEvent => Unit): Button = {
        val button: Button = new Button(text) {
            style = "-fx-background-color: #B82025;" +
              "-fx-text-fill: white;" +
              "-fx-font-size: 14px;" +
              "-fx-padding: 10 20 10 20;" +
              "-fx-background-radius: 5;"
            onMouseEntered = _ => style = "-fx-background-color: #595FAB;" +
              "-fx-text-fill: white;" +
              "-fx-font-size: 14px;" +
              "-fx-padding: 10 20 10 20;" +
              "-fx-background-radius: 5;"
            onMouseExited = _ => style = "-fx-background-color: #B82025;" +
              "-fx-text-fill: white;" +
              "-fx-font-size: 14px;" +
              "-fx-padding: 10 20 10 20;" +
              "-fx-background-radius: 5;"
        }
        button.onAction = action
        button
    }

    /**
     * Creates a pre-styled TextField with the given prompt text.
     *
     * @param textString the prompt text to display in the text field
     * @return the styled TextField
     */
    def createStyledTextField(textString: String): TextField = {
        val basicTextField = new BasicTextField(textString)
        val styles = Map(
            "-fx-background-color" -> "#231F20",
            "-fx-text-fill" -> "White",
            "-fx-font-size" -> "14px",
            "-fx-font-family" -> "Ubuntu",
            "-fx-padding" -> "10 20 10 20",
            "-fx-background-radius" -> "5"
        )
        val styledTextField = new StyleDecorator(basicTextField, styles)
        styledTextField.render().asInstanceOf[TextField]
    }

    /**
     * Updates the GUI on observer notification.
     *
     * @param gameState the current state of the game
     */
    override def update(gameState: GameState): Unit = {
        Platform.runLater {
            if (gameState.isInstanceOf[GameStateUninitialized]) {
                stage.scene = sceneUninitialized()
            } else {
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
}