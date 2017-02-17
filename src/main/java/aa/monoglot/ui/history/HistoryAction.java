package aa.monoglot.ui.history;

/**
 * @author cofl
 * @date 2/17/2017
 */
interface HistoryAction {
    void doAction();
    void undoAction();

    /**
     * Checks if this action is a tab switch from tab[from] to tab[to].
     */
    boolean matchTS(int to, int from);
}
