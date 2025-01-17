package model.GameManager

import model.GameManager.GameManager
import model.GameManager.GameManagerDefault.GameManagerDefault

object GameManagerInstance {
    given GameManager = new GameManagerDefault()
}
