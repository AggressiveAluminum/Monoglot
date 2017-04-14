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
public class TagsTest {

    private static long id;
    private static Tag fullTag;

        @BeforeClass
        public static void init() throws Exception {
            Project.newProject();
            id = Project.getProject().getDatabase().getNextID("tags");
            fullTag = Tag.create("Fun");
        }

        @AfterClass
        public static void close() throws Exception {
            if(Project.isOpen())
                Project.getProject().close();
        }

        @Test
        public void getNull() throws SQLException {
            Assert.assertNull(Tag.fetch(Project.getProject().getDatabase().getNextID("tags")));
        }

        @Test
        public void testEmptyTagIsNotNull() throws SQLException {
            Assert.assertNotNull(fullTag);
        }

        @Test
        public void testUpdateName() throws SQLException{
            fullTag = fullTag.updateName("Fun");
            Assert.assertEquals("Fun", fullTag.toString());
    }

        @Test
        public void testUpdateDescription() throws SQLException{
           fullTag = fullTag.updateDescription("This tag is for fun words.");
           Assert.assertEquals("This tag is for fun words.", fullTag.description);
        }
}
