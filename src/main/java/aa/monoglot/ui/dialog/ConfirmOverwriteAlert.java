package aa.monoglot.ui.dialog;

import javafx.scene.control.Alert;
import javafx.stage.Window;

/**
 * @author cofl
 * @date 2/22/2017
 */
public class ConfirmOverwriteAlert extends Alert {
    public ConfirmOverwriteAlert(Window owner, String headerText){
        super(AlertType.CONFIRMATION);
        initOwner(owner);
        setHeaderText(headerText);
    }
}
