package view

import scalafx.scene.Node
import scalafx.scene.control.{Button, TextField}
import scalafx.Includes.handle
import scalafx.event.ActionEvent

object ComponentDecoraters {
  /**
   * Base trait for UI components.
   */
  trait UIComponent {
    /**
     * Renders the UI component.
     *
     * @return the rendered Node
     */
    def render(): Node
  }

  /**
   * Class representing a basic button.
   *
   * @param text the text to display on the button
   * @param action the action to perform when the button is clicked
   */
  class BasicButton(val text: String, action: scalafx.event.ActionEvent => Unit) extends UIComponent {
    /**
     * Renders the button.
     *
     * @return the rendered Button
     */
    override def render(): Button = new Button(text) {
      onAction = handle {
        action
      }
    }
  }

  /**
   * Class representing a basic text field.
   *
   * @param prompt the prompt text to display in the text field
   */
  class BasicTextField(val prompt: String) extends UIComponent {
    /**
     * Renders the text field.
     *
     * @return the rendered TextField
     */
    override def render(): TextField = new TextField {
      promptText = prompt
    }
  }

  /**
   * Class to add styles to a UI component.
   *
   * @param component the UI component to decorate
   * @param styles a map of CSS styles to apply
   */
  class StyleDecorator(component: UIComponent, styles: Map[String, String]) extends UIComponent {
    /**
     * Renders the decorated component with styles.
     *
     * @return the rendered Node with styles applied
     */
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

  /**
   * Class to add hover effects to a UI component.
   *
   * @param component the UI component to decorate
   * @param hoverStyles a map of CSS styles to apply on hover
   */
  class HoverEffectDecorator(component: UIComponent, hoverStyles: Map[String, String]) extends UIComponent {
    /**
     * Renders the decorated component with hover effects.
     *
     * @return the rendered Node with hover effects applied
     */
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