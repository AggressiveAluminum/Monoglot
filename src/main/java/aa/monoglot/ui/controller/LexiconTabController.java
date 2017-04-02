package aa.monoglot.ui.controller;

import aa.monoglot.misc.keys.LogString;
import aa.monoglot.project.Project;
import aa.monoglot.project.db.Definition;
import aa.monoglot.project.db.Headword;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.ui.component.DefinitionCellFactory;
import aa.monoglot.util.Log;
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
import java.util.UUID;

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
    public ListView<Headword> searchResults;

    @FXML private BorderPane wordSection;
    public Headword activeWord;
    @FXML private TextField headwordField, pronunciationField, romanizationField, stemField;
    @FXML private ComboBox<?> typeField;//TODO
    @FXML private ComboBox<?> categoryField;//TODO
    @FXML private CheckComboBox<?> tagsField;
    @FXML private Label createdLabel, modifiedLabel;

    @FXML private TitledPane lexiconDefinitionsPane;
    @FXML private ListView<Definition> definitionsList;

    @FXML private void initialize(){
        tab.controller(this);
        searchField.textProperty().addListener(this::searchListener);
        searchTags.getCheckModel().getCheckedIndices().addListener(this::searchListener);
        searchType.getSelectionModel().selectedIndexProperty().addListener(this::searchListener);
        searchCategory.getSelectionModel().selectedIndexProperty().addListener(this::searchListener);
        searchResults.setOnMouseClicked(event -> {
            try {
                switchActiveWord(searchResults.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {
                //TODO: deal with it.
            }
        });
        definitionsList.setCellFactory(new DefinitionCellFactory());
    }

    public void clearInfo() throws SQLException {
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
        definitionsList.setItems(Definition.fetch(null));
        wordSection.setDisable(true);
    }

    boolean switchActiveWord(Headword newHeadword) throws SQLException {
        if(save()) {
            if (newHeadword != null) {
                activeWord = newHeadword;
                updateWordUI();
            }
            return true;
        }
        return false;
    }

    void createNewWord(ActionEvent event) throws SQLException {
        if(!switchActiveWord(Headword.create()))
            Log.warning(LogString.LEXICON_SWITCH_FAILED);
    }

    @FXML private void deleteWord(ActionEvent event) throws SQLException {
        if(activeWord != null) {
            Headword.delete(activeWord);
            activeWord = null;
            updateWordUI();
        }
    }

    @FXML private void saveWord(ActionEvent event){
        save();
    }

    private void updateWordUI() throws SQLException {
        if(activeWord == null)
            wordSection.setDisable(true);
        else wordSection.setDisable(false);
        headwordField.setText(activeWord == null?"":activeWord.word);
        romanizationField.setText(activeWord == null?"":activeWord.romanization);
        pronunciationField.setText(activeWord == null?"":activeWord.pronunciation);
        stemField.setText(activeWord == null?"":activeWord.stem);
        createdLabel.setText(activeWord == null?"":activeWord.created.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        if(activeWord != null && activeWord.modified != null)
            modifiedLabel.setText(activeWord.modified.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        else modifiedLabel.setText("");
        if(activeWord == null || activeWord.type == null)
            typeField.getSelectionModel().clearSelection();
        //else typeField.getSelectionModel().select(activeWord.type);
        if(activeWord == null || activeWord.category == null)
            categoryField.getSelectionModel().clearSelection();
        //else typeField.getSelectionModel().select(activeWord.category);

        definitionsList.setItems(Definition.fetch(activeWord));
        //TODO: category/type lookup
        //TODO: tags
        loadWordList();
    }

    @FXML private void searchListener(Observable observable){
        try {
            loadWordList();
        } catch (SQLException e){
            //TODO: do something
        }
    }
    private void loadWordList() throws SQLException {
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
                UUID type = null;// = typeField.getSelectionModel().isEmpty()?null:typeField.getSelectionModel().getSelectedItem().ID;
                UUID category = null;// = categoryField.getSelectionModel().isEmpty()?null:categoryField.getSelectionModel().getSelectedItem().ID;

                Headword updatedWord = activeWord.update(headwordField.getText(), romanizationField.getText(),
                        pronunciationField.getText(), stemField.getText(), null, null);
                if(updatedWord != activeWord) {
                    activeWord = Headword.put(updatedWord);
                    Project.getProject().markSaveNeeded();
                }
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
        try {
            if (activeWord != null)
                activeWord = Headword.fetch(activeWord.ID);
            updateWordUI();
            return true;
        } catch(SQLException e){
            Log.warning(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean onUnload(){
        try {
            clearInfo();
        } catch(SQLException e){
            //TODO: what to do?
        }
        wordSection.setDisable(true);
        return true;
    }
}