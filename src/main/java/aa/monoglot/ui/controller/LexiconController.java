package aa.monoglot.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/12/2017
 */
public class LexiconController extends AbstractChildController<MonoglotController> {
    @FXML private ResourceBundle resources;

    @FXML private VBox searchSection;
    @FXML private TextField searchField;
    @FXML private ComboBox<?> searchType;
    @FXML private ComboBox<?> searchCategory;
    @FXML private CheckComboBox<?> searchTags;

    //TODO: change this to HeadWord, modify toString to return proper result.
    public ListView<String> searchResults;

    @FXML private BorderPane wordSection;
    //public Headword activeWord;

    @FXML private void initialize(){
        searchResults.getItems().addAll(resources.getString("placeholder.long").split("\\s+"));
    }

    protected void postInitialize(){}

    protected void clearInfo(){
        searchResults.getItems().clear();

        searchField.clear();

        searchType.getSelectionModel().clearSelection();
        searchType.getItems().clear();

        searchCategory.getSelectionModel().clearSelection();
        searchCategory.getItems().clear();

        searchTags.getCheckModel().clearChecks();
        searchTags.getItems().clear();
        //TODO
    }
}
