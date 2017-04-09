package aa.monoglot.ui.history;

import aa.monoglot.ui.ControlledTab;
import aa.monoglot.util.Log;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;

/**
 * Tracks tab switches for {@linkplain aa.monoglot.ui.history.History}
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

    public boolean doAction(int currentTab){
        tabSelector.getSelectionModel().select(to);
        ControlledTab from = (ControlledTab) tabs.getSelectionModel().getSelectedItem();
        if(from.controller().save() && from.controller().onUnload()){
            tabs.getSelectionModel().select(to);
            ((ControlledTab) tabs.getSelectionModel().getSelectedItem()).controller().onLoad();
            return true;
        } else return false;
    }

    public boolean undoAction(int currentTab){
        tabSelector.getSelectionModel().select(from);
        ControlledTab fromTab = (ControlledTab) tabs.getSelectionModel().getSelectedItem();
        if (fromTab.controller().save() && fromTab.controller().onUnload()) {
            tabs.getSelectionModel().select(from);
            ((ControlledTab) tabs.getSelectionModel().getSelectedItem()).controller().onLoad();
            return true;
        } else return false;
    }
}
