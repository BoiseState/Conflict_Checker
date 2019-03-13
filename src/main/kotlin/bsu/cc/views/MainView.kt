package bsu.cc.views

import bsu.cc.Styles
import bsu.cc.parser.DemoParser
import bsu.cc.parser.identifyAndWriteConflicts
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.control.TextField
import javafx.scene.control.SelectionMode
import tornadofx.*

class MainView : View("Conflict Checker") {
    var fileNameField: TextField by singleAssign()

    override val root = borderpane {
        addClass(Styles.welcomeScreen)
        top {
            borderpane {
                top {
                    menubar {
                        menu("File") {
                            item("Export", "Shortcut+E").action {
                                println("Exporting! (TO BE IMPLEMENTED)")
                            }
                        }
                        menu("Edit") {
                            item("Copy", "Shortcut+C").action {
                                println("Copying!")
                            }
                            item("Paste", "Shortcut+V").action {
                                println("Pasting!")
                            }
                        }
                        menu("View") {
                            item("Theme").action {
                                println("Theme needs to be implemented")
                            }
                        }
                    }
                }
                bottom {
                    label(title).addClass(Styles.heading)
                }
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
                        DemoParser.producerTest(fileNameField.text)
                    }
                }
                val greekLetters = listOf("Alpha","Beta",
                        "Gamma","Delta","Epsilon").observable()
                listview(greekLetters) {
                    selectionModel.selectionMode = SelectionMode.SINGLE
                }
                button("Highlight Test") {
                    setOnAction{
                        identifyAndWriteConflicts(fileNameField.text)
                    }
                }
            }
        }
        bottom {
            hbox {
                addClass(Styles.footer)
                label("File Name")
                fileNameField = textfield("""src\main\resources\Spring 2019 Validation Report Example.xlsx""")
            }
        }
    }
}