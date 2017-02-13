package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.ui.dialog.AboutDialog;
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

    public MenuBar menuBar;

    public TabPane tabs;
    public BorderPane navigationBar;
    public AnchorPane statusBar;
    public AnchorPane rootPane;

    @FXML
    Label counter;
    int count = 0;

    public ComboBox tabSelector;

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
        tabSelector.getSelectionModel().select(0);

        Platform.runLater(()->{
            rootPane.setTopAnchor(navigationBar, menuBar.getHeight());
            rootPane.setTopAnchor(tabs, menuBar.getHeight() + navigationBar.getHeight() - 6);
            rootPane.setBottomAnchor(tabs, statusBar.getHeight());
        });
    }

    public void changeActiveTab(ActionEvent event) {
        tabs.getSelectionModel().select(tabSelector.getSelectionModel().getSelectedIndex());
    }

    public void quitApplication(ActionEvent event) {
        Platform.exit();
    }

    public void openSettingsDialog(ActionEvent event) {
    }

    public void openAboutDialog(ActionEvent event) {
        try {
            System.out.println("Hello, suckers!");
            AboutDialog dialog = new AboutDialog(resources);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(rootPane.getScene().getWindow());
            //dialog.showAndWait();
            throw new Exception();
        } catch(Exception e){
            Monoglot.getMonoglot().showError(e);
        }
    }
}
