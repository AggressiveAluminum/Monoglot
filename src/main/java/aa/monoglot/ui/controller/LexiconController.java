package aa.monoglot.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/12/2017
 */
public class LexiconController extends AbstractChildController<MonoglotController> {
    @FXML
    private ResourceBundle resources;

    @FXML
    private VBox searchSection;

    //TODO: change this to HeadWord, modify toString to return proper result.
    public ListView<String> searchResults;

    @FXML
    private void initialize(){
        searchResults.getItems().addAll(resources.getString("placeholder.long").split("\\s+"));
    }

    protected void postInitialize(){}
}
