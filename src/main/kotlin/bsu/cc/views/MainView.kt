package bsu.cc.views

import bsu.cc.Styles
import bsu.cc.parser.identifyAndWriteConflicts
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import tornadofx.*
import java.lang.IllegalStateException
import java.time.LocalTime
import java.util.*
import kotlin.String
import kotlin.collections.ArrayList

class MainView : View("Conflict Checker") {
    var fileNameField: TextField by singleAssign()
    private val CONSTRAINT_PATH_KEY = "constraintsFilePath"
    private val total = SimpleIntegerProperty()
    private val priority = SimpleIntegerProperty()
    private val non = SimpleIntegerProperty()

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
                                        set(CONSTRAINT_PATH_KEY to fileList[0].absolutePath)
                                        save()
                                    }
                                }
                            }
                            item("Export", "Shortcut+E").action {
                                println("Constraint file path is ${config[CONSTRAINT_PATH_KEY]}")
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
                        hbox(80) {
                            padding = insets(15, 10, 0, 10)
                            hbox(100) {
                                hbox(15) {
                                    label("Total Conflicts") {
                                        addClass(Styles.bold)
                                    }
                                    label("NaN") {
                                        bind(total)
                                    }
                                }
                                hbox(15) {
                                    label("Priority") {
                                        addClass(Styles.bold)
                                    }
                                    label("NaN") {
                                        bind(priority)
                                    }
                                }
                                hbox(15) {
                                    label("Non-priority") {
                                        addClass(Styles.bold)
                                    }
                                    label("NaN") {
                                        bind(non)
                                    }
                                }
                            }
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
                                showConflicts(fileNameField.text)
                            }
                        }
                    }
                }
            }
        }
    }

    fun showConflicts(fileName : String) {
        val newConflicts = identifyAndWriteConflicts(fileName)

        var id = 0
        var priorityCount = 0
        var nonCount = 0
        newConflicts.keys.forEach { constraint ->
            if(constraint.priority.toString().equals("PRIORITY")) priorityCount++ else nonCount++
//            (newConflicts[constraint]?: throw IllegalStateException("Key does not have value")).forEach { classSchedules ->
//                id++
//                classSchedules.forEach { entry ->
//                    toDisplay.add(Conflict(id, entry.catalogNumber, priority, entry.description, entry.startTime, entry.room))
//                }
//            }
        }

        total.value = priorityCount + nonCount
        priority.value = priorityCount
        non.value = nonCount
    }
}