import java.util.ArrayList;

/**
 * Created by Darren on 3/13/17.
 */
public class Types {
    public ArrayList<String> types = new ArrayList<String>();

    public void add(String temp) {
        types.add(temp);
    }

    public void delete(String temp) {
        types.remove(temp);
    }

    public void setName(String n, int index) {
        types.set(index, n);
    }

    public String getName(int index) {
        return types.get(index);
    }

}


