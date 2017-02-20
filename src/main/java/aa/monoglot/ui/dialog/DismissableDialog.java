package aa.monoglot.ui.dialog;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * @author cofl
 * @date 2/20/2017
 */
public class DismissableDialog<R> extends Dialog<R> {
    {
        setOnCloseRequest(e->{
            System.out.println("Requested!");
            hide();
        });
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node button = getDialogPane().lookupButton(ButtonType.CLOSE);
        button.setVisible(false);
        button.setManaged(false);
    }
}
