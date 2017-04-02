package aa.monoglot.ui.component;

import aa.monoglot.project.db.Definition;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class DefinitionCellFactory implements Callback<ListView<Definition>, ListCell<Definition>> {
    @Override
    public ListCell<Definition> call(ListView<Definition> param) {
        return new DefinitionCell();
    }
}
