package aa.monoglot.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class MonoglotController {
    @FXML
    private ResourceBundle resources;

    public TabPane tabs;
    public BorderPane navigationBar;
    public AnchorPane statusBar;
    public AnchorPane rootPane;

    @FXML
    Label counter;
    int count = 0;

    // === TABS ===
    public Tab lexiconTab;
    public LexiconController lexiconTabController;

    // === INIT ===
    @FXML
    private void initialize(){
        lexiconTabController.registerMaster(this);
        navigationBar.layout();
        Platform.runLater(()->rootPane.setTopAnchor(tabs, navigationBar.getHeight() - 6));
        rootPane.setBottomAnchor(tabs, statusBar.getHeight());
    }
}
