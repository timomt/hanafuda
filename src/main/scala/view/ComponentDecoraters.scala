package view

import scalafx.scene.effect.DropShadow
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color

object ComponentDecoraters {
  trait CardComponent {
    def createCard(): StackPane
  }

  trait CardDecorator extends CardComponent {
    protected val decoratedCard: CardComponent

    override def createCard(): StackPane = decoratedCard.createCard()
  }

  class BasicCardComponent(cardImage: ImageView) extends CardComponent {
    override def createCard(): StackPane = {
      new StackPane {
        children = cardImage
        minWidth = 50
        minHeight = 100
      }
    }
  }

  class HighlightDecorator(override protected val decoratedCard: CardComponent) extends CardDecorator {
    override def createCard(): StackPane = {
      val card = super.createCard()
      card.onMouseClicked = _ => {
        card.scaleX = 1.2
        card.scaleY = 1.2
        card.effect = new DropShadow {
          color = Color.Black
          radius = 20
          spread = 0.4
        }
      }
      card
    }
  }

  class EffectDecorator(override protected val decoratedCard: CardComponent) extends CardDecorator {
    override def createCard(): StackPane = {
      val card = super.createCard()
      card.effect = new DropShadow {
        color = Color.Black
        radius = 10
        spread = 0.2
      }
      card
    }
  }
}