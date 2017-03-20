package aa.monoglot.util;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.function.Consumer;

/**
 * @author cofl
 * @date 3/12/2017
 */
public class SilentException {
    public static final void rethrow(Exception e){
        throw (RuntimeException) e;
    }
    public static final InvalidationListener invalidationListener(ExceptingInvalidationListener function){
        return e -> {
            try {
                function.apply(e);
            } catch(Exception ex){
                rethrow(ex);
            }
        };
    }

    @FunctionalInterface
    public interface ExceptingInvalidationListener {
        void apply(Observable t) throws Exception;
    }
}
