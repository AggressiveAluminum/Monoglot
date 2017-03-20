package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.project.Project;
import aa.monoglot.project.db.Headword;
import aa.monoglot.project.db.SearchFilter;
import aa.monoglot.project.db.WordType;
import aa.monoglot.util.SilentException;
import aa.monoglot.util.UT;
import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
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

/*
 * Notes on things that tripped me up:
 *  - In this class, parentController.setProjectControlsEnabled DOES NOT enable word editing.
 *    Use wordSection.setDisable(false);.
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
    @FXML private ComboBox<?> categoryField;
    @FXML private CheckComboBox<?> tagsField;
    @FXML private Label createdLabel, modifiedLabel;

    @FXML private TitledPane lexiconDefinitionsPane;

    @FXML private void initialize(){
        filter = new SearchFilter(searchField.textProperty(), searchType.getSelectionModel().selectedIndexProperty(),
                searchCategory.getSelectionModel().selectedIndexProperty(), searchTags.getCheckModel().getCheckedIndices());
        searchResults.getSelectionModel().selectedItemProperty().addListener(SilentException.invalidationListener(e -> {
            switchActiveWord(searchResults.getSelectionModel().getSelectedItem());
            wordSection.setDisable(false);
        }));
    }

    protected void postInitialize(){}

    public void clearInfo(){
        searchResults.setItems(EMPTY_LIST);

        searchField.clear();

        searchType.getSelectionModel().clearSelection();
        searchType.getItems().clear();

        searchCategory.getSelectionModel().clearSelection();
        searchCategory.getItems().clear();

        searchTags.getCheckModel().clearChecks();
        searchTags.getItems().clear();

        activeWord = null;
        headwordField.setText("");
        pronunciationField.setText("");
        romanizationField.setText("");
        stemField.setText("");
        typeField.getSelectionModel().clearSelection();
        categoryField.getSelectionModel().clearSelection();
        tagsField.getCheckModel().clearChecks();
        createdLabel.setText("");
        modifiedLabel.setText("");
        wordSection.setDisable(true);
    }

    boolean switchActiveWord(Headword newHeadword) throws SQLException {
        if(saveWord()) {
            if (newHeadword != null) {
                activeWord = newHeadword;
                updateWordUI();
            }
            return true;
        }
        return false;
    }

    void createNewWord(ActionEvent event) throws SQLException {
        if(switchActiveWord(Headword.create()))
            wordSection.setDisable(false);
        else System.err.println("Couldn't switch to a new word!");
    }

    public void deleteWord(ActionEvent event) {
        //TODO
    }

    public boolean hasUnsavedWord(){
        return activeWord != null && activeWord.ID == null && headwordField.getText() != null && !headwordField.getText().equals("");
    }

    public boolean saveWord() throws SQLException {
        if(activeWord != null){
            if(activeWord.ID == null && (headwordField.getText() == null || headwordField.getText().equals("")))
                return true;
            if(!verifyFields()) {
                //TODO: mark fields. Put this in #verifyFields()?
                return false;
            }
            //TODO: type and category lookup
            //Integer type = typeField.getSelectionModel().isEmpty()?null:typeField.getSelectionModel().getSelectedIndex();
            //Integer category = categoryField.getSelectionModel().isEmpty()?null:categoryField.getSelectionModel().getSelectedIndex();

            activeWord = Project.getProject().getDatabase().put(activeWord.update(headwordField.getText(),
                    romanizationField.getText(), pronunciationField.getText(), stemField.getText(), null, null));
            updateWordUI();
        }
        return true;
    }

    void updateWordUI() throws SQLException {
        headwordField.setText(activeWord.word);
        romanizationField.setText(activeWord.romanization);
        pronunciationField.setText(activeWord.pronunciation);
        stemField.setText(activeWord.stem);
        createdLabel.setText(activeWord.created.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        if(activeWord.modified != null)
            modifiedLabel.setText(activeWord.modified.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        else modifiedLabel.setText("");
        if(activeWord.type == null)
            typeField.getSelectionModel().clearSelection();
        //else typeField.getSelectionModel().select(activeWord.type);
        if(activeWord.category == null)
            categoryField.getSelectionModel().clearSelection();
        //else typeField.getSelectionModel().select(activeWord.category);
        //TODO: category/type lookup
        //TODO: tags
        loadWordList();
    }

    public void loadWordList() throws SQLException {
        searchResults.setItems(FXCollections.observableArrayList(
                Project.getProject().getDatabase().simpleSearch(
                        UT.c(searchField.getText()), null, null, null)));
    }

    private boolean verifyFields(){
        String field = headwordField.getText();
        if(field == null || field.equals(""))
            return false;
        //TODO: verify other fields
        return true;
    }

    @Override
    public boolean unload(){
        try {
            if(saveWord()){
                clearInfo();
                wordSection.setDisable(false);
                return true;
            }
        } catch(SQLException e){
            //TODO: rethrow?
        }
        return false;
    }

    @Override
    public boolean load(){
        try {
            if (activeWord != null) {
                activeWord = Headword.fetch(activeWord.ID);
                loadWordList();
                updateWordUI();
            }
            return true;
        } catch(SQLException e){
            //TODO: rethrow?
            return false;
        }
    }
}

