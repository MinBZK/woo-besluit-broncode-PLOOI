package nl.overheid.koop.plooi.dcn.integration.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

// TODO Removwe this and useApache Commons FileUtils instead
public class FileUtils {

    public static void copyDirectoryTo(String src, String dst) throws IOException {
        File srcFile = new File(src);
        File dstFile = new File(dst);

        copyDirectoryCompatibityMode(srcFile, dstFile);
    }

    private static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    private static void copyFile(File sourceFile, File destinationFile)
            throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
                OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    public static void deletePath(String path) throws IOException {
        File directoryToClean = new File(path);
        if (directoryToClean.exists()) {
            for (File subFile : directoryToClean.listFiles()) {
                try (var innerFiles = Files.walk(Paths.get(subFile.getPath()))) {
                    innerFiles
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            }
        }
    }
}
