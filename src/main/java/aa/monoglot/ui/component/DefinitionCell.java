package aa.monoglot.ui.component;

import aa.monoglot.project.db.Definition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

class DefinitionCell extends ListCell<Definition> {
    DefinitionCell(){
        setSkin(new DefinitionCellSkin(this, null));
    }
    @Override
    protected void updateItem(Definition definition, boolean empty){
        ((DefinitionCellSkin)this.getSkin()).update(definition);
    }
}

class DefinitionCellSkin implements Skin<DefinitionCell> {
    private final DefinitionCell cell;
    private Definition definition;
    private BorderPane pane = new BorderPane();
    DefinitionCellSkin(DefinitionCell cell, Definition definition){
        this.cell = cell;
        update(definition);
    }

    @Override
    public DefinitionCell getSkinnable() {
        return cell;
    }

    @Override
    public Node getNode() {
        if(definition != null)
            return pane;
        return new Label();
    }

    public void update(Definition definition){
        this.definition = definition;
        pane.setCenter(new TextArea(definition.text));
        pane.setLeft(new Label(Integer.toString(definition.order)));
    }

    @Override
    public void dispose() {

    }
}