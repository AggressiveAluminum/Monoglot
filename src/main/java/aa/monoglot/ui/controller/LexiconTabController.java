package aa.monoglot.ui.controller;

import aa.monoglot.misc.keys.LogString;
import aa.monoglot.project.Project;
import aa.monoglot.project.db.*;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.ui.component.DefinitionCell;
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
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/*
 * Notes on things that tripped me up:
 *  - In this class, parentController.setProjectControlsEnabled DOES NOT enable word editing.
 *    Use wordSection.setDisable(false);.
 */
public class LexiconTabController implements GeneralController {
    private static final ObservableList<Headword> EMPTY_LIST = new SimpleListProperty<>();
    @FXML private ControlledTab tab;
    private final DefinitionHandlers definitionHandlers = new DefinitionHandlers();

    @FXML private TextField searchField;
    @FXML private ComboBox<Type> searchType;
    @FXML private ComboBox<Category> searchCategory;
    @FXML private CheckComboBox<Tag> searchTags;
    public ListView<Headword> searchResults;

    @FXML private BorderPane wordSection;
    private Headword activeWord;
    @FXML private TextField headwordField, pronunciationField, romanizationField, stemField;
    @FXML private ComboBox<Type> typeField;
    @FXML private ComboBox<Category> categoryField;
    @FXML private CheckComboBox<Tag> tagsField;
    @FXML private Label createdLabel, modifiedLabel;

    @FXML private VBox definitionsListPane;
    private ObservableList<DefinitionCell> definitionsList;

    @SuppressWarnings("unchecked")
    @FXML private void initialize(){
        tab.controller(this);
        searchField.textProperty().addListener(this::searchListener);
        searchTags.getCheckModel().getCheckedIndices().addListener(this::searchListener);
        searchType.getSelectionModel().selectedIndexProperty().addListener(this::searchListener);
        searchCategory.getSelectionModel().selectedIndexProperty().addListener(this::searchListener);
        searchResults.setOnMouseClicked(event -> {
            try {
                switchActiveWord(searchResults.getSelectionModel().getSelectedItem());
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        definitionsListPane.getChildren().setAll(FXCollections.observableArrayList());
        // don't try this at home, kids.
        definitionsList = (ObservableList<DefinitionCell>) (ObservableList) definitionsListPane.getChildren();
    }

    @SuppressWarnings("unchecked")
    private void clearInfo() throws SQLException, IOException {
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
        populateDefinitions(Collections.emptyList());

        wordSection.setDisable(true);
    }

    boolean switchActiveWord(Headword newHeadword) throws SQLException, IOException {
        if(save()) {
            if (newHeadword != null) {
                activeWord = newHeadword;
                updateWordUI();
            }
            return true;
        }
        return false;
    }

    void createNewWord(ActionEvent event) throws SQLException, IOException {
        if(!switchActiveWord(Headword.create()))
            Log.warning(LogString.LEXICON_SWITCH_FAILED);
        else Log.fine(LogString.LEXICON_NEW_WORD);
    }

    @FXML private void deleteWord(ActionEvent event) throws SQLException, IOException {
        if(activeWord != null) {
            Headword.delete(activeWord);
            Log.info(LogString.LEXICON_WORD_DELETED, activeWord.word);
            activeWord = null;
            updateWordUI();
        }
    }

    @FXML private void saveWord(ActionEvent event){
        save();
    }

    private void updateWordUI() throws SQLException, IOException {
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

        int temp;
        if(activeWord == null || activeWord.type == null)
            typeField.getSelectionModel().clearSelection();
        else if((temp = typeField.getItems().indexOf(activeWord.type)) != -1)
            typeField.getSelectionModel().select(temp);
        if(activeWord == null || activeWord.category == null)
            categoryField.getSelectionModel().clearSelection();
        else if((temp = categoryField.getItems().indexOf(activeWord.category)) != -1)
            categoryField.getSelectionModel().select(temp);
        if(activeWord != null) {
            List<Tag> tags = Tag.fetchFor(activeWord);
        }
        populateDefinitions(null);
        loadWordList();
    }

    @SuppressWarnings("unchecked")
    private void populateDefinitions(List<Definition> list) throws SQLException, IOException {
        definitionsListPane.getChildren().setAll(FXCollections.observableArrayList());
        // don't try this at home, kids.
        definitionsList = (ObservableList<DefinitionCell>) (ObservableList) definitionsListPane.getChildren();

        if(list == null)
            list = Definition.fetch(activeWord);
        for(Definition definition: list)
            definitionsList.add(new DefinitionCell(definition, definitionHandlers));
        if(definitionsList.size() > 0)
            definitionsList.get(definitionsList.size() - 1).setIsLast(true);
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
        if(field == null || field.length() == 0)
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
                if (!verifyFields())
                    return false;
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
        } catch(SQLException | IOException e){
            //TODO: tell someone
        }
        return false;
    }

    @Override
    public boolean onLoad(){
        try {
            if (activeWord != null)
                activeWord = Headword.fetch(activeWord.ID);
            ObservableList<Type> types = FXCollections.observableList(Type.fetchAll());
                typeField.setItems(types);
                searchType.setItems(types);
            /*ObservableList<Category> categories = FXCollections.observableList(Category.fetchAll());
                categoryField.setItems(categories);
                searchCategory.setItems(categories);*/
            ObservableList<Tag> tags = FXCollections.observableList(Tag.fetchAll());
                tagsField.getItems().setAll(tags);
                searchTags.getItems().setAll(tags);
            updateWordUI();
            return true;
        } catch(SQLException | IOException e){
            Log.warning(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean onUnload(){
        try {
            clearInfo();
        } catch(SQLException |IOException e){
            //TODO: what to do?
        }
        wordSection.setDisable(true);
        return true;
    }

    @FXML
    private void createDefinition(ActionEvent event) throws SQLException, IOException {
        if(activeWord != null) {
            if(activeWord.ID == null)
                if(!verifyFields() || !save())
                    return;
            Definition previous = definitionsList.isEmpty()?null: definitionsList.get(definitionsList.size() - 1).getDefinition();
            if(definitionsList.size() > 0)
                definitionsList.get(definitionsList.size() - 1).setIsLast(false);
            definitionsList.add(new DefinitionCell(Definition.create(activeWord, previous), definitionHandlers));
            if(definitionsList.size() > 0)
                definitionsList.get(definitionsList.size() - 1).setIsLast(true);
            Log.info(LogString.LEXICON_DEF_ADDED, activeWord.word);
        }
    }

    public class DefinitionHandlers {
        public void up(ActionEvent event) {
            try {
                DefinitionCell cell = (DefinitionCell) ((Button) event.getSource()).getParent().getParent();
                populateDefinitions(cell.getDefinition().update(cell.getDefinition().order - 1));
            } catch(SQLException | IOException e){
                throw new RuntimeException(e);
            }
        }

        public void down(ActionEvent event){
            try {
                DefinitionCell cell = (DefinitionCell) ((Button) event.getSource()).getParent().getParent();
                populateDefinitions(cell.getDefinition().update(cell.getDefinition().order + 1));
            } catch(SQLException | IOException e){
                throw new RuntimeException(e);
            }
        }

        public void delete(ActionEvent event){
            try {
                DefinitionCell cell = (DefinitionCell) ((Button) event.getSource()).getParent().getParent();
                Definition.delete(cell.getDefinition());
                populateDefinitions(Definition.fetch(activeWord));
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}