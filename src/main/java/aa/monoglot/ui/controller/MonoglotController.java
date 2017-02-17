package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.ui.dialog.AboutDialog;
import aa.monoglot.ui.history.History;
import aa.monoglot.ui.history.TabSwitchActionFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class MonoglotController {
    @FXML
    private ResourceBundle resources;

    // === HISTORY ===
    private History history = new History();
    private TabSwitchActionFactory tabSwitchActionFactory;

    // === ELEMENTS ===
    public MenuBar menuBar;

    public TabPane tabs;
    public BorderPane navigationBar;
    public AnchorPane statusBar;
    public AnchorPane rootPane;

    public Button historyBackButton, historyForeButton;

    public ComboBox tabSelector;
    private int selected = 1; // Lexicon tab is #1, Project Settings is #0.

    // === TABS ===
    public Tab lexiconTab;
    public LexiconController lexiconTabController;

    public Tab projectTab;
    public SettingsController projectTabController;

    // === INIT ===
    @FXML
    private void initialize(){
        lexiconTabController.registerMaster(this);
        projectTabController.registerMaster(this);

        for(Tab t: tabs.getTabs())
            tabSelector.getItems().add(t.getText());
        tabSelector.getSelectionModel().select(selected);
        tabs.getSelectionModel().select(selected);

        tabSwitchActionFactory = new TabSwitchActionFactory(tabSelector, tabs);

        historyBackButton.disableProperty().bind(history.hasNoHistoryProperty());
        historyForeButton.disableProperty().bind(history.hasNoFutureProperty());

        Platform.runLater(this::postInit);
    }

    private void postInit(){
        rootPane.setTopAnchor(navigationBar, menuBar.getHeight());
        rootPane.setTopAnchor(tabs, menuBar.getHeight() + navigationBar.getHeight() - 6);
        rootPane.setBottomAnchor(tabs, statusBar.getHeight());
    }

    public void changeActiveTab(ActionEvent event) {
        int from = selected;
        selected = tabSelector.getSelectionModel().getSelectedIndex();

        if(from == selected)
            return;
        if(history.matchFTS(selected, from) || history.matchPTS(from, selected))
            return;

        history.addAndDo(tabSwitchActionFactory.getTabSwitchAction(from, selected));
    }

    @FXML
    private void historyForward(){
        history.forward();
    }

    @FXML
    private void historyBack(){
        history.back();
    }

    public void quitApplication(ActionEvent event) {
        Platform.exit();
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
}
