package aa.monoglot.Logs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.logging.Logger;
import java.nio.file.FileStore;


/**
 * Created by Matt on 2/24/17.
 */
public class logSaver {

    public void save(Logger log) {

        FileStore logs = new FileStore() {
            @Override
            public String name() {
                return null;
            }

            @Override
            public String type() {
                return null;
            }

            @Override
            public boolean isReadOnly() {
                return false;
            }

            @Override
            public long getTotalSpace() throws IOException {
                return 0;
            }

            @Override
            public long getUsableSpace() throws IOException {
                return 0;
            }

            @Override
            public long getUnallocatedSpace() throws IOException {
                return 0;
            }

            @Override
            public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
                return false;
            }

            @Override
            public boolean supportsFileAttributeView(String name) {
                return false;
            }

            @Override
            public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
                return null;
            }

            @Override
            public Object getAttribute(String attribute) throws IOException {
                return null;
            }
        }



    }
}
