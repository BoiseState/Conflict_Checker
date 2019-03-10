package bsu.cc.views

import bsu.cc.Styles
import bsu.cc.parser.DemoParser
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.control.TextField
import javafx.scene.control.SelectionMode
import javafx.scene.paint.Color
import tornadofx.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period

class MainView : View("Conflict Checker") {
    var fileNameField: TextField by singleAssign()

    val conflicts = listOf(
            Conflict(1, "cs421","Analysis of Algorithms",LocalTime.of(13,15,0)),
            Conflict(1, "cs410","Databases",LocalTime.of(13,15,0)),
            Conflict(2, "cs221","Computer Science II",LocalTime.of(9,0,0)),
            Conflict(2, "cs253","Intro to Systems Programming",LocalTime.of(9,0,0))
    ).observable()

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
                borderpane {
                    left {
                        button("Choose File") {
                            setOnAction {
                                DemoParser.producerTest(fileNameField.text)
                            }
                        }
                    }

                    center {
                        fileNameField = textfield("""src\main\resources\Spring 2019 Validation Report Example.xlsx""")
                    }

                    bottom {
                        tableview(conflicts) {
                            column("Conflict ID", Conflict::conflictIdProp)
                            column("Class ID", Conflict::classNumberProp)
                            column("Class Name", Conflict::fullNameProp)
                            column("Start Time", Conflict::timeProp)
                        }
                    }
                }
            }
        }
        bottom {
            hbox {
                addClass(Styles.footer)
                borderpane {
                    right {
                        button("Export") {
                            setOnAction {
                                println("Exporting...")
                            }
                        }
                    }
                }
            }
        }
    }
}

class Conflict(id: Int, classNumber: String, fullName: String, time: LocalTime) {
    val conflictIdProp = SimpleIntegerProperty(id)
    var id by conflictIdProp

    val classNumberProp = SimpleStringProperty(classNumber)
    var classNumber by classNumberProp

    val fullNameProp = SimpleStringProperty(fullName)
    var fullName by fullNameProp

    val timeProp = SimpleObjectProperty(time)
    var time by timeProp

    // Make age an observable value as well
    // val ageProperty = birthdayProperty.objectBinding { Period.between(it, LocalDate.now()).years }
}