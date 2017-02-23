package aa.monoglot.io;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Copies one directory tree to another.
 * @author cofl
 * @date 2/22/2017
 */
public class FileTreeCopier implements FileVisitor<Path> {
    private final Path src, dest;

    /**
     * @param src The source directory.
     * @param dest The destination directory.
     */
    public FileTreeCopier(Path src, Path dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path d = dest.resolve(src.relativize(dir).toString());
        if(!d.toString().trim().equals("") && Files.notExists(d))
            Files.createDirectory(d);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, dest.resolve(src.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        // try again.
        Files.copy(file, dest.resolve(src.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
