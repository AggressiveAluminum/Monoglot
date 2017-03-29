package aa.monoglot.project;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectTest {
    private static Path savePath;

    @BeforeClass
    public static void initProjectTests() throws Exception{
        savePath = Files.createTempFile("mglt-test", ".mglt");
    }

    @AfterClass
    public static void cleanupProjectTests() throws Exception{
        if(Project.isOpen())
            Project.getProject().close();
        Files.deleteIfExists(savePath);
    }

    @Test
    public void aa_createProject() throws Exception {
        Project.newProject();
    }

    @Test
    public void ab_isOpen(){
        Assert.assertTrue(Project.isOpen());
    }

    @Test
    public void ac_getProject(){
        Assert.assertNotNull(Project.getProject());
    }

    @Test
    public void ad_savePath1(){
        Assert.assertFalse(Project.getProject().hasSavePath());
    }

    @Test
    public void ae_setSavePath(){
        Project.getProject().setSavePath(savePath);
        Assert.assertTrue(Project.getProject().hasSavePath());
    }

    @Test
    public void af_saveNeeded(){
        Assert.assertTrue(Project.getProject().isSaveNeeded());
    }

    @Test
    public void ag_projectSettings() throws IOException {
        Project.getProject().getSettings().put(ProjectKey.K_NAME, "Test1234");
        Project.getProject().getSettings().put(ProjectKey.K_NOTES, "A comment.");
        Project.getProject().getSettings().store(null);
        Project.getProject().markSaveNeeded();
    }

    @Test
    public void ah_save() throws Exception {
        FileTime oldTime = Files.getLastModifiedTime(savePath);
        Project.getProject().save();
        Assert.assertNotEquals(Files.getLastModifiedTime(savePath), oldTime);
    }

    @Test
    public void ai_saveNeeded(){
        Assert.assertFalse(Project.getProject().isSaveNeeded());
    }

    @Test
    public void aj_close(){
        Project.getProject().close();
    }

    @Test
    public void ak_projectClosed(){
        Assert.assertFalse(Project.isOpen());
    }

    @Test
    public void al_openProject() throws Exception {
        Project.openProject(savePath);
    }

    @Test
    public void am_projectSettings(){
        Assert.assertEquals("Test1234", Project.getProject().getSettings().get(ProjectKey.K_NAME));
        Assert.assertEquals("A comment.", Project.getProject().getSettings().get(ProjectKey.K_NOTES));
    }
}
