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
import java.time.Period

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

                val persons = listOf(
                        Person(1,"Samantha Stuart",LocalDate.of(1981,12,4)),
                        Person(2,"Tom Marks",LocalDate.of(2001,1,23)),
                        Person(3,"Stuart Gills",LocalDate.of(1989,5,23)),
                        Person(3,"Nicole Williams",LocalDate.of(1998,8,11))
                ).observable()

                tableview(persons) {
                    column("ID", Person::idProperty)
                    column("Name", Person::nameProperty)
                    column("Birthday", Person::birthdayProperty)
                    column("Age", Person::ageProperty)
                }

                button("Highlight Test") {
                    setOnAction{
                        DemoParser.highlightTest(fileNameField.text)
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

class Person(id: Int, name: String, birthday: LocalDate) {
    val idProperty = SimpleIntegerProperty(id)
    var id by idProperty

    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    val birthdayProperty = SimpleObjectProperty(birthday)
    var birthday by birthdayProperty

    // Make age an observable value as well
    val ageProperty = birthdayProperty.objectBinding { Period.between(it, LocalDate.now()).years }
}