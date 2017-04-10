package aa.monoglot.project.tables;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Alex on 4/5/17.
 */
public class InflectionTable implements Serializable {

    private void save(){

        Object object = null;
    //Serialize table
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("table.ser"))) {
            out.writeObject(object);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void open(String type){



    }

}
