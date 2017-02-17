package aa.monoglot.ui.history;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Stack;

/**
 * Tracks UI navigation history.
 *
 * @author cofl
 * @date 2/15/2017
 */
public class History {
    private final Stack<HistoryAction> history = new Stack<>();
    private final Stack<HistoryAction> future = new Stack<>();
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
    public void addAndDo(HistoryAction action){
        history.push(action);
        future.clear(); // wipe out forward history

        hasNoHistory.set(false);
        hasNoFuture.set(true);

        action.doAction();
    }

    /**
     * Goes back in the history.
     */
    public void back(){
        if(history.isEmpty())
            return;
        HistoryAction action = history.pop();
        future.push(action);

        hasNoHistory.set(history.isEmpty());
        hasNoFuture.set(false);

        action.undoAction();
    }

    /**
     * Goes forward in the history.
     */
    public void forward(){
        if(future.isEmpty())
            return;
        HistoryAction action = future.pop();
        history.push(action);

        hasNoHistory.set(false);
        hasNoFuture.set(future.isEmpty());

        action.doAction();
    }

    /**
     * Matches a tab switch in the Future.
     */
    public boolean matchFTS(int to, int from) {
        if(!hasFuture())
            return false;
        return future.peek().matchTS(to, from);
    }

    /**
     * Matches a tab switch in the History;
     */
    public boolean matchPTS(int to, int from) {
        if(!hasHistory())
            return false;
        return history.peek().matchTS(to,from);
    }
}
