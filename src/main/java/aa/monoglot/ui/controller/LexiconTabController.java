package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.misc.keys.LogString;
import aa.monoglot.project.Project;
import aa.monoglot.project.db.*;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.ui.component.DefinitionCell;
import aa.monoglot.ui.dialog.Dialogs;
import aa.monoglot.util.Log;
import aa.monoglot.util.UT;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;

public class LexiconTabController implements GeneralController {
    private static final PseudoClass ERROR_CLASS = PseudoClass.getPseudoClass("invalid-field");
    private static final PseudoClass GREY_ITALICS = PseudoClass.getPseudoClass("grey-italics");
    @FXML private ControlledTab tab;
    private final DefinitionHandlers definitionHandlers = new DefinitionHandlers();

    @FXML private TextField searchField;
    @FXML private ComboBox<Type> searchType;
    @FXML private ComboBox<Category> searchCategory;
    @FXML private CheckComboBox<Tag> searchTags;
    @FXML private ListView<Headword> searchResults;
    @FXML private Label searchResultsCount;

    @FXML private GridPane wordSection;
    @FXML private VBox timestampsSection;
    @FXML private Button deleteWordButton;
    @FXML private Button newDefinitionButton;
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

        // === SEARCH ===
        InvalidationListener searchListener = (e) -> {
            try {
                loadWordList();
            } catch(SQLException ex){throw new RuntimeException(ex);}
        };
        searchField.textProperty().addListener(searchListener);
        searchTags.getCheckModel().getCheckedIndices().addListener(searchListener);
        searchType.getSelectionModel().selectedIndexProperty().addListener(searchListener);
        searchCategory.getSelectionModel().selectedIndexProperty().addListener(searchListener);
        searchResults.setOnMouseClicked(event -> {
            try {
                switchActiveWord(searchResults.getSelectionModel().getSelectedItem());
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        // === HEADWORD FOCUS ===
        headwordField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!n && activeWord != null && verifyFields()) {
                    activeWord = activeWord.updateWord(headwordField.getText().trim());
                    updateWordUI();
                }
            } catch(SQLException | IOException e){throw new RuntimeException(e);}
        });
        headwordField.textProperty().addListener(e->verifyFields());
        romanizationField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!n && activeWord != null && verifyFields()){
                    activeWord = activeWord.updateRomanization(romanizationField.getText());
                    updateWordUI();
                }
            } catch(SQLException | IOException e){throw new RuntimeException(e);}
        });
        romanizationField.textProperty().addListener(e->verifyFields());
        pronunciationField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!n && activeWord != null && verifyFields())
                    activeWord = activeWord.updatePronunciation(pronunciationField.getText());
            } catch(SQLException e){throw new RuntimeException(e);}
        });
        pronunciationField.textProperty().addListener(e->verifyFields());
        stemField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!n && activeWord != null && verifyFields()){
                    activeWord = activeWord.updateStem(stemField.getText());
                    updateWordUI();
                }
            } catch(SQLException | IOException e){throw new RuntimeException(e);}
        });
        stemField.textProperty().addListener(e->verifyFields());
        tagsField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!n && activeWord != null && verifyFields()){
                    activeWord = activeWord.updateTags(tagsField.getCheckModel().getCheckedItems());
                    updateWordUI();
                }
            } catch(SQLException | IOException e){throw new RuntimeException(e);}
        });
        typeField.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            try {
                if(activeWord != null && verifyFields()){
                    activeWord = activeWord.updateType(n);
                    updateWordUI();
                }
            } catch(SQLException | IOException e){throw new RuntimeException(e);}
        });
        categoryField.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            try {
                if(activeWord != null && verifyFields()){
                    activeWord = activeWord.updateCategory(n);
                    updateWordUI();
                }
            } catch(SQLException | IOException e){throw new RuntimeException(e);}
        });

        // === DEFINITIONS INIT ===
        definitionsListPane.getChildren().setAll(FXCollections.observableArrayList());
        // don't try this at home, kids.
        definitionsList = (ObservableList<DefinitionCell>) (ObservableList) definitionsListPane.getChildren();
    }

    @SuppressWarnings("unchecked")
    private void clearInfo() throws SQLException, IOException {
        searchResults.setItems(FXCollections.emptyObservableList());

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

        setWordControlsDisabled(true);
    }

    private void setWordControlsDisabled(boolean disabled){
        wordSection.setDisable(disabled);
        timestampsSection.setDisable(disabled);
        deleteWordButton.setDisable(disabled);
        newDefinitionButton.setDisable(disabled);
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

    @FXML
    void createNewWord(ActionEvent event) throws SQLException, IOException {
        String word = Dialogs.promptHeadword();
        if(word != null) {
            Headword headword = Headword.create(word);
            Definition.create(headword, null);
            Log.fine(LogString.LEXICON_NEW_WORD, word);
            if (!switchActiveWord(headword))
                Log.warning(LogString.LEXICON_SWITCH_FAILED);
        }
    }

    @FXML private void deleteWord(ActionEvent event) throws SQLException, IOException {
        if(activeWord != null) {
            Headword.delete(activeWord);
            Log.info(LogString.LEXICON_WORD_DELETED, activeWord.word);
            activeWord = null;
            updateWordUI();
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void updateWordUI() throws SQLException, IOException {
        setWordControlsDisabled(activeWord == null);
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
        if(activeWord != null)
            for(Tag tag: Tag.fetchFor(activeWord))
                tagsField.getCheckModel().check(tagsField.getItems().indexOf(tag));
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
    private void loadWordList() throws SQLException {
        if(Project.isOpen()) {
            ObservableList<Headword> words = Project.getProject().getDatabase().simpleSearch(
                    UT.c(searchField.getText()), searchType.getSelectionModel().getSelectedItem(),
                    searchCategory.getSelectionModel().getSelectedItem(), searchTags.getCheckModel().getCheckedItems());
            searchResultsCount.setText(Monoglot.getMonoglot().getLocalString(AppString.SEARCH_RESULT_COUNT, words.size()));
            searchResultsCount.pseudoClassStateChanged(GREY_ITALICS,words.isEmpty());
            searchResults.setItems(words);
        }
    }

    private boolean verifyFields(){
        boolean ok = true;
        if(headwordField.getText().trim().length() == 0) {
            ok = false;
            headwordField.pseudoClassStateChanged(ERROR_CLASS, true);
        } else headwordField.pseudoClassStateChanged(ERROR_CLASS, false);
        //TODO: verify other fields
        return ok;
    }

    @Override
    public boolean save(){
        try {
            if (activeWord != null) {
                if(!verifyFields())
                    return false;
                activeWord = activeWord.updateAll(headwordField.getText().trim(), romanizationField.getText(), pronunciationField.getText(),
                        stemField.getText(), typeField.getSelectionModel().getSelectedItem(), categoryField.getSelectionModel().getSelectedItem());
                for(DefinitionCell cell: definitionsList)
                    cell.save();
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
                searchCategory.setItems(categories);*///TODO
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
            throw new RuntimeException(e);
            //TODO: what to do?
        } finally {
            setWordControlsDisabled(true);
        }
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

    /**
     * Handlers for {@linkplain DefinitionCell}, so we don't end up passing a bunch of lambdas individually.
     */
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
                Log.info(LogString.LEXICON_DEF_DELETED, cell.getDefinition().order, activeWord.word);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}