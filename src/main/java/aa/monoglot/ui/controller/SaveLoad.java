package aa.monoglot.ui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Darren on 2/18/17.
 * Save and Load functions, for saving and loading user projects.
 */
public class SaveLoad {

    static public void Save(String savePath, String tempLoc) throws java.io.IOException {

        //Creates zip file a location specified (savePath) and names it (fileName)
        File f = new File(savePath);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));

        File d = new File(tempLoc);
        File[] dirListing = d.listFiles();
        for(int i = 0; i < dirListing.length; i++){
            File input = new File(dirListing[i].getPath());
            FileInputStream fis = new FileInputStream(input);
            ZipEntry e = new ZipEntry(input.getName());
            out.putNextEntry(e);
            byte[] tmp = new byte[4*1024];
            int size = 0;
            while((size = fis.read(tmp)) != -1){
                out.write(tmp, 0, size);
            }
            fis.close();
        }
        out.closeEntry();
        out.flush();
        out.close();
    }

    static public void Load(String projPath, String openLoc) {

    }
}
