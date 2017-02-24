package aa.monoglot.db;

import javafx.beans.Observable;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @author cofl
 * @date 2/24/2017
 */
public class SearchFilter extends BooleanPropertyBase {
    public StringProperty field;
    public ReadOnlyIntegerProperty typeIndex, categoryIndex;
    public ObservableList<Integer> tags;

    public SearchFilter(StringProperty field, ReadOnlyIntegerProperty typeIndex, ReadOnlyIntegerProperty categoryIndex, ObservableList<Integer> tags){
        this.field = field;
        this.typeIndex = typeIndex;
        this.categoryIndex = categoryIndex;
        this.tags = tags;

        this.field.addListener(this::changeListener);
        this.typeIndex.addListener(this::changeListener);
        this.categoryIndex.addListener(this::changeListener);
        this.tags.addListener((Observable c) -> super.set(true));
    }

    private void changeListener(ObservableValue obs, Object oldv, Object newv){
        if(oldv != null && newv != null && !oldv.equals(newv))
            super.set(true);
    }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
