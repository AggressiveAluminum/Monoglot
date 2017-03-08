package aa.monoglot.ui.controller;

import aa.monoglot.project.Project;
import aa.monoglot.project.db.Headword;
import aa.monoglot.project.db.SearchFilter;
import aa.monoglot.project.db.WordType;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.CheckComboBox;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

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
    @FXML private ComboBox<?> categoryField;
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

    boolean switchActiveWord(Headword newHeadword) throws SQLException {
        if(activeWord != null){
            {
                //TODO: type and category lookup
                Integer type = typeField.getSelectionModel().isEmpty()?null:typeField.getSelectionModel().getSelectedIndex();
                Integer category = categoryField.getSelectionModel().isEmpty()?null:categoryField.getSelectionModel().getSelectedIndex();

                activeWord = activeWord.update(headwordField.getText(), romanizationField.getText(),
                        pronunciationField.getText(), stemField.getText(), null, null);
            }
            if(!verify(activeWord)){
                //TODO: tell somebody;
                return false;
            }
            Project.getProject().getDatabase().put(activeWord);
        }
        if(newHeadword != null) {
            activeWord = newHeadword;
            updateWordUI();
        }
        return true;
    }

    void createNewWord(ActionEvent event) throws SQLException {
        //TODO: exception handling.
        switchActiveWord(Headword.create());
    }
    private void updateWordUI(){
        headwordField.setText(activeWord.word);
        romanizationField.setText(activeWord.romanization);
        pronunciationField.setText(activeWord.pronunciation);
        stemField.setText(activeWord.stem);
        createdLabel.setText(activeWord.created.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

        modifiedLabel.setText(activeWord.modified == null?"":activeWord.modified.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        if(activeWord.type == null)
            typeField.getSelectionModel().clearSelection();
        //else typeField.getSelectionModel().select(activeWord.type);
        if(activeWord.category == null)
            categoryField.getSelectionModel().clearSelection();
        //else typeField.getSelectionModel().select(activeWord.category);
        //TODO: category/type lookup
        //TODO: tags
    }

    private static boolean verify(Headword headword){
        if(headword.word.equals(""))
            return false;
        //TODO: verification of categories and types
        return true;
    }
}

