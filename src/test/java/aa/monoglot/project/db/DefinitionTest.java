package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

public class DefinitionTest {
    private static Headword word1, word2;
    @BeforeClass
    public static void init() throws Exception {
        Project.newProject();
        word1 = Headword.put(Headword.create().update("test","","","",null,null));
        word2 = Headword.put(Headword.create().update("tekst","text","tɛk͡st","tex",null,null));
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
        Definition def2 = def1.update("A test definition");
        Assert.assertEquals(def1.ID, def2.ID);
        Assert.assertEquals(def1.headwordID, def2.headwordID);
    }

    @Test
    public void updateOrder() throws SQLException {
        Definition def1 = Definition.create(word2, null).update("test1");
        Definition def2 = Definition.create(word2, def1).update("test2");
        Assert.assertEquals(def1.order, 1);
        Assert.assertEquals(def2.order, 2);
        //TODO
    }
}
