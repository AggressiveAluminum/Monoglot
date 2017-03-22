package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.project.Project;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.ui.dialog.Dialogs;
import aa.monoglot.ui.history.History;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class MonoglotController {
    @FXML private ResourceBundle resources;

    @FXML private MenuBar menuBar;
    @FXML private MenuItem saveProjectItem;
    @FXML private MenuItem saveProjectAsItem;
    @FXML private MenuItem closeProjectItem;
    @FXML private Menu editMenu;

    @FXML private ComboBox<String> tabSelector;
    @FXML private TabPane tabs;

    @FXML private BorderPane navigationBar;
    @FXML private AnchorPane statusBar;

    private History history;

    @FXML private void init(){
        history = new History(tabSelector, tabs);

        for(Tab t: tabs.getTabs())
            tabSelector.getItems().add(t.getText());
        tabSelector.getItems().remove(tabSelector.getItems().size() - 1);
        tabSelector.getSelectionModel().select(History.LEXICON_TAB_INDEX);
        tabs.getSelectionModel().select(tabs.getTabs().size() - 1);

        Platform.runLater(() -> {
            AnchorPane.setTopAnchor(navigationBar, menuBar.getHeight());
            AnchorPane.setTopAnchor(tabs, menuBar.getHeight() + navigationBar.getHeight() - 6);
            AnchorPane.setBottomAnchor(tabs, statusBar.getHeight());
        });
    }

    private void setProjectControlsDisabled(boolean disabled){
        history.reset();

        tabSelector.setDisable(disabled);
        editMenu.setDisable(disabled);
        saveProjectItem.setDisable(disabled);
        saveProjectAsItem.setDisable(disabled);
        closeProjectItem.setDisable(disabled);

        if(disabled){
            for(Tab t: tabs.getTabs())
                ((ControlledTab) t).getController().save();
        } else {
            tabs.getSelectionModel().select(History.LEXICON_TAB_INDEX);
            tabSelector.getSelectionModel().select(History.LEXICON_TAB_INDEX);
        }
    }

    private ControlledTab getCurrentTab(){
        return (ControlledTab) tabs.getSelectionModel().getSelectedItem();
    }

    public void quitApplication(WindowEvent event) {
        try {
            if (noOpenProject())
                Platform.exit();
            else event.consume();
        } catch(SQLException e){
            //TODO: DO SOMETHING WITH THIS, PROPERLY.
        }
    }

    // == HISTORY ==
    @FXML private void historyForward(){
        history.forward();
    }
    @FXML private void historyBack(){
        history.back();
    }

    // == PROJECT CONTROLS ==
    private boolean noOpenProject() throws SQLException {
        if(Project.isOpen()){
            if(Project.getProject().hasSavePath())
                Project.getProject().save();
            else {
                Optional<ButtonType> result = Dialogs.yesNoCancel(AppString.CHECK_SAVE);
                if(result.isPresent()){
                    if(result.get() == ButtonType.YES)
                        commonSave(!Project.getProject().hasSavePath());
                    else if(result.get() == ButtonType.CANCEL)
                        return false;
                }
            }
            Project.getProject().close();
            for(Tab t: tabs.getTabs())
                ((ControlledTab) t).getController().onUnload();
        }
        return true;
    }

    @FXML private void newProject(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        if(noOpenProject()){
            Project.newProject();
            setProjectControlsDisabled(false);
        }
    }

    @FXML private void openProject(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        if(noOpenProject()){
            File file = Dialogs.getChooser().showOpenDialog(Monoglot.getMonoglot().getWindow());
            if(file != null) {
                Project.openProject(file.toPath());
                setProjectControlsDisabled(false);
            }
        }
    }

    @FXML private void recoverProject(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        if(noOpenProject()){
            File file = new DirectoryChooser().showDialog(Monoglot.getMonoglot().getWindow());
            if(file != null){
                Project.openProject(file.toPath());
                setProjectControlsDisabled(false);
            }
        }
    }

    @FXML private void saveProject(ActionEvent event) throws SQLException {
        commonSave(!Project.getProject().hasSavePath());
    }

    @FXML private void saveProjectAs(ActionEvent event) throws SQLException {
        commonSave(true);
    }

    private void commonSave(boolean getNewFile) throws SQLException {
        if(Project.isOpen()){
            if(getNewFile){
                File file = Dialogs.getChooser().showSaveDialog(Monoglot.getMonoglot().getWindow());
                if(file != null)
                    Project.getProject().setSavePath(file.toPath());
                else return;
            }
            for(Tab t: tabs.getTabs())
                ((ControlledTab) t).getController().save();
            Project.getProject().save();
        }
    }

    @FXML private void closeProject(ActionEvent event) throws SQLException {
        noOpenProject();
        setProjectControlsDisabled(true);
    }

    // == EDIT CONTROLS ==
    @FXML private void createNewWord(ActionEvent event) throws SQLException {
        if(Project.isOpen()){
            history.goToTab(History.LEXICON_TAB_INDEX);
            ((LexiconTabController) getCurrentTab().getController()).createNewWord(event);
        }
    }

    // == OTHER ACTIONS ==
    @FXML private void openAboutDialog(ActionEvent event){

    }
}
