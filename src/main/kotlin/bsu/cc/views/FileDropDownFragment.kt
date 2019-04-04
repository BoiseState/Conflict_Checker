package bsu.cc.views
import bsu.cc.Styles
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import tornadofx.*
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileDropDownFragment(
        val text: String,
        var dir: String,
        val onSelect: (Path?) -> Unit
) : Fragment() {

    private val files = FXCollections.observableArrayList<PathGUIWrapper>()
    private var comboBox: ComboBox<PathGUIWrapper> by singleAssign()

    override val root = borderpane {
        left {
            label(text)
        }
        center {
            comboBox = combobox {
                maxWidth = Double.MAX_VALUE
                hgrow = Priority.ALWAYS
                items = files
                onAction = EventHandler<ActionEvent> { onSelect(selectedItem?.path) }
                addEventHandler(ComboBox.ON_SHOWN) { refreshFiles() }
            }
        }
    }


    fun setSelected(selected: String) {
        try {
            comboBox.value = PathGUIWrapper(Paths.get(selected))
        } catch (e: Exception) {
            //daddy don't care
        }
    }


    private fun refreshFiles() {
        files.clear()
        try {
            files.addAll(Files.newDirectoryStream(Paths.get(dir))
                    .filter { !it.toFile().isDirectory }
                    .map { PathGUIWrapper(it) })
        } catch (_: Exception) {
            //ignore any exceptions
        }
    }
}


private class PathGUIWrapper(val path: Path) {
    override fun toString(): String {
        return path.fileName.toString()
    }
}