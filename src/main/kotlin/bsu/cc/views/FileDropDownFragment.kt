package bsu.cc.views
import bsu.cc.Styles
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ComboBox
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

    override val root = borderpane {
        left {
            label(text)
        }
        center {
            combobox<PathGUIWrapper> {
                addClass(Styles.fullWidth)
                items = files
                onAction = EventHandler<ActionEvent> { onSelect(selectedItem?.path) }
                addEventHandler(ComboBox.ON_SHOWN) { refreshFiles() }
            }
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