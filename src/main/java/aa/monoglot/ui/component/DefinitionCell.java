package aa.monoglot.ui.component;

import aa.monoglot.project.db.Definition;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class DefinitionCell extends BorderPane {
    private final Definition definition;
    public DefinitionCell(Definition definition){
        this.definition = definition;
        this.setLeft(new Label(Integer.toString(definition.order)));
        this.setCenter(new TextArea(definition.text));
    }
}
