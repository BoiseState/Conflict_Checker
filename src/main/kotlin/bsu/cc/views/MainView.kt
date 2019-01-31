package bsu.cc.views

import bsu.cc.Styles
import bsu.cc.parser.ParserDemo
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.control.TextField
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    var fileNameField: TextField by singleAssign()

    override val root = borderpane {
        addClass(Styles.welcomeScreen)
        top {
            stackpane {
                label(title).addClass(Styles.heading)
            }
        }
        center {
            vbox {
                addClass(Styles.content)
                button("Click me") {
                    setOnAction {
                        alert(INFORMATION, "Well done!", "You clicked me!")
                    }
                }
                button("Print Test") {
                    setOnAction{
                        ParserDemo.demoParse(fileNameField.text)
                    }
                }
            }
        }
        bottom {
            hbox {
                label("File Name")
                fileNameField = textfield()
            }
        }
    }
}