package aa.monoglot.ui.dialog;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.misc.keys.LocalizationKey;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Dialogs {
    private static List<FileChooser.ExtensionFilter> fileFilter;
    public static FileChooser getChooser() {
        ensureExtensionFilters();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(fileFilter);
        chooser.setSelectedExtensionFilter(fileFilter.get(0));
        return chooser;
    }

    private static void ensureExtensionFilters(){
        fileFilter = Arrays.asList(
                new FileChooser.ExtensionFilter(Monoglot.getMonoglot().getLocalString(AppString.MGLT_FILE), "*.mglt", "*.monoglot"),
                new FileChooser.ExtensionFilter(Monoglot.getMonoglot().getLocalString(AppString.ALL_FILES), "*")
        );
    }

    public static Optional<ButtonType> yesNoCancel(LocalizationKey key){
        return new YesNoCancelAlert(Monoglot.getMonoglot().getWindow(), Monoglot.getMonoglot().getLocalString(key)).showAndWait();
    }

    public static Alert error(Window owner, Throwable cause, String title, String header, String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        if(owner != null && owner.isShowing())
            alert.initOwner(owner);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        StringWriter writer = new StringWriter();
            cause.printStackTrace(new PrintWriter(writer));
        TextArea area = new TextArea(writer.toString());
            area.setEditable(false);
            area.setWrapText(true);
        alert.getDialogPane().setExpandableContent(area);

        return alert;
    }

    public static Alert warning(Window owner, String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        if(owner != null && owner.isShowing())
            alert.initOwner(owner);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        return alert;
    }

    public static String promptHeadword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.getDialogPane().setStyle(".header-panel {visibility:hidden;-fx-max-height:0}");
        dialog.initOwner(Monoglot.getMonoglot().getWindow());
        dialog.setTitle(Monoglot.getMonoglot().getLocalString(AppString.HEADWORD_PROMPT_TITLE));
        dialog.setHeaderText(Monoglot.getMonoglot().getLocalString(AppString.HEADWORD_PROMPT_HEADER));
        dialog.getEditor().setPromptText(Monoglot.getMonoglot().getLocalString(AppString.HEADWORD_PROMPT_TEXT));

        Optional<String> result = dialog.showAndWait();
        return result.filter(e->!e.isEmpty()).orElse(null);
    }
}
