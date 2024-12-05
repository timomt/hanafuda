package view

import scalafx.scene.Node
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint.Color
import scalafx.Includes.{eventClosureWrapperWithParam, handle}
import scalafx.event.ActionEvent
import scalafx.event.EventIncludes.eventClosureWrapperWithParam

object ComponentDecoraters {
  // Base trait for UI components
  trait UIComponent {
    def render(): Node
  }

  class BasicButton(val text: String, action: scalafx.event.ActionEvent => Unit) extends UIComponent {
    override def render(): Button = new Button(text) {
      onAction = handle {
        action
      }
    }
  }

  class BasicTextField(val prompt: String) extends UIComponent {
    override def render(): TextField = new TextField {
      promptText = prompt
    }
  }

  class StyleDecorator(component: UIComponent, styles: Map[String, String]) extends UIComponent {
    override def render(): Node = {
      val node = component.render()
      node match {
        case textField: TextField =>
          textField.style = styles.map { case (key, value) => s"$key: $value;" }.mkString
          textField
        case _ => node
      }
    }
  }

  class HoverEffectDecorator(component: UIComponent, hoverStyles: Map[String, String]) extends UIComponent {
    override def render(): Node = {
      val node = component.render()
      node match {
        case button: Button =>
          val normalStyle = button.style.value
          button.onMouseEntered = _ => button.style = hoverStyles.map { case (key, value) => s"$key: $value;" }.mkString
          button.onMouseExited = _ => button.style = normalStyle
          button
        case _ => node
      }
    }
  }
}