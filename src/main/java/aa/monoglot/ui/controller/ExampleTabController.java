package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.misc.keys.LogString;
import aa.monoglot.project.Project;
import aa.monoglot.project.db.Example;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.util.Log;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.sql.SQLException;

public class ExampleTabController implements GeneralController {
    private static final PseudoClass GREY_ITALICS = PseudoClass.getPseudoClass("grey-italics");
    @FXML private ControlledTab tab;
    @FXML private JFXTextField searchField;
    @FXML private Label searchResultsCount;
    @FXML private JFXListView<Example> searchResults;
    @FXML private HBox exampleSection;
    @FXML private JFXButton deleteButton;
    @FXML private Label exampleIDLabel;
    @FXML private JFXTextField textField;
    @FXML private JFXTextField glossField;
    @FXML private JFXTextField pronunciationField;
    @FXML private JFXTextField literalField;
    @FXML private JFXTextField freeField;
    @FXML private JFXTextArea explanationField;

    private boolean updatingUIFlag = false;
    private Example activeExample;

    @FXML
    private void initialize(){
        tab.controller(this);
        searchField.textProperty().addListener(e -> {
            try {
                loadExampleList();
            } catch(SQLException ex){throw new RuntimeException(ex);}
        });

        // == SAVING ==
        textField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!updatingUIFlag && !n && activeExample != null){
                    activeExample = activeExample.updateText(textField.getText());
                    updateUI();
                }
            } catch(SQLException e){throw new RuntimeException(e);}
        });
        glossField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!updatingUIFlag && !n && activeExample != null){
                    activeExample = activeExample.updateGloss(glossField.getText());
                    updateUI();
                }
            } catch(SQLException e){throw new RuntimeException(e);}
        });
        pronunciationField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!updatingUIFlag && !n && activeExample != null){
                    activeExample = activeExample.updatePronunciation(pronunciationField.getText());
                    updateUI();
                }
            } catch(SQLException e){throw new RuntimeException(e);}
        });
        literalField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!updatingUIFlag && !n && activeExample != null){
                    activeExample = activeExample.updateLiteralTranslation(literalField.getText());
                    updateUI();
                }
            } catch(SQLException e){throw new RuntimeException(e);}
        });
        freeField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!updatingUIFlag && !n && activeExample != null){
                    activeExample = activeExample.updateFreeTranslation(freeField.getText());
                    updateUI();
                }
            } catch(SQLException e){throw new RuntimeException(e);}
        });
        explanationField.focusedProperty().addListener((v, o, n) -> {
            try {
                if(!updatingUIFlag && !n && activeExample != null){
                    activeExample = activeExample.updateExplanation(explanationField.getText());
                    updateUI();
                }
            } catch(SQLException e){throw new RuntimeException(e);}
        });
    }

    private void loadExampleList() throws SQLException {
        if(Project.isOpen()){
            ObservableList<Example> examples = Example.search(searchField.getText());
            searchResultsCount.setText(Monoglot.getMonoglot().getLocalString(AppString.SEARCH_RESULT_COUNT, examples.size()));
            searchResultsCount.pseudoClassStateChanged(GREY_ITALICS, examples.isEmpty());
            searchResults.setItems(examples);
        } else searchResults.setItems(FXCollections.emptyObservableList());
    }

    private void updateUI() throws SQLException {
        updatingUIFlag = true;
        {
            exampleIDLabel.setText(Long.toString(activeExample == null?-1:activeExample.ID));
            textField.setText(activeExample == null?"":activeExample.text);
            glossField.setText(activeExample == null?"":activeExample.explanation);
            pronunciationField.setText(activeExample == null?"":activeExample.pronunciation);
            literalField.setText(activeExample == null?"":activeExample.literal_translation);
            freeField.setText(activeExample == null?"":activeExample.free_translation);
            explanationField.setText(activeExample == null?"":activeExample.explanation);
            boolean disable = activeExample == null;
            exampleSection.setDisable(disable);
            deleteButton.setDisable(disable);
            loadExampleList();
        }
        updatingUIFlag = false;
    }

    private void switchActiveExample(Example newExample) throws SQLException {
        if(save()){
            activeExample = newExample;
            updateUI();
            Log.fine(LogString.EXAMPLE_CREATED);
        }
    }

    @FXML
    private void create(ActionEvent event) throws SQLException {
        switchActiveExample(Example.create());
        Log.fine(LogString.EXAMPLE_DELETED);
    }

    @FXML
    private void delete(ActionEvent event) throws SQLException {
        if(activeExample != null){
            Example.delete(activeExample);
            activeExample = null;
            updateUI();
        }
    }

    @FXML
    private void search(MouseEvent mouseEvent) throws SQLException {
        switchActiveExample(searchResults.getSelectionModel().getSelectedItem());
    }

    @Override
    public boolean onLoad(){
        try {
            if (activeExample != null)
                activeExample = Example.fetch(activeExample.ID);
            updateUI();
            return true;
        } catch(SQLException e){
            Log.warning(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean save(){
        try {
            if(activeExample != null)
                activeExample.updateAll(textField.getText(), glossField.getText(), pronunciationField.getText(), literalField.getText(), freeField.getText(), explanationField.getText());
            return true;
        } catch(SQLException e){return false;}
    }

    @Override
    public void onProjectClosed(){
        activeExample = null;
        try {
            updateUI();
        } catch (SQLException e){throw new RuntimeException(e);}
    }
}
