import java.util.ArrayList;

/**
 * Created by Darren on 3/13/17.
 */
public class Types {
    public ArrayList<String> types = new ArrayList<String>();

    void add(String temp) {
        types.add(temp);
    }

    void delete(String temp) {
        types.remove(temp);
    }

}
