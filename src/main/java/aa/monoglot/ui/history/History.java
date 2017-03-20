package aa.monoglot.ui.history;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayDeque;

/**
 * Tracks UI navigation history.
 */
public class History {
    private static final int MAX_HISTORY = 32;
    private final ArrayDeque<HistoryAction> history = new ArrayDeque<>();
    private final ArrayDeque<HistoryAction> future = new ArrayDeque<>();
    private final BooleanProperty hasNoHistory = new SimpleBooleanProperty(true);
    private final BooleanProperty hasNoFuture = new SimpleBooleanProperty(true);

    public BooleanProperty hasNoHistoryProperty() {
        return hasNoHistory;
    }

    public BooleanProperty hasNoFutureProperty() {
        return hasNoFuture;
    }

    /**
     * Returns if there are any actions in the history list.
     */
    public boolean hasHistory(){
        return !hasNoHistory.get();
    }

    /**
     * Returns if there are any actions in the future list.
     */
    public boolean hasFuture(){
        return !hasNoFuture.get();
    }

    /**
     * Adds an action to the history and performs it.
     */
    public boolean addAndDo(HistoryAction action){
        if(action.doAction()) {
            history.push(action);
            if (history.size() > MAX_HISTORY)
                history.removeLast();
            future.clear(); // wipe out forward history

            hasNoHistory.set(false);
            hasNoFuture.set(true);
            return true;
        }
        return false;
    }

    /**
     * Goes back in the history.
     */
    public void back(){
        if(history.isEmpty())
            return;
        if(history.peek().undoAction()) {
            HistoryAction action = history.pop();
            future.push(action);
            // don't need to check max, history + future will never be > MAX_HISTORY,

            hasNoHistory.set(history.isEmpty());
            hasNoFuture.set(false);
        }
    }

    /**
     * Goes forward in the history.
     */
    public void forward(){
        if(future.isEmpty())
            return;
        if(future.peek().doAction()) {
            HistoryAction action = future.pop();
            history.push(action);
            // don't need to check max, history + future will never be > MAX_HISTORY,

            hasNoHistory.set(false);
            hasNoFuture.set(future.isEmpty());
        }
    }

    public void clear() {
        history.clear();
        future.clear();

        hasNoHistory.set(true);
        hasNoFuture.set(true);
    }
}
