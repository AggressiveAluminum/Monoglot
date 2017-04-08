package aa.monoglot.ui.dialog;

import aa.monoglot.Monoglot;
import aa.monoglot.misc.keys.AppString;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class HelpWindow extends Stage {
    public HelpWindow(){
        WebView view = new WebView();
        getIcons().setAll(Monoglot.getMonoglot().getIcons());
        initModality(Modality.NONE);
        setScene(new Scene(view, 800, 600));
        setTitle(Monoglot.getMonoglot().getLocalString(AppString.HELP_WINDOW_TITLE));
        setMinHeight(600);
        setMinWidth(800);

        view.getEngine().getLoadWorker().stateProperty().addListener((v, o, n)->{
            if(o == Worker.State.SCHEDULED && n == Worker.State.RUNNING){
                ((JSObject) view.getEngine().executeScript("window")).setMember("jlocal", Monoglot.getMonoglot().getResources());
            }
        });
        view.getEngine().load(getClass().getClassLoader().getResource("help/index.html").toExternalForm());
    }
}
