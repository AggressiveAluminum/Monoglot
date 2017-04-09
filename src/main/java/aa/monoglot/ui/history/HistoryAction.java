package aa.monoglot.ui.history;

interface HistoryAction {
    boolean doAction(int currentTab);
    boolean undoAction(int currentTab);
}
