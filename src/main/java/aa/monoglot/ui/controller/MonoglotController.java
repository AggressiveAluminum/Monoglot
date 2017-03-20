package aa.monoglot.ui.controller;

import aa.monoglot.util.ApplicationErrorCode;
import aa.monoglot.Monoglot;
import aa.monoglot.project.Project;
import aa.monoglot.ui.dialog.AboutDialog;
import aa.monoglot.ui.dialog.YesNoCancelAlert;
import aa.monoglot.ui.history.History;
import aa.monoglot.ui.history.TabSwitchActionFactory;
import aa.monoglot.util.SilentException;
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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MonoglotController {
    static private final int LEXICON_TAB_ORDER  = 1;
    static private final int PROJECT_TAB_ORDER = 0;
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

    public boolean switchContext(Tab from, Tab to){
        if (from == lexiconTab) {
            lexiconTabController.unload();
        } else if (from == projectTab) {
            projectTabController.unload();
        }

        if (to == lexiconTab) {
            lexiconTabController.load();
        } else if (to == projectTab) {
            projectTabController.load();
        }
        return true;
    }

    private boolean checkCloseProject(boolean delete) throws SQLException {
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
            Project.getProject().close(delete);
        }
        return true;
    }

    // == MENU ACTIONS ==
    public void quitApplication(Event event) {
        try {
            if (checkCloseProject(true))
                Platform.exit();
            else event.consume();
        } catch (SQLException e){
            SilentException.rethrow(e);
        }
    }

    @FXML private void closeProject(ActionEvent e) throws SQLException {
        checkCloseProject(true);
    }

    @FXML private void closeProjectNoDeleteItem(ActionEvent event) throws SQLException {
        checkCloseProject(false);
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

    public void newProject(ActionEvent event) throws SQLException {
        if(checkCloseProject(true))
            ETC.newProject(this);
    }

    public void openProject(ActionEvent event) throws SQLException {
        if(checkCloseProject(true))
            ETC.openProject(this);
    }

    public void recoverWorkingDirectory(ActionEvent event) throws SQLException {
        if(checkCloseProject(true))
            ETC.recoverWorkingDirectory(this);
    }

    public void saveProject(ActionEvent event) throws SQLException {
        if(Project.isProjectOpen())
            ETC.saveProject(this);
    }

    public void saveProjectAs(ActionEvent event) {
        if(Project.isProjectOpen())
            ETC.saveProjectAs(this);
    }

    public void setLocalStatus(String key, Object... args){
        status.setText(String.format(resources.getString(key), args));
    }

    /**
     * Accelerator: Shortcut + N
     */
    @FXML
    private void createNewWord(ActionEvent event) throws SQLException {
        if (Project.isProjectOpen()) {// without this check, the app will crash when a project isn't open.
            if(tabSelector.getSelectionModel().getSelectedIndex() == LEXICON_TAB_ORDER)
                lexiconTabController.createNewWord(event);
            else if (history.addAndDo(tabSwitchActionFactory.getTabSwitchAction(tabSelector.getSelectionModel().getSelectedIndex(), LEXICON_TAB_ORDER)))
                lexiconTabController.createNewWord(event);
        }
    }

    void saveAllComponents() throws SQLException {
        lexiconTabController.saveWord();
        //TODO
    }
}
