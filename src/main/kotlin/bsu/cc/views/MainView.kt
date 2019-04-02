package bsu.cc.views

import bsu.cc.ConfigurationKeys
import bsu.cc.Styles
import bsu.cc.parser.identifyAndWriteConflicts
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.lang.IllegalStateException
import java.time.LocalTime
import java.util.*
import kotlin.String

class MainView : View("Conflict Checker") {
    var fileNameField: TextField by singleAssign()

    var conflicts = mutableListOf<Conflict>(
            Conflict(1, "num","Priority","Sample Class",LocalTime.of(13,15,0), "room")
    ).observable()

    override val root = borderpane {
        addClass(Styles.welcomeScreen)
        top {
            borderpane {
                top {
                    menubar {
                        menu("File") {
                            item("Choose Constraints File").action {
                                val fileList = chooseFile("Constraints File", arrayOf(FileChooser.ExtensionFilter("CSV", "*.csv")), FileChooserMode.Single)
                                if(fileList.isNotEmpty()) {
                                    with(config) {
                                        set(ConfigurationKeys.CONSTRAINT_PATH_KEY to fileList[0].absolutePath)
                                        save()
                                    }
                                }
                            }
                            item("Export", "Shortcut+E").action {
                                println("Constraint file path is ${config[ConfigurationKeys.CONSTRAINT_PATH_KEY]}")
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
                                val file = FileChooser().showOpenDialog(null)
                                if (file != null) {
                                    fileNameField.text = file.absolutePath
                                }
                            }
                        }
                    }

                    center {
                        fileNameField = textfield("""..\..\..\src\main\resources\Spring 2019 Validation Report Example.xlsx""")
                    }

                    bottom {
                        tableview(conflicts) {
                            isEditable = true
                            column("Conflict ID", Conflict::conflictIdProp).makeEditable()
                            column("Class ID", Conflict::classNumberProp).makeEditable()
                            column("Priority", Conflict::priorityProp).makeEditable()
                            column("Class Name", Conflict::fullNameProp).makeEditable()
                            column("Start Time", Conflict::timeProp).makeEditable()
                            column("Room", Conflict::roomProp).makeEditable()
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
                        button("Process") {
                            setOnAction {
                                showConflicts(fileNameField.text, config)
                            }
                        }
                    }
                }
            }
        }
    }

    fun showConflicts(fileName : String, config: ConfigProperties) {
        val outputFile = File(identifyAndWriteConflicts(fileName, config))
        Desktop.getDesktop().open(outputFile)
    }
}

class Conflict(id: Int, classNumber: String, priority: String, fullName: String, time: LocalTime, room: String) {
    val conflictIdProp = SimpleIntegerProperty(id)
    var id by conflictIdProp

    val classNumberProp = SimpleStringProperty(classNumber)
    var classNumber by classNumberProp

    val priorityProp = SimpleStringProperty(priority)
    var priority by priorityProp

    val fullNameProp = SimpleStringProperty(fullName)
    var fullName by fullNameProp

    val timeProp = SimpleObjectProperty(time)
    var time by timeProp

    val roomProp = SimpleStringProperty(room)
    var room by roomProp

    // Make age an observable value as well
    // val ageProperty = birthdayProperty.objectBinding { Period.between(it, LocalDate.now()).years }
}