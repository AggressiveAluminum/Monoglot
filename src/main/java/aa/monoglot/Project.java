package aa.monoglot;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

/**
 * @author cofl
 * @date 2/17/2017
 */
public class Project {
    //private Database database;
    private File workingDirectory;

    public Project(){
        //TODO
    }

    public boolean hasWorkingDirectory(){
        return workingDirectory != null && workingDirectory.exists() && workingDirectory.isDirectory();
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) throws FileNotFoundException, NotDirectoryException {
        if(!workingDirectory.exists())
            throw new FileNotFoundException(workingDirectory.getAbsolutePath());
        if(!workingDirectory.isDirectory())
            throw new NotDirectoryException(workingDirectory.getAbsolutePath());

        this.workingDirectory = workingDirectory;
    }
}
