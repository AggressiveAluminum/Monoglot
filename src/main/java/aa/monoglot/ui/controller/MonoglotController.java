package aa.monoglot.ui.controller;

import aa.monoglot.util.ApplicationErrorCode;
import aa.monoglot.Monoglot;
import aa.monoglot.project.Project;
import aa.monoglot.ui.dialog.AboutDialog;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    public ComboBox<String> tabSelector;
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
                new FileChooser.ExtensionFilter(resources.getString("app.fileTypeDescription"), "*.mglt", "*.monoglot"),
                new FileChooser.ExtensionFilter(resources.getString("app.allFilesTypeDescription"), "*")
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
        AnchorPane.setTopAnchor(navigationBar, menuBar.getHeight());
        AnchorPane.setTopAnchor(tabs, menuBar.getHeight() + navigationBar.getHeight() - 6);
        AnchorPane.setBottomAnchor(tabs, statusBar.getHeight());

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
        if(checkCloseProject())
            Platform.exit();
        else event.consume();
    }

    @FXML private void closeProject(ActionEvent e){
        checkCloseProject();
    }

    private boolean checkCloseProject() {
        if(Project.isProjectOpen()){
            if(Project.getProject().hasUnsavedChanges()){
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
            Project.getProject().close();
        }
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
            Monoglot.getMonoglot().showError(e, ApplicationErrorCode.RECOVERABLE_ERROR);
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
        if(checkCloseProject())
            ETC.newProject(this);
    }

    public void openProject(ActionEvent event) {
        if(checkCloseProject())
            ETC.openProject(this);
    }

    public void recoverWorkingDirectory(ActionEvent event) {
        if(checkCloseProject())
            ETC.recoverWorkingDirectory(this);
    }

    public void saveProject(ActionEvent event) {
        ETC.saveProject(this);
    }

    public void saveProjectAs(ActionEvent event) {
        ETC.saveProjectAs(this);
    }

    public void setLocalStatus(String key, Object... args){
        status.setText(String.format(resources.getString(key), args));
    }
}
