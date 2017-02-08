package aa.monoglot.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class MonoglotController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private Label counter;
    private int count = 0;

    @FXML
    private void initialize(){
        counter.setText(Integer.toString(count));
    }

    @FXML
    private void incrementCounter(ActionEvent event) {
        counter.setText(Integer.toString(++count));
    }
}
