package aa.monoglot.ui.history;

import aa.monoglot.Monoglot;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;

/**
 * Tracks tab switches for {@link aa.monoglot.ui.history.History History}
 */
class TabSwitchAction implements HistoryAction {
    private final ComboBox tabSelector;
    private final TabPane tabs;
    private final int from, to;

    TabSwitchAction(ComboBox tabSelector, TabPane tabs, int from, int to) {
        this.tabSelector = tabSelector;
        this.tabs = tabs;
        this.from = from;
        this.to = to;
    }

    public boolean doAction(){
        if(Monoglot.getMonoglot().mainController.switchContext(tabs.getTabs().get(from), tabs.getTabs().get(to))){
            tabSelector.getSelectionModel().select(to);
            tabs.getSelectionModel().select(to);
            return true;
        }
        return false;
    }

    public boolean undoAction(){
        if(Monoglot.getMonoglot().mainController.switchContext(tabs.getTabs().get(to), tabs.getTabs().get(from))) {
            tabSelector.getSelectionModel().select(from);
            tabs.getSelectionModel().select(from);
            return true;
        }
        return false;
    }
}
