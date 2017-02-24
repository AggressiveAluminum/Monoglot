package aa.monoglot.db;

import aa.monoglot.Monoglot;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author cofl
 * @date 2/23/2017
 */
@Deprecated
public final class OldHeadword {
    private Integer ID;
    private Timestamp created;

    private final StringProperty word, romanization, pronunciation, stem;
    private final IntegerProperty type, category;
    private final ObjectProperty<Timestamp> modified;

    private final BooleanProperty wasEdited;

    OldHeadword(String word){
        this(null, word, null, null, null, null, null, Timestamp.from(Instant.now()), null);
    }

    OldHeadword(Integer ID, String word, String romanization, String pronunciation, String stem,
                Integer type, Integer category, Timestamp created, Timestamp modified){
        this.ID = ID;
        this.created = created;

        this.word = new SimpleStringProperty(word);
        this.romanization = new SimpleStringProperty(romanization);
        this.pronunciation = new SimpleStringProperty(pronunciation);
        this.stem = new SimpleStringProperty(stem);
        this.type = new SimpleIntegerProperty(type);
        this.category = new SimpleIntegerProperty(category);
        this.modified = new SimpleObjectProperty<>(modified);

        this.wasEdited = new SimpleBooleanProperty(false);

        this.word.addListener(this::changeListener);
        this.word.addListener(this::timeListener);
        this.romanization.addListener(this::changeListener);
        this.pronunciation.addListener(this::changeListener);
        this.stem.addListener(this::changeListener);
        this.type.addListener(this::changeListener);
        this.category.addListener(this::changeListener);
        this.modified.addListener(this::changeListener);
    }

    private void changeListener(ObservableValue value, Object oldVal, Object newVal){
        wasEdited.set(true);
    }

    private void timeListener(ObservableValue value, Object oldVal, Object newVal){
        modified.set(Timestamp.from(Instant.now()));
    }

    public void put() throws SQLException {
        //if(wasEdited.get())
            //Monoglot.getMonoglot().getProject().getDatabase().put(this);
    }

    void setID(int newID){
        this.ID = newID;
    }

    public Integer getID() {
        return ID;
    }

    public String getWord() {
        return word.get();
    }

    public StringProperty wordProperty() {
        return word;
    }

    public String getRomanization() {
        return romanization.get();
    }

    public StringProperty romanizationProperty() {
        return romanization;
    }

    public String getPronunciation() {
        return pronunciation.get();
    }

    public StringProperty pronunciationProperty() {
        return pronunciation;
    }

    public String getStem() {
        return stem.get();
    }

    public StringProperty stemProperty() {
        return stem;
    }

    public int getType() {
        return type.get();
    }

    public IntegerProperty typeProperty() {
        return type;
    }

    public int getCategory() {
        return category.get();
    }

    public IntegerProperty categoryProperty() {
        return category;
    }

    public Timestamp getCreated() {
        return created;
    }

    public Timestamp getModified() {
        return modified.get();
    }

    public ObjectProperty<Timestamp> modifiedProperty() {
        return modified;
    }

    public boolean wasEdited() {
        return wasEdited.get();
    }
}
