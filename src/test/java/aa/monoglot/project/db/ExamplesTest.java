package aa.monoglot.project.db;

import aa.monoglot.project.Project;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matt on 4/3/17.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExamplesTest {

    private static long id;
    private static Example example;

    @BeforeClass
    public static void init() throws Exception {
        Project.newProject();
        id = Project.getProject().getDatabase().getNextID("examples");
        example = Example.create();
    }

    @AfterClass
    public static void close() throws Exception {
        if(Project.isOpen())
            Project.getProject().close();
    }

    @Test
    public void getNull() throws SQLException {
        Assert.assertNull(Example.fetch(Project.getProject().getDatabase().getNextID("examples")));
    }

    @Test
    public void testUpdateText() throws SQLException{
        example = example.updateText("This is an example.");
        Assert.assertEquals("This is an example.", example.toString());
    }

    @Test
    public void testUpdateExplanation() throws SQLException{
        example = example.updateExplanation("This is an example that is for testing purposes only.");
        Assert.assertEquals("This is an example that is for testing purposes only.", example.explanation);
    }
}
