package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class DefinitionTest {
    private static Headword word1, word2;
    @BeforeClass
    public static void init() throws Exception {
        Project.newProject();
        word1 = Headword.create("test");
        word2 = Headword.create("tekst");
    }
    @AfterClass
    public static void close() throws Exception {
        if(Project.isOpen())
            Project.getProject().close();
    }

    @Test
    public void create() throws SQLException {
        Definition def1 = Definition.create(word1, null);
        Assert.assertNotNull(def1);
        Assert.assertNotNull(def1.ID);
        Assert.assertEquals(word1.ID, def1.headwordID);
    }

    @Test
    public void updateText() throws SQLException {
        Definition def1 = Definition.create(word1, null);
        Definition def2 = def1.updateText("A test definition");
        Assert.assertEquals(def1.ID, def2.ID);
        Assert.assertEquals(def1.headwordID, def2.headwordID);
    }

    @Test
    public void updateOrder() throws SQLException {
        Definition def1 = Definition.create(word2, null).updateText("test1");
        Definition def2 = Definition.create(word2, def1).updateText("test2");
        Assert.assertEquals(1, def1.order);
        Assert.assertEquals(2, def2.order);
        List<Definition> defs = def1.update(2);
        def1 = defs.get(0);
        def2 = defs.get(1);
        Assert.assertEquals(1, def1.order);
        Assert.assertEquals(2, def2.order);
        Assert.assertEquals("test2", def1.text);
        Assert.assertEquals("test1", def2.text);
    }
}
