package aa.monoglot.ui.component;

import aa.monoglot.project.db.Definition;
import aa.monoglot.ui.controller.LexiconTabController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.SQLException;

public class DefinitionCell extends BorderPane {
    private Definition definition;

    @FXML private Button upButton;
    @FXML private Label orderNumber;
    @FXML private Button downButton;
    @FXML private Button deleteButton;
    @FXML private TextArea textArea;

    public DefinitionCell(Definition definition, LexiconTabController.DefinitionHandlers handlers) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/component/DefinitionCell.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        update(definition);
        if(handlers != null) {
            upButton.setOnAction(handlers::up);
            downButton.setOnAction(handlers::down);
            deleteButton.setOnAction(handlers::delete);
        }
        textArea.focusedProperty().addListener((v, o, n)->{
            if(!n) {
                boolean last = downButton.isDisabled();
                try {
                    update(definition.update(textArea.getText()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    downButton.setDisable(last);
                }
            }
        });
    }

    private static void setSizes(Control node){
        node.setMinHeight(42);
        node.setMaxHeight(42);
        node.setMinWidth(42);
        node.setMaxWidth(42);
    }

    public Definition getDefinition() {
        return definition;
    }

    public void update(Definition newDefinition){
        this.definition = newDefinition;
        this.textArea.setText(definition.text);
        this.orderNumber.setText(Integer.toString(definition.order));
        upButton.setDisable(definition.order == 1);
        downButton.setDisable(false);
    }

    public void setIsLast(boolean isLast){
        downButton.setDisable(isLast);
    }
}
