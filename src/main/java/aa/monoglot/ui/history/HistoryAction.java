package aa.monoglot.ui.history;

/**
 * @author cofl
 * @date 3/20/2017
 */
interface HistoryAction {
    boolean doAction();
    boolean undoAction();
}
