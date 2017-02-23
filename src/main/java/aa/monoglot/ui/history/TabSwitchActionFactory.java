package aa.monoglot.ui.history;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;

/**
 * Convenience class for creating {@link aa.monoglot.ui.history.TabSwitchAction TabSwitchActions}.
 *
 * @author cofl
 * @date 2/17/2017
 */
public class TabSwitchActionFactory {
    private final ComboBox tabSelector;
    private final TabPane tabs;

    public TabSwitchActionFactory(ComboBox tabSelector, TabPane tabs){
        this.tabSelector = tabSelector;
        this.tabs = tabs;
    }

    public TabSwitchAction getTabSwitchAction(int from, int to){
        return new TabSwitchAction(tabSelector, tabs, from, to);
    }
}
