package aa.monoglot.ui.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Darren on 2/18/17.
 */
public class SaveLoad {

    static public void Save(String savePath, String fileName) throws java.io.IOException {

        File f = new File(savePath + fileName);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
        File[] dirListing = f.listFiles();
        for(int i = 0; i < dirListing.length; i++){
            File input = new File(dirListing[i].getPath());
            ZipEntry e = new ZipEntry(input.getName());
            out.putNextEntry(e);
        }
        /*ZipEntry e;
        //System.out.println(dirListing.toString());
        if (dirListing != null) {
            for (File child : dirListing) {
                e = new ZipEntry(child.getName());
                out.putNextEntry(e);
            }
        }*/
        out.closeEntry();
        out.close();
    }
}
