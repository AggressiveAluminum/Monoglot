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

    private static UUID id;
    private static Tag emptyTag;
    private static Tag fullTag;

        @BeforeClass
        public static void init() throws Exception {
            Project.newProject();
            id = Project.getProject().getDatabase().getNextID();
            emptyTag = Tag.create();
            fullTag = Tag.create();
        }

        @AfterClass
        public static void close() throws Exception {
            if(Project.isOpen())
                Project.getProject().close();
        }

        @Test
        public void getNull() throws SQLException {
            Assert.assertNull(Tag.fetch(UUID.randomUUID()));
        }

        @Test
        public void testEmptyTagIsNotNull() throws SQLException{
            Assert.assertNotNull(emptyTag);
        }

        @Test
        public void testInsertEmptyTag() throws SQLException{
            Tag.insert(id, emptyTag);

        }

        @Test
        public void testUpdateEmptyTag() throws SQLException{
            fullTag = Tag.create();
            fullTag = Tag.update(id, "Fun", "This tag is for fun words.");
            Assert.assertTrue("Descriptions are inded the same.",  fullTag.description.toString().equals("This tag is for fun words."));
    }

        @Test
        public void testGoodTagToStringAndEqual() throws SQLException{
            Tag thing = Tag.create();
            UUID ID = Project.getProject().getDatabase().getNextID();
            thing = Tag.insert(ID, "Words", "This tag is for words");
            Assert.assertEquals("Fun", thing
                    .toString());
            Tag compare = Tag.fetch(ID);
            Assert.assertTrue("Tags are the same. ", thing.equals(compare));
        }
}
