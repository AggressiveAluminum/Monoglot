package aa.monoglot.ui.history;

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

    public void doAction(){
        tabSelector.getSelectionModel().select(to);
        tabs.getSelectionModel().select(to);
    }

    public void undoAction(){
        tabSelector.getSelectionModel().select(from);
        tabs.getSelectionModel().select(from);
    }
}
