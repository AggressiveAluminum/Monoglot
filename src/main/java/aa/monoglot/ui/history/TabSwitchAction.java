package aa.monoglot.ui.history;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;

/**
 * Tracks tab switches for {@link aa.monoglot.ui.history.History History}
 *
 * @author cofl
 * @date 2/17/2017
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
        System.err.printf("[DO] Going from tab[%d] to tab[%d]\n", from, to);
        tabSelector.getSelectionModel().select(to);
        tabs.getSelectionModel().select(to);
    }

    public void undoAction(){
        System.err.printf("[UNDO] Going from tab[%d] to tab[%d]\n", to, from);
        tabSelector.getSelectionModel().select(from);
        tabs.getSelectionModel().select(from);
    }

    public boolean matchTS(int to, int from){
        return to == to && from == from;
    }
}
