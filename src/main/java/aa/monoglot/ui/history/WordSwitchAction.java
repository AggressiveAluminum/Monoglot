package aa.monoglot.ui.history;

import aa.monoglot.misc.keys.LogString;
import aa.monoglot.project.db.Headword;
import aa.monoglot.ui.controller.LexiconTabController;
import aa.monoglot.util.Log;

import java.io.IOException;
import java.sql.SQLException;

public class WordSwitchAction implements HistoryAction{
    private final LexiconTabController lexiconTabController;
    private final Headword current, future;
    WordSwitchAction(LexiconTabController lexiconTabController, Headword currentWord, Headword newWord) {
        this.lexiconTabController = lexiconTabController;
        current = currentWord;
        future = newWord;
    }

    @Override
    public boolean doAction() {
        try {
            Headword updated = Headword.fetch(future.ID);
            if (!lexiconTabController.switchActiveWord(updated))
                Log.warning(LogString.LEXICON_SWITCH_FAILED);
        } catch (SQLException | IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean undoAction() {
        try {
            Headword updated = Headword.fetch(current.ID);
            if (!lexiconTabController.switchActiveWord(updated))
                Log.warning(LogString.LEXICON_SWITCH_FAILED);
        } catch (SQLException | IOException e){
            return false;
        }
        return true;
    }
}
