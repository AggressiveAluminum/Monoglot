package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;

/**
 * @author cofl
 * @date 2/15/2017
 */
public class SimpleLinkController {
    public void openLink(ActionEvent e){
        if(!(e.getSource() instanceof Hyperlink))
            return;
        Hyperlink link = (Hyperlink) e.getSource();
        Monoglot.getMonoglot().getHostServices().showDocument(link.getText());
    }
}
