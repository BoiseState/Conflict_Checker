package bsu.cc

import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val welcomeScreen by cssclass()
        val content by cssclass()
        val heading by cssclass()
        val footer by cssclass()
    }

    init {
        welcomeScreen {
            backgroundColor += LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, c("#ddddde")), Stop(1.0, c("#eeeeee")))
            heading {
                padding = box(10.px)
                fontSize = 3.em
                textFill = Color.BLACK
                fontWeight = FontWeight.BOLD
            }
            content {
                padding = box(25.px)
                button {
                    fontSize = 22.px
                }
            }
            footer {
                label {
                    textFill = Color.BLACK
                    padding = box(5.px,10.px)
                }
            }
        }
    }
}