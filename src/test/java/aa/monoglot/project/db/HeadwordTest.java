package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
    public void aa_headwordFetch1() throws SQLException {
        Assert.assertNull(Headword.fetch(UUID.randomUUID()));
    }

    @Test
    public void ab_headwordCreate(){
        Headword word = Headword.create();
        Assert.assertNotNull(word);
        Assert.assertNull(word.ID);
    }

    @Test
    public void ac_headwordPut() throws SQLException {
        Headword word = Headword.put(Headword.create().update("test","","","",null,null));
        Assert.assertNotNull(word.ID);
        Assert.assertEquals(word, Headword.fetch(word.ID));
    }

    @Test
    public void ad_fetchList() throws SQLException {
        Headword test = Headword.create().update("testb1","","","",null,null);
        test = Headword.put(test);
        Assert.assertNotNull(test.ID);
        Headword test2 = Headword.create().update("testb2","","","",null,null);
        test2 = Headword.put(test2);
        Assert.assertNotNull(test2.ID);
        List<Headword> words = Project.getProject().getDatabase().simpleSearch("testb",null,null,null);
        System.err.println(words.toString());
        Assert.assertEquals(2, words.size());
        Assert.assertEquals(test, words.get(0));
        Assert.assertEquals(test2, words.get(1));
    }

    @Test
    public void ae_deleteWord() throws SQLException {
        Headword word = Headword.create().update("testc1","","","",null,null);
        word = Headword.put(word);
        Assert.assertNotNull(Headword.fetch(word.ID));
        Headword.delete(word);
        Assert.assertNull(Headword.fetch(word.ID));
    }
}
