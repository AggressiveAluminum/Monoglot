package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;

public class SimpleLinkController {
    public void openLink(ActionEvent e){
        if(!(e.getSource() instanceof Hyperlink))
            return;
        Hyperlink link = (Hyperlink) e.getSource();
        Monoglot.getMonoglot().getHostServices().showDocument(link.getText());
    }
}
