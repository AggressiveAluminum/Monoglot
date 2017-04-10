package aa.monoglot.ui.controller;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;
import aa.monoglot.misc.keys.AppWarning;
import aa.monoglot.project.Project;
import aa.monoglot.ui.ControlledTab;
import aa.monoglot.ui.dialog.Dialogs;
import aa.monoglot.ui.dialog.HelpWindow;
import aa.monoglot.ui.history.History;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class MonoglotController implements GeneralController {
    @FXML private ResourceBundle resources;
    @FXML private MenuBar menuBar;
    @FXML private MenuItem saveProjectItem;
    @FXML private MenuItem saveProjectAsItem;
    @FXML private MenuItem closeProjectItem;
    @FXML private Menu editMenu;

    @FXML private ComboBox<String> tabSelector;
    @FXML private TabPane tabs;

    @FXML private BorderPane navigationBar;
    @FXML private AnchorPane statusBar;

    @FXML private Button historyForeButton;
    @FXML private Button historyBackButton;
    @FXML private Label status;
    private int numTabs;

    @FXML private void initialize(){
        numTabs = tabs.getTabs().size();
        for(Tab t: tabs.getTabs())
            tabSelector.getItems().add(t.getText());
        tabSelector.getItems().remove(numTabs - 1);
        ((EmptyTabController) ((ControlledTab) tabs.getTabs().get(tabs.getTabs().size() - 1)).controller()).setMaster(this);

        History.init(tabSelector, tabs);
        historyBackButton.disableProperty().bind(History.getInstance().hasNoHistoryProperty());
        historyForeButton.disableProperty().bind(History.getInstance().hasNoFutureProperty());
        History.getInstance().silentGoTo(History.LEXICON_TAB_INDEX, numTabs - 1);
    }

    public void setProjectControlsDisabled(boolean disabled){
        History.getInstance().reset();

        tabSelector.setDisable(disabled);
        editMenu.setDisable(disabled);
        saveProjectItem.setDisable(disabled);
        saveProjectAsItem.setDisable(disabled);
        closeProjectItem.setDisable(disabled);

        if(!disabled){
            History.getInstance().silentGoTo(History.LEXICON_TAB_INDEX, History.LEXICON_TAB_INDEX);
            tabs.getSelectionModel().select(History.LEXICON_TAB_INDEX);
            tabSelector.getSelectionModel().select(History.LEXICON_TAB_INDEX);
            ((ControlledTab) tabs.getSelectionModel().getSelectedItem()).controller().onLoad();
        } else {
            for(Tab t: tabs.getTabs())
                ((ControlledTab) t).controller().onUnload();
            History.getInstance().silentGoTo(History.LEXICON_TAB_INDEX, numTabs - 1);
        }
    }

    private ControlledTab getCurrentTab(){
        return (ControlledTab) tabs.getSelectionModel().getSelectedItem();
    }

    public void quitApplication(ActionEvent event) {
        try {
            if (noOpenProject())
                Platform.exit();
            else event.consume();
        } catch(SQLException e){
            //TODO: DO SOMETHING WITH THIS, PROPERLY.
        }
    }

    public void wQuitApplication(WindowEvent event){
        try {
            if(noOpenProject())
                Platform.exit();
            else event.consume();
        } catch (SQLException e){
            //TODO: DO SOMETHING WITH THIS, PROPERLY.
        }
    }

    // == HISTORY ==
    @FXML private void historyForward(){
        History.getInstance().forward();
    }
    @FXML private void historyBack(){
        History.getInstance().back();
    }

    // == PROJECT CONTROLS ==
    private boolean noOpenProject() throws SQLException {
        if(Project.isOpen()){
            for(Tab t: tabs.getTabs())
                ((ControlledTab) t).controller().save();
            if(Project.getProject().isSaveNeeded()){
                Optional<ButtonType> result = Dialogs.yesNoCancel(AppString.CHECK_SAVE);
                if(result.isPresent()){
                    if(result.get() == ButtonType.YES)
                        commonSave(!Project.getProject().hasSavePath());
                    else if(result.get() == ButtonType.CANCEL)
                        return false;
                }
            }
            for(Tab t: tabs.getTabs())
                ((ControlledTab) t).controller().onUnload();
            Project.getProject().close();
        }
        return true;
    }

    @FXML
    void newProject(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        if(noOpenProject()){
            Project.newProject();
            setProjectControlsDisabled(false);
        }
    }

    @FXML
    void openProject(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        if(noOpenProject()){
            File file = Dialogs.getChooser().showOpenDialog(Monoglot.getMonoglot().getWindow());
            if(file != null) {
                Project.openProject(file.toPath());
                setProjectControlsDisabled(false);
            }
        }
    }

    @FXML private void recoverProject(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        if(noOpenProject()){
            File file = new DirectoryChooser().showDialog(Monoglot.getMonoglot().getWindow());
            if(file != null){
                Project.openProject(file.toPath());
                setProjectControlsDisabled(false);
            }
        }
    }

    @FXML private void saveProject(ActionEvent event) throws SQLException {
        commonSave(!Project.getProject().hasSavePath());
    }

    @FXML private void saveProjectAs(ActionEvent event) throws SQLException {
        commonSave(true);
    }

    private void commonSave(boolean getNewFile) throws SQLException {
        if(Project.isOpen()){
            if(getNewFile){
                File file = Dialogs.getChooser().showSaveDialog(Monoglot.getMonoglot().getWindow());
                if(file != null)
                    Project.getProject().setSavePath(file.toPath());
                else return;
            }
            for(Tab t: tabs.getTabs())
                ((ControlledTab) t).controller().save();
            Project.getProject().save();
        }
    }

    @FXML private void closeProject(ActionEvent event) throws SQLException {
        for(Tab t: tabs.getTabs())
            ((ControlledTab) t).controller().save();
        if(noOpenProject())
            setProjectControlsDisabled(true);
    }

    // == EDIT CONTROLS ==
    @FXML private void createNewWord(ActionEvent event) throws SQLException, IOException {
        if(Project.isOpen()){
            History.getInstance().goToTab(History.LEXICON_TAB_INDEX);
            ((LexiconTabController) getCurrentTab().controller()).createNewWord(event);
        }
    }

    // == OTHER ACTIONS ==
    @FXML private void openAboutDialog(ActionEvent event){

    }

    @FXML private void showHelp(ActionEvent event){
        new HelpWindow().show();
    }

    public void start(Application.Parameters parameters) {
        if(!parameters.getRaw().isEmpty()){
            try {
                Project.openProject(Paths.get(parameters.getRaw().get(0)));
                setProjectControlsDisabled(false);
            } catch (SQLException | IOException | InvalidPathException | ClassNotFoundException e){
                Dialogs.warning(Monoglot.getMonoglot().getWindow(), Monoglot.getMonoglot().getLocalString(AppWarning.NO_SUCH_PROJECT_TITLE),
                        Monoglot.getMonoglot().getLocalString(AppWarning.NO_SUCH_PROJECT_HEADER),
                        Monoglot.getMonoglot().getLocalString(AppWarning.NO_SUCH_PROJECT_TEXT)).show();
            }
        }
    }
}
