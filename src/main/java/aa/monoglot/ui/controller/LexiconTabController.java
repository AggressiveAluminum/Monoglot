package aa.monoglot.ui.controller;

import aa.monoglot.project.Project;
import aa.monoglot.project.db.Headword;
import aa.monoglot.project.db.SearchFilter;
import aa.monoglot.project.db.WordType;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.util.UT;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.CheckComboBox;

import javax.swing.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/*
 * Notes on things that tripped me up:
 *  - In this class, parentController.setProjectControlsEnabled DOES NOT enable word editing.
 *    Use wordSection.setDisable(false);.
 */
public class LexiconTabController implements GeneralController {
    private static final ObservableList<Headword> EMPTY_LIST = new SimpleListProperty<>();
    @FXML private ControlledTab tab;

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
        tab.controller(this);
        filter = new SearchFilter(searchField.textProperty(), searchType.getSelectionModel().selectedIndexProperty(),
                searchCategory.getSelectionModel().selectedIndexProperty(), searchTags.getCheckModel().getCheckedIndices());
        searchResults.setOnMouseClicked(event -> {
            try {
                switchActiveWord(searchResults.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {
                //TODO: deal with it.
            }
        });
    }

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
        if(save()) {
            if (newHeadword != null) {
                activeWord = newHeadword;
                updateWordUI();
                wordSection.setDisable(false);
            }
            return true;
        }
        return false;
    }

    void createNewWord(ActionEvent event) throws SQLException {
        if(!switchActiveWord(Headword.create()))
            System.err.println("Couldn't switch to a new word!");
    }

    @FXML private void deleteWord(ActionEvent event) {
        //TODO
    }

    @FXML private void saveWord(ActionEvent event){
        save();
    }

    public boolean hasUnsavedWord(){
        return activeWord != null && activeWord.ID == null && headwordField.getText() != null && !headwordField.getText().equals("");
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
    public boolean save(){
        try {
            if (activeWord != null) {
                if (activeWord.ID == null && (headwordField.getText() == null || headwordField.getText().equals("")))
                    return true;
                if (!verifyFields()) {
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
        } catch(SQLException e){
            //TODO: tell someone
        }
        return false;
    }

    @Override
    public boolean onLoad(){
        //TODO: load more things.
        try {
            if (activeWord != null) {
                activeWord = Headword.fetch(activeWord.ID);
                loadWordList();
                updateWordUI();
                wordSection.setDisable(false);
            }
            return true;
        } catch(SQLException e){
            //TODO: rethrow?
            return false;
        }
    }

    @Override
    public boolean onUnload(){
        if(save()){
            clearInfo();
            wordSection.setDisable(true);
            return true;
        }
        return false;
    }
}