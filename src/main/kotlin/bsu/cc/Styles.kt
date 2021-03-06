package bsu.cc

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*
import javax.swing.text.TableView

class Styles : Stylesheet() {
    companion object {
        val welcomeScreen by cssclass()
        val content by cssclass()
        val heading by cssclass()
        val footer by cssclass()
        val bold by cssclass()
        val fileChooser by cssclass()
        val font by cssclass()

        val boxHeight = 40.px
    }

    init {
        font {
            fontSize = 12.pt
            font = Font.font("Abel")
        }
        welcomeScreen {
            backgroundColor += LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, c("#ddddde")), Stop(1.0, c("#eeeeee")))
            minWidth = 700.px
        }
        heading {
            padding = box(10.px)
            fontSize = 3.em
            textFill = Color.BLACK
            fontWeight = FontWeight.BOLD
        }
        fileChooser {
            padding = box(10.px, 0.px)
        }
        content {
            padding = box(25.px)
            button {
                fontSize = 22.px
                startMargin = 25.px
                minHeight = boxHeight
                maxHeight = boxHeight
                padding = box(4.px)
            }
            textField {
                minHeight = boxHeight
                maxHeight = boxHeight
            }
        }
        footer {
            padding = box(0.px,25.px,25.px,25.px)
            button {
                textFill = Color.BLACK
                fontSize = 22.px
            }
        }
        bold {
            fontWeight = FontWeight.EXTRA_BOLD
        }
    }
}