package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.Project;
import aa.monoglot.io.IO;
import aa.monoglot.ui.dialog.AboutDialog;
import aa.monoglot.ui.dialog.ConfirmOverwriteAlert;
import aa.monoglot.ui.dialog.YesNoCancelAlert;
import aa.monoglot.ui.history.History;
import aa.monoglot.ui.history.TabSwitchActionFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class MonoglotController {
    public List<FileChooser.ExtensionFilter> mgltExtensionFilter;

    @FXML
    public ResourceBundle resources;

    // === HISTORY ===
    private History history = new History();
    private boolean manualSwitchRequested = false;
    private TabSwitchActionFactory tabSwitchActionFactory;

    // === ELEMENTS ===
    public MenuBar menuBar;
    @FXML private MenuItem saveProjectItem;
    @FXML private MenuItem saveProjectAsItem;
    @FXML private MenuItem closeProjectItem;

    @FXML private Menu editMenu;

    public TabPane tabs;
    public BorderPane navigationBar;
    public AnchorPane statusBar;
    public AnchorPane rootPane;

    public Button historyBackButton, historyForeButton;

    public ComboBox tabSelector;
    private int selected = 1; // Lexicon tab is #1, Project Settings is #0.

    @FXML private Label status;

    // === TABS ===
    public Tab lexiconTab;
    public LexiconController lexiconTabController;

    public Tab projectTab;
    public SettingsController projectTabController;

    public Tab emptyTab;

    // === INIT ===
    @FXML private void initialize(){
        mgltExtensionFilter = Arrays.asList(
                new FileChooser.ExtensionFilter(resources.getString("app.fileTypeDescription"), ".mglt", ".monoglot")
        );

        lexiconTabController.registerMaster(this);
        projectTabController.registerMaster(this);

        for(Tab t: tabs.getTabs())
            tabSelector.getItems().add(t.getText());

        tabSelector.getItems().remove(tabSelector.getItems().size() - 1);
        tabSelector.getSelectionModel().select(selected);
        tabs.getSelectionModel().select(emptyTab);

        tabSwitchActionFactory = new TabSwitchActionFactory(tabSelector, tabs);

        historyBackButton.disableProperty().bind(history.hasNoHistoryProperty());
        historyForeButton.disableProperty().bind(history.hasNoFutureProperty());

        Platform.runLater(this::postInit);
    }

    private void postInit(){
        rootPane.setTopAnchor(navigationBar, menuBar.getHeight());
        rootPane.setTopAnchor(tabs, menuBar.getHeight() + navigationBar.getHeight() - 6);
        rootPane.setBottomAnchor(tabs, statusBar.getHeight());

        setProjectControlsEnabled(false);
    }

    // == TAB SWITCH ==
    @FXML private void changeActiveTabManualRequest(MouseEvent event){
        manualSwitchRequested = true;
    }

    public void changeActiveTab(ActionEvent event) {
        int from = selected;
        selected = tabSelector.getSelectionModel().getSelectedIndex();

        if(from == selected)
            return;
        if(manualSwitchRequested)
            history.addAndDo(tabSwitchActionFactory.getTabSwitchAction(from, selected));
    }

    // == HISTORY ==
    @FXML private void historyForward(){
        manualSwitchRequested = false;
        history.forward();
    }

    @FXML private void historyBack(){
        manualSwitchRequested = false;
        history.back();
    }

    // == MENU ACTIONS ==
    public void quitApplication(Event event) {
        if(closeProjectImpl())
            Platform.exit();
        else event.consume();
    }

    @FXML private void closeProject(ActionEvent e){
        checkCloseCurrentProject();
    }

    private boolean closeProjectImpl() {
        if(Monoglot.getMonoglot().getProject() != null && Monoglot.getMonoglot().getProject().hasUnsavedChanges()) {
            Optional<ButtonType> result = new YesNoCancelAlert(
                    Monoglot.getMonoglot().window,
                    resources.getString("dialog.saveOnExit.text")
                ).showAndWait();
            if(result.isPresent()){
                if(result.get() == ButtonType.YES)
                    saveProject(null);
                else if(result.get() == ButtonType.CANCEL)
                    return false;
            }
        }
        Monoglot.getMonoglot().closeProject();
        return true;
    }

    public void openSettingsDialog(ActionEvent event) {
        //TODO
    }

    public void openAboutDialog(ActionEvent event) {
        try {
            AboutDialog dialog = new AboutDialog(resources);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(rootPane.getScene().getWindow());
            dialog.showAndWait();
        } catch(Exception e){
            Monoglot.getMonoglot().showError(e);
        }
    }

    public void setProjectControlsEnabled(boolean onOff) {
        history.clear();
        manualSwitchRequested = false;

        tabSelector.setDisable(!onOff);
        editMenu.setDisable(!onOff);
        saveProjectItem.setDisable(!onOff);
        saveProjectAsItem.setDisable(!onOff);
        closeProjectItem.setDisable(!onOff);

        if(!onOff) {
            lexiconTabController.clearInfo();
            projectTabController.clearInfo();
            tabs.getSelectionModel().select(emptyTab);
        } else {
            tabs.getSelectionModel().select(selected = 1);
            tabSelector.getSelectionModel().select(selected);
        }
    }

    public void newProject(ActionEvent event) {
        if(checkCloseCurrentProject())
            IO.newProject(this);
    }

    public void openProject(ActionEvent event) {
        if(checkCloseCurrentProject())
            IO.openProject(this);
    }

    public void recoverWorkingDirectory(ActionEvent event) {
        if(checkCloseCurrentProject())
            IO.recoverWorkingDirectory(this);
    }

    public void saveProject(ActionEvent event) {
        if(checkCloseCurrentProject())
            IO.saveProject(this);
    }

    public void saveProjectAs(ActionEvent event) {
        if(checkCloseCurrentProject())
            IO.saveProjectAs(this);
    }

    private boolean checkCloseCurrentProject(){
        if(Monoglot.getMonoglot().getProject() != null) {
            if(closeProjectImpl())
                setProjectControlsEnabled(false);
            else return false;
        }
        return true;
    }

    public void setLocalStatus(String key, Object... args){
        status.setText(String.format(key, args));
    }
}
