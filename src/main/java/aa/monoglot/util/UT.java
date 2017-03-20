package aa.monoglot.util;

/**
 * Simple utility functions.
 */
public class UT {
    /**
     * String null coallesce to empty string.
     */
    public final static String c(String o){
        if(o == null)
            return "";
        return o;
    }

    /**
     * Generic null coallesce to some default.
     */
    public final static <T> T c(T o, T def){
        if(o == null)
            return def;
        return o;
    }

    /**
     * Compare two objects, taking nulls into account.
     */
    public final static <T> boolean nc(T a, T b){
        if(a == null)
            return b == null;
        return a.equals(b);
    }
}
