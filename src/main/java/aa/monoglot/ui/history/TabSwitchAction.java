package aa.monoglot.ui.history;

import aa.monoglot.ui.ControlledTab;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import old.monoglot.Monoglot;

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

    public boolean doAction(){
        if(Monoglot.getMonoglot().mainController.switchContext(tabs.getTabs().get(from), tabs.getTabs().get(to))){
            tabSelector.getSelectionModel().select(to);
            if(((ControlledTab) tabs.getSelectionModel().getSelectedItem()).getController().onUnload()){
                tabs.getSelectionModel().select(to);
                ((ControlledTab) tabs.getSelectionModel().getSelectedItem()).getController().onLoad();
                return true;
            }
        }
        return false;
    }

    public boolean undoAction(){
        if(Monoglot.getMonoglot().mainController.switchContext(tabs.getTabs().get(to), tabs.getTabs().get(from))) {
            tabSelector.getSelectionModel().select(from);
            if (((ControlledTab) tabs.getSelectionModel().getSelectedItem()).getController().onUnload()) {
                tabs.getSelectionModel().select(from);
                ((ControlledTab) tabs.getSelectionModel().getSelectedItem()).getController().onLoad();
                return true;
            }
        }
        return false;
    }
}
