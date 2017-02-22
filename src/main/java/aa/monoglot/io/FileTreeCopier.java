package aa.monoglot.io;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author cofl
 * @date 2/22/2017
 */
public class FileTreeCopier implements FileVisitor<Path> {
    private final Path src, dest;
    public FileTreeCopier(Path src, Path dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        String newDir = src.relativize(dir).toString();
        System.err.println(dir.toString() + " :: " + newDir);
        Path d = dest.resolve(newDir);
        if(!d.toString().trim().equals("") && Files.notExists(d)) {
            Files.createDirectory(d);
            System.err.println("Created \"" + newDir + "\"");
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String newFile = src.relativize(file).toString();
        System.err.println(file.toString() + " :: " + newFile);
        Files.copy(file, dest.resolve(newFile), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        // try again.
        String newFile = src.relativize(file).toString();
        System.err.println(file.toString() + " :: " + newFile);
        Files.copy(file, dest.resolve(newFile), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        System.err.println("Completed \"" + src.relativize(dir).toString() + "\"");
        return FileVisitResult.CONTINUE;
    }
}
