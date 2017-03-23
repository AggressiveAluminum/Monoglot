package aa.monoglot.ui.history;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;

import java.util.ArrayDeque;

public class History {
    public static final int LEXICON_TAB_INDEX = 1;

    private static final int MAX_HISTORY_SIZE = 32;
    private final ArrayDeque<HistoryAction> history = new ArrayDeque<>();
    private final ArrayDeque<HistoryAction> future = new ArrayDeque<>();
    private final BooleanProperty hasNoHistory = new SimpleBooleanProperty(true);
    private final BooleanProperty hasNoFuture = new SimpleBooleanProperty(true);

    private final ComboBox<String> tabSelector;
    private final TabPane tabs;
    private boolean executingHistoryAction = false;

    public History(ComboBox<String> tabSelector, TabPane tabs){
        this.tabSelector = tabSelector;
        this.tabSelector.setOnAction(this::tabSwitchHandler);
        this.tabs = tabs;
        reset();
    }
    /**
     * Returns if there are any actions in the future list.
     */
    public boolean hasFuture(){
        return !hasNoFuture.get();
    }
    /**
     * Returns if there are any actions in the history list.
     */
    public boolean hasHistory(){
        return !hasNoHistory.get();
    }

    public BooleanProperty hasNoHistoryProperty() {
        return hasNoHistory;
    }

    public BooleanProperty hasNoFutureProperty(){
        return hasNoFuture;
    }

    /**
     * Adds an action to the history and performs it.
     */
    public boolean addAndDo(HistoryAction action){
        executingHistoryAction = true;
        if(action.doAction()){
            history.push(action);
            if(history.size() > MAX_HISTORY_SIZE)
                history.removeLast();
            future.clear();
            hasNoHistory.set(false);
            hasNoFuture.set(true);
            return true;
        }
        return false;
    }

    public void reset() {
        history.clear();
        future.clear();
        hasNoHistory.set(true);
        hasNoFuture.set(true);
    }

    public void goToTab(int index) {
        int sel = tabs.getSelectionModel().getSelectedIndex();
        if(index != sel)
            addAndDo(new TabSwitchAction(tabSelector, tabs, sel, index));
    }

    public void forward() {
        if(future.isEmpty())
            return;
        executingHistoryAction = true;
        if(future.peek().doAction()){
            history.push(future.pop());
            hasNoHistory.set(false);
            hasNoFuture.set(future.isEmpty());
        }
    }

    public void back() {
        if(history.isEmpty())
            return;
        executingHistoryAction = true;
        if(history.peek().undoAction()) {
            future.push(history.pop());
            hasNoHistory.set(history.isEmpty());
            hasNoFuture.set(false);
        }
    }

    public void tabSwitchHandler(ActionEvent event) {
        if(!executingHistoryAction) {
            int from = tabs.getSelectionModel().getSelectedIndex();
            int to = tabSelector.getSelectionModel().getSelectedIndex();
            if (from != to)
                addAndDo(new TabSwitchAction(tabSelector, tabs, from, to));
        } else executingHistoryAction = false;
    }
}

