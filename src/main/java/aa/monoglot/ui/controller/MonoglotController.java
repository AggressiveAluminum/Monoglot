package aa.monoglot.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.ResourceBundle;

/**
 * @author cofl
 * @date 2/8/2017
 */
public class MonoglotController {
    @FXML
    private ResourceBundle resources;

    public HBox navigationBar;
    public TabPane tabs;
    public AnchorPane statusBar, rootPane;

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
        rootPane.setTopAnchor(tabs, 28.0);
        rootPane.setBottomAnchor(tabs, statusBar.getHeight());
    }
}
