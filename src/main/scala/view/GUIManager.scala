package view

import controller.{GameController, Observer}
import model.DisplayType.{GAME, SUMMARY}
import model.{Card, CardMonth, CardName, CardType, Deck, DisplayType, GameState, GameStatePlanned, GameStateRandom, GameStateSummary, GameStateUninitialized, Player, instantWinCombinations, yakuCombinations}
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{HPos, Insets, Pos, VPos}
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, Button, Label, ScrollPane, TableColumn, TableView, TextArea, TextField, ToolBar}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.Includes.*
import scalafx.animation.{AnimationTimer, RotateTransition, TranslateTransition}
import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer
import scalafx.event
import scalafx.event.ActionEvent
import scalafx.geometry.Pos.TopCenter
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.cell.TextFieldTableCell
import scalafx.scene.effect.{DropShadow, GaussianBlur}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.shape.{Line, StrokeLineCap}
import scalafx.stage.Screen
import scalafx.util.Duration
import view.ComponentDecoraters.{BasicTextField, StyleDecorator}

import scala.collection.immutable.List
import scala.util.Random

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

            //--------------------------------------------------------------------------------
            //animation properties for the falling leaf animation
            def createFallingLeaf(
                                     imagePath: String,
                                     sceneWidth: Double,
                                     sceneHeight: Double,
                                     leafWidth: Double,
                                     leafHeight: Double
                                 ): ImageView = {
                val leafImage = new Image(imagePath)

                val leafImageView = new ImageView(leafImage) {
                    fitWidth = leafWidth
                    fitHeight = leafHeight
                }

                val random = new Random()

                val animation: TranslateTransition = new TranslateTransition {
                    duration = Duration(50)
                    node = leafImageView
                    toX = random.nextDouble() * (sceneWidth - leafWidth)
                    toY = sceneHeight
                    delay = Duration(random.nextInt(3000))
                    leafImageView.visible = false
                    onFinished = _ => {
                        // Restart with new random positions
                        fromY = -sceneHeight
                        duration = Duration(5000)
                        toX = random.nextDouble() * (sceneWidth - leafWidth)
                        toY = sceneHeight
                        leafImageView.visible = true
                        playFromStart()
                    }
                }

                val sway = new RotateTransition {
                    duration = Duration(1000)
                    node = leafImageView
                    byAngle = 20
                    cycleCount = RotateTransition.Indefinite
                    autoReverse = true
                }

                val timer = AnimationTimer(_ => {
                    leafImageView.translateX.value += math.sin(System.currentTimeMillis() / 300.0) * 0.5
                })

                animation.play()
                leafImageView
            }

            val leaves = (1 to 19).map { i =>
                val leaf = createFallingLeaf(
                    s"/img/background/leaf$i.png",
                    vw,
                    vh,
                    vw * 0.05,
                    vh * 0.05
                )
                leaf.layoutX = Random.nextDouble() * vw
                leaf.layoutY = -vh*0.5
                leaf
            }

            //--------------------------------------------------------------------------------

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
            children = List(logo, vbox) ++ leaves
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

    // TODO: full functionality of original TUI output
    def combinationsScene(gameState: GameState): Scene = {
        new Scene {
            val rootPane = new StackPane {
                background = new Background(Array(
                    new BackgroundImage(
                        new Image("/img/background/board_cards.png",
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

                def colorizeOverviewCard(game: GameState, card: Card): List[String] = card match {
                    case c if game.players.head.side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName) => c.unicode.map(line => s"\u001b[32m$line\u001b[0m")
                    case c if game.players(1).side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName) => c.unicode.map(line => s"\u001b[31m$line\u001b[0m")
                    case _ => card.unicode.map(line => s"\u001b[0m$line\u001b[0m")
                }

                def createCard(gameState: GameState, card: Card): StackPane = {
                    val cardImage = cardCache(card.index)
                    val cardStackPane = new StackPane {
                        children = new ImageView {
                            image = cardImage
                            fitWidth = vw * 0.055 * 0.5
                            fitHeight = vw * 0.055 * 1.5 * 0.5
                            smooth = true
                            preserveRatio = true
                        }
                        effect = new DropShadow {
                            color = if (gameState.players.head.side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName)) {
                                Color.Blue
                            } else if (gameState.players(1).side.cards.exists(c => c.month == card.month && c.cardType == card.cardType && c.cardName == card.cardName)) {
                                Color.Red
                            } else {
                                Color.Black
                            }
                            radius = 10
                            spread = 0.2
                        }
                        padding = Insets(0)
                    }
                    cardStackPane
                }

                def createCombinationRow(gameState: GameState, title: String, cards: Seq[Card]): VBox = {
                    new VBox {
                        alignment = Pos.Center
                        spacing = 0.025 * vh
                        children = List(
                            new Label(title) {
                                style = "-fx-font-size: 16px; -fx-text-fill: white;"
                            },
                            new FlowPane {
                                alignment = Pos.Center
                                hgap = 0.005 * vh
                                vgap = 0.005 * vh
                                children = cards.map(card => createCard(gameState, card))
                            }
                        )
                    }
                }

                val combinations = List(
                    createCombinationRow(gameState ,"Gokō (五光) \"Five Hikari\" 10pts.", Deck.defaultDeck().cards.filter(_.cardType == CardType.HIKARI)),
                    createCombinationRow(gameState ,"Shikō (四光) \"Four Hikari\" 8pts.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN)),
                    createCombinationRow(gameState ,"Ame-Shikō (雨四光) \"Rainy Four Hikari\" 7pts.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI)),
                    createCombinationRow(gameState ,"Sankō (三光) \"Three Hikari\" 6pts.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.HIKARI && c.cardName != CardName.RAIN)),
                    createCombinationRow(gameState ,"Tsukimi-zake (月見酒) \"Moon Viewing\" 5pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.MOON || c.cardName == CardName.SAKE_CUP)),
                    createCombinationRow(gameState ,"Hanami-zake (花見酒) \"Cherry Blossom Viewing\" 5pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.CURTAIN || c.cardName == CardName.SAKE_CUP)),
                    createCombinationRow(gameState ,"Inoshikachō (猪鹿蝶) \"Boar, Deer, Butterfly\" 5pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.BOAR || c.cardName == CardName.DEER || c.cardName == CardName.BUTTERFLIES)),
                    createCombinationRow(gameState ,"Tane (タネ) 1pt.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.TANE)),
                    createCombinationRow(gameState ,"Akatan Aotan no Chōfuku (赤短青短の重複) \"Red Poem, Blue Poem\" 10pts.", Deck.defaultDeck().cards.filter(c => c.cardName == CardName.POETRY_TANZAKU || c.cardName == CardName.BLUE_TANZAKU)),
                    createCombinationRow(gameState ,"Akatan (赤短) \"Red Poem\" 5pts.", Deck.defaultDeck().cards.filter(_.cardName == CardName.POETRY_TANZAKU)),
                    createCombinationRow(gameState ,"Aotan (青短) \"Blue Poem\" 5pts.", Deck.defaultDeck().cards.filter(_.cardName == CardName.BLUE_TANZAKU)),
                    createCombinationRow(gameState ,"Tanzaku (短冊) \"Ribbons\" 1pt.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.TANZAKU)),
                    createCombinationRow(gameState ,"Kasu (カス) 1pt.", Deck.defaultDeck().cards.filter(c => c.cardType == CardType.KASU))
                )

                val combinationsLayout = new FlowPane {
                    alignment = Pos.TopCenter
                    hgap = vw * 0.01
                    vgap = vh * 0.02
                    children = combinations.map { combination =>
                        new VBox {
                            alignment = Pos.Center
                            spacing = vh * 0.01
                            children = List(combination)
                            padding = Insets(0)
                            style = "-fx-border-color: transparent; -fx-border-width: 0; -fx-background-color: transparent;"
                        }
                    }
                    padding = Insets(0.05 * vh, 0, 0, 0)
                    prefWidth = vw * 0.8
                    prefHeight = vh * 0.8
                }

                val taskbarChild = createGameTaskbarSimple(gameState)

                children = List(
                    new BorderPane {
                        center = combinationsLayout
                        bottom = taskbarChild
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
            val backgroundPane: StackPane = new StackPane {
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
                effect = new GaussianBlur(10) // Apply blur effect to the background
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
                effect = new GaussianBlur(10)
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

            val continueButton: Button = new Button("Press to Continue") {
                onAction = (e: ActionEvent) => {
                    GameController.processInput("continue")
                }
                style = "-fx-background-color: #B82025; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5;"
                StackPane.setAlignment(this, Pos.Center)
                StackPane.setMargin(this, Insets(20))
            }

            val rootPane: StackPane = new StackPane {
                children = List(backgroundPane, combinedLayout, /*createGameTaskbar(gameState),*/ continueButton)
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
    case class SummaryRow(yaku: String, player1Score: Int, player2Score: Int) {
        val yakuProperty = new StringProperty(this, "yaku", yaku)
        val player1ScoreProperty = new IntegerProperty(this, "player1Score", player1Score)
        val player2ScoreProperty = new IntegerProperty(this, "player2Score", player2Score)
    }

    def summaryScene(gameState: GameState): Scene = {
        new Scene {
            val backgroundPane: StackPane = new StackPane {
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
                effect = new GaussianBlur(10) // Apply blur effect to the background
            }

            def createSummaryTable(game: GameState): TableView[SummaryRow] = {
                def formatPlayerName(name: String): String = {
                    if (name.length > 20) name.take(17) + "..." else name.padTo(20, ' ')
                }

                val summaryData = ObservableBuffer[SummaryRow](
                    yakuCombinations.appendedAll(instantWinCombinations).map { combo =>
                        val player1Score = if (game.asInstanceOf[GameStateSummary].outOfCardsEnding) 0 else combo.evaluate(game.players.head)
                        val player2Score = if (game.asInstanceOf[GameStateSummary].outOfCardsEnding) 0 else combo.evaluate(game.players(1))
                        SummaryRow(combo.unicodeShort, player1Score, player2Score)
                    }: _*
                )

                new TableView[SummaryRow](summaryData) {
                    columns ++= List(
                        new TableColumn[SummaryRow, String]("Yaku") {
                            cellValueFactory = _.value.yakuProperty
                            cellFactory = TextFieldTableCell.forTableColumn[SummaryRow]()
                            prefWidth = 200
                        },
                        new TableColumn[SummaryRow, Number]("Player 1 Score") {
                            cellValueFactory = _.value.player1ScoreProperty.asInstanceOf[ObservableValue[Number, Number]]
                            prefWidth = 100
                        },
                        new TableColumn[SummaryRow, Number]("Player 2 Score") {
                            cellValueFactory = _.value.player2ScoreProperty.asInstanceOf[ObservableValue[Number, Number]]
                            prefWidth = 100
                        }
                    )
                    // Use CSS to set the background image
                    style = "-fx-background-image: url('/img/background/board_cards.png'); " +
                      "-fx-background-repeat: no-repeat; " +
                      "-fx-background-position: center; " +
                      "-fx-background-size: cover;"
                    prefWidth = 500
                    maxWidth = 500
                }
            }

            def createGameTaskbarSummary(gameState: GameState): ToolBar = {
                val button1 = createGameTaskbarButton("New", (e: ActionEvent) => {
                    GameController.processInput("new")
                })
                val button2 = createGameTaskbarButton("Exit", (e: ActionEvent) => {
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
                            children = List(button1, button2)
                        },
                        rightSpacer
                    )
                    style = "-fx-background-color: #231F20;"
                }
            }

            val rootPane: StackPane = new StackPane {
                children = List(
                    backgroundPane,
                    new VBox {
                        children = List(
                            new Label("Summary") {
                                style = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;"
                            },
                            new Label("Score of each player") {
                                style = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;"
                            },
                            createSummaryTable(gameState)
                        )
                        spacing = 10
                        alignment = Pos.Center
                    },
                    createGameTaskbarSummary(gameState)
                )
                // Zentrierung und Transparenz-Effekt
                style = "-fx-background-color: transparent;"
            }
            root = rootPane
        }
    }

    /**
     * Creates the scene for the help display.
     *
     * @param gameState the current game state
     * @return the scene for the help display
     */
    def helpScene(gameState: GameState): Scene = {
        new Scene {
            val rootPane: StackPane = new StackPane {
                val backgroundPane: StackPane = new StackPane {
                    background = new Background(Array(
                        new BackgroundImage(
                            new Image("/img/background/bar.png"),
                            BackgroundRepeat.NoRepeat,
                            BackgroundRepeat.NoRepeat,
                            BackgroundPosition.Center,
                            new BackgroundSize(
                                vw, vh, true, true, false, true
                            )
                        )
                    ))

                    def createTextField(initialText: String): TextField = {
                        val textProperty = StringProperty(initialText)

                        new TextField {
                            text <==> textProperty
                            editable = false
                            style = "-fx-font-family: 'Chalkduster', 'Comic Sans MS', cursive; " +
                              "-fx-text-fill: #ffffff; " +
                              "-fx-font-size: 20px; " +
                              "-fx-effect: dropshadow(gaussian, #000000, 2, 0.5, 1, 1); " +
                              "-fx-background-color: transparent; " +
                              "-fx-padding: 10px; " +
                              "-fx-font-style: italic; " +
                              "-fx-font-weight: bold;" +
                              "-fx-border-color: transparent; " +
                              "-fx-border-width: 0;"
                        }
                    }

                    val helpText = List(
                        createTextField("Welcome to Hanafuda! Here are the buttons you can use:"),
                        createTextField("1. start \n   - Starts a new game with the given player names."),
                        createTextField("2. continue \n   - return to the current game."),
                        createTextField("3. match \n   - Matches cards at positions hand and deck on the board."),
                        createTextField("4. discard \n   - discard card at given number.\n   - argument x is only to be provided when discarding from hand"),
                        createTextField("5. new\n   - takes player names and creates a new game from scratch"),
                        createTextField("6. combinations \n   - Displays the possible combinations of cards."),
                        createTextField("7. help\n   - Displays this help page."),
                        createTextField("8. exit\n   - Exits the game.")
                    )

                    val textAreaPane: StackPane = new StackPane {
                        val drawingBoundsX: Double = 360  // X position of the drawing area
                        val drawingBoundsY: Double = 150  // Y position of the drawing area
                        val drawingWidth: Double = 1000   // Width of the drawing area
                        val drawingHeight: Double = 600   // Height of the drawing area

                        val drawingPane: Pane = new Pane {
                            style = "-fx-background-color: transparent;"
                        }

                        val textLayer: VBox = new VBox {
                            alignment = Pos.Center
                            spacing = 10
                            children = helpText
                            maxWidth = drawingWidth
                            maxHeight = drawingHeight
                            style = "-fx-background-color: transparent;"
                        }

                        var dragStartX: Double = 0
                        var dragStartY: Double = 0

                        drawingPane.onMousePressed = (event: MouseEvent) => {
                            if (event.x >= drawingBoundsX && event.x <= drawingBoundsX + drawingWidth &&
                              event.y >= drawingBoundsY && event.y <= drawingBoundsY + drawingHeight) {
                                dragStartX = event.x
                                dragStartY = event.y
                            }
                        }

                        drawingPane.onMouseDragged = (event: MouseEvent) => {
                            val constrainedEndX = math.max(drawingBoundsX, math.min(event.x, drawingBoundsX + drawingWidth))
                            val constrainedEndY = math.max(drawingBoundsY, math.min(event.y, drawingBoundsY + drawingHeight))

                            val line = new Line {
                                startX = dragStartX
                                startY = dragStartY
                                endX = constrainedEndX
                                endY = constrainedEndY
                                stroke = Color.White
                                strokeWidth = 3
                                strokeLineCap = StrokeLineCap.Round
                            }

                            if (dragStartX >= drawingBoundsX && dragStartX <= drawingBoundsX + drawingWidth &&
                              dragStartY >= drawingBoundsY && dragStartY <= drawingBoundsY + drawingHeight) {
                                drawingPane.children.add(line)
                            }

                            dragStartX = constrainedEndX
                            dragStartY = constrainedEndY
                        }
                        children = List(textLayer, drawingPane)
                    }

                    val taskbarChild = createGameTaskbarSimple(gameState)

                    children = List(textAreaPane, taskbarChild)
                }
                children = List(backgroundPane)
            }
            root = rootPane
        }
    }


    /* ------------------------------------------------------- */

    /**
     * Shows a popup with the given message.
     *
     * @param message
     */
    //TODO: Add styling and make it look better
    def showErrorPopup(message: String): Unit = {
        val alert = new Alert(AlertType.Error) {
            title = "Error"
            headerText = "An error occurred"
            dialogPane().content = new TextArea {
                text = message
                editable = true
                wrapText = true
            }
        }
        alert.showAndWait()
    }

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
                case _ => GameController.processInput(s"match")
        })
        val button3 = createGameTaskbarButton("Discard", (e: ActionEvent) => {
            gameState match
                case _: GameStatePlanned if selectedHandCard.isDefined =>
                    GameController.processInput(s"discard ${gameState.players.head.hand.cards.indexOf(selectedHandCard.get) + 1}")
                case _: GameStateRandom =>
                    GameController.processInput("discard")
                case _ => GameController.processInput(s"discard")
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

        val playerTextField = createStyledLabel(s"Player: ${gameState.players.head.name}")
        val playerScoreField = createStyledLabel(s"Score: ${gameState.players.head.score}")

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
                    children = List(button1, button2, button3, button4, button5, button6, button7, playerTextField, playerScoreField)
                },
                rightSpacer
            )
            style = "-fx-background-color: #231F20;"
        }
    }

    /**
     * Creates a simpler Toolbar.
     *
     * @param gameState the current game state
     * @return the simpler ToolBar.
     */
    def createGameTaskbarSimple(gameState: GameState): ToolBar = {
        val button1 = createGameTaskbarButton("Help", (e: ActionEvent) => {
            GameController.processInput("help")
        })
        val button2 = createGameTaskbarButton("Combinations", (e: ActionEvent) => {
            GameController.processInput(if gameState.displayType == DisplayType.COMBINATIONS then "con" else "com")
        })
        val button3 = createGameTaskbarButton("Continue", (e: ActionEvent) => {
            GameController.processInput("continue")
        })
        val button4 = createGameTaskbarButton("Exit", (e: ActionEvent) => {
            GameController.processInput("exit")
        })

        val playerTextField = createStyledLabel(s"Player: ${gameState.players.head.name}")
        val playerScoreField = createStyledLabel(s"Score: ${gameState.players.head.score}")

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
                    children = List(button1, button2, button3, button4, playerTextField, playerScoreField)
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
     * Creates a pre-styled Label with the given prompt text.
     *
     * @param textString the prompt text to display in the label
     * @return the styled TLabel
     */
    def createStyledLabel(textString: String): Label = {
        val label = new Label(textString)
        label.style = "-fx-background-color: #231F20; " +
                      "-fx-text-fill: White; " +
                      "-fx-font-size: 14px; " +
                      "-fx-font-family: Ubuntu; " +
                      "-fx-padding: 10 20 10 20; " +
                      "-fx-background-radius: 5;"
        label
    }

    /**
     * Updates the GUI on observer notification.
     *
     * @param gameState the current state of the game
     */
    override def update(gameState: GameState): Unit = {
        Platform.runLater {
            //--------------------------------------------------------------------------------
            //Testcase Summary
            /*
            val gameState: GameState = new GameStateSummary(players = List(
                Player(name = "???", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty),
                Player(name = "???", hand = Deck(List.empty), side = Deck(List.empty), score = 0, calledKoiKoi = false, yakusToIgnore = List.empty)),
                deck = Deck(List.empty),
                board = Deck(List.empty),
                displayType = SUMMARY,
                stdout = None,
                stderr = None,
                outOfCardsEnding = false)
             */
            //--------------------------------------------------------------------------------

            if (gameState.stderr.isDefined) {
                showErrorPopup(gameState.stderr.get)
            }
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