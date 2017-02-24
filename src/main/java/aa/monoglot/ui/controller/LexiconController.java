package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.db.Headword;
import aa.monoglot.db.SearchFilter;
import aa.monoglot.db.WordType;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.CheckComboBox;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/12/2017
 */
public class LexiconController extends AbstractChildController<MonoglotController> {
    private static final ObservableList<Headword> EMPTY_LIST = new SimpleListProperty<>();
    @FXML private ResourceBundle resources;

    @FXML private TextField searchField;
    @FXML private ComboBox<?> searchType;
    @FXML private ComboBox<?> searchCategory;
    @FXML private CheckComboBox<?> searchTags;
    
    private SearchFilter filter;

    //TODO: change this to HeadWord, modify toString to return proper result.
    public ListView<Headword> searchResults;

    @FXML private BorderPane wordSection;
    public Headword activeWord;
    @FXML private TextField headwordField, pronunciationField, romanizationField, stemField;
    @FXML private ComboBox<WordType> typeField;
    //@FXML private ComboBox<WordCategory> categoryField;
    @FXML private CheckComboBox<?> tagsField;
    @FXML private Label createdLabel, modifiedLabel;

    @FXML private TitledPane lexiconDefinitionsPane;

    @FXML private void initialize(){
        filter = new SearchFilter(searchField.textProperty(), searchType.getSelectionModel().selectedIndexProperty(),
                searchCategory.getSelectionModel().selectedIndexProperty(), searchTags.getCheckModel().getCheckedIndices());
        searchResults.getSelectionModel().selectedItemProperty().addListener(this::activeWordChanged);
        //TODO

        // == Field Change Listener ==
        // use listeners on the fields themselves to update?
    }

    public void projectChanged(){
        Monoglot.getMonoglot().getProject().getDBListing(filter, searchResults::setItems);
    }

    protected void postInitialize(){}

    protected void clearInfo(){
        searchResults.setItems(EMPTY_LIST);

        searchField.clear();

        searchType.getSelectionModel().clearSelection();
        searchType.getItems().clear();

        searchCategory.getSelectionModel().clearSelection();
        searchCategory.getItems().clear();

        searchTags.getCheckModel().clearChecks();
        searchTags.getItems().clear();
        //TODO
    }

    private void activeWordChanged(ObservableValue<? extends Headword> value, Headword oldWord, Headword newWord){
        /*try {
            Database db = Monoglot.getMonoglot().getProject().getDatabase();
            if(activeWord != null) {
                Headword updatedWord = activeWord.update(headwordField.getText(), pronunciationField.getText(), romanizationField.getText(), stemField.getText(), typeField.getSelectionModel().getSelectedItem());
                if(updatedWord != activeWord)
                    db.put(updatedWord);
            }
            activeWord = newWord;
        } catch(SQLException e){
            //TODO: Tell somebody.
        }*/
    }
}

