package aa.monoglot.ui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

/**
 * @author cofl
 * @date 2/20/2017
 */
public class YesNoCancelAlert extends Alert {
    public YesNoCancelAlert(){
        super(AlertType.CONFIRMATION);

        ButtonBar buttonBar=(ButtonBar)getDialogPane().lookup(".button-bar");
        buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);

        getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }

    public YesNoCancelAlert(Window owner, String headerText){
        this();
        initOwner(owner);
        setHeaderText(headerText);
    }
}
