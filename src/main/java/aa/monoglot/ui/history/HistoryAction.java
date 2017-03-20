package aa.monoglot.ui.history;

interface HistoryAction {
    boolean doAction();
    boolean undoAction();
}
