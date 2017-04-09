package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class HeadwordTest {
    @BeforeClass
    public static void init() throws Exception {
        Project.newProject();
    }

    @AfterClass
    public static void close() throws Exception {
        if(Project.isOpen())
            Project.getProject().close();
    }

    @Test
    public void headwordFetchNull() throws SQLException {
        Assert.assertNull(Headword.fetch(Project.getProject().getDatabase().getNextID("entry")));
    }

    @Test
    public void headwordCreate() throws SQLException {
        Headword word = Headword.create("word");
        Assert.assertNotNull(word);
        Assert.assertEquals("word", word.word);
    }

    @Test
    public void fetchList() throws SQLException {
        Headword test = Headword.create("testb1");
        Headword test2 = Headword.create("testb2");
        List<Headword> words = Project.getProject().getDatabase().simpleSearch("testb",null,null,null);
        System.err.println(words.toString());
        Assert.assertEquals(2, words.size());
        Assert.assertEquals(test, words.get(0));
        Assert.assertEquals(test2, words.get(1));
    }

    @Test
    public void deleteWord() throws SQLException {
        Headword word = Headword.create("testc1");
        Assert.assertNotNull(Headword.fetch(word.ID));
        Headword.delete(word);
        Assert.assertNull(Headword.fetch(word.ID));
    }
}
