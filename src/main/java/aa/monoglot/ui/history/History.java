package aa.monoglot.ui.history;

import aa.monoglot.project.db.Headword;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.ui.controller.LexiconTabController;
import aa.monoglot.util.Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;

public class History {
    public static final int LEXICON_TAB_INDEX = 1;
    private static History instance;

    private static final int MAX_HISTORY_SIZE = 32;
    private final ArrayDeque<HistoryAction> history = new ArrayDeque<>();
    private final ArrayDeque<HistoryAction> future = new ArrayDeque<>();
    private final BooleanProperty hasNoHistory = new SimpleBooleanProperty(true);
    private final BooleanProperty hasNoFuture = new SimpleBooleanProperty(true);

    private final ComboBox<String> tabSelector;
    private final TabPane tabs;
    private final LexiconTabController lexiconTabController;
    private boolean executingHistoryAction = false;

    private History(ComboBox<String> tabSelector, TabPane tabs){
        this.tabSelector = tabSelector;
        this.tabSelector.setOnAction(this::tabSwitchHandler);
        this.tabs = tabs;
        this.lexiconTabController = ((LexiconTabController) ((ControlledTab) tabs.getTabs().get(LEXICON_TAB_INDEX)).controller());
        reset();
    }
    public static void init(ComboBox<String> tabSelector, TabPane tabs){
        instance = new History(tabSelector, tabs);
    }
    public static History getInstance(){
        return instance;
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
    private void addAndDo(HistoryAction action){
        executingHistoryAction = true;
        if(action.doAction())
            add(action);
        executingHistoryAction = false;
    }

    /**
     * Just adds an action to the history. Only use if the action is already handled elsewhere.
     */
    public void add(HistoryAction action){
        history.push(action);
        if(history.size() > MAX_HISTORY_SIZE)
            history.removeLast();
        future.clear();
        hasNoHistory.set(false);
        hasNoFuture.set(true);
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

    public void goToWord(Headword newWord){
        if(lexiconTabController.getActiveWord() == null){
            try {
                lexiconTabController.switchActiveWord(newWord);
            } catch(SQLException |IOException e){throw new RuntimeException(e);}
        } else if(lexiconTabController.getActiveWord().ID != newWord.ID)
            addAndDo(new WordSwitchAction(lexiconTabController, lexiconTabController.getActiveWord(), newWord));
    }

    public void silentGoTo(int selector, int tab) {
        executingHistoryAction = true;
        tabSelector.getSelectionModel().select(selector);
        tabs.getSelectionModel().select(tab);
        executingHistoryAction = false;
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
        executingHistoryAction = false;
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
        executingHistoryAction = false;
    }

    private void tabSwitchHandler(@SuppressWarnings("unused") ActionEvent event) {
        Log.entering(this.getClass().getName(), "tabSwitchHandler");
        if(!executingHistoryAction) {
            int from = tabs.getSelectionModel().getSelectedIndex();
            int to = tabSelector.getSelectionModel().getSelectedIndex();
            if (from != to)
                addAndDo(new TabSwitchAction(tabSelector, tabs, from, to));
        } else executingHistoryAction = false;
        Log.exiting(this.getClass().getName(), "tabSwitchHandler");
    }
}

