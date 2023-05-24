package nl.overheid.koop.plooi.test.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;

public final class TestUtils {

    private TestUtils() {
    }

    public static String readFileAsString(Class<? extends Object> clazz, String fileName) {
        try {
            return stripWindowsCR(Files.readString(resolve(clazz, fileName).toPath()));
        } catch (IOException e) {
            Assertions.fail("Cannot read " + fileName, e);
            return "fake";
        }
    }

    public static byte[] readFileAsBytes(Class<? extends Object> clazz, String fileName) {
        try {
            return Files.readAllBytes(resolve(clazz, fileName).toPath());
        } catch (IOException e) {
            Assertions.fail("Cannot read " + fileName, e);
            return "fake".getBytes();
        }
    }

    public static File resolve(Class<? extends Object> clazz, String fileName) {
        return new File("src/test/resources/" + clazz.getPackage().getName().replace('.', '/') + "/" + fileName);
    }

    public static String stripWindowsCR(String str) {
        return str.replace("\r", "");
    }

    public static String stripTrailingSpaces(String str) {
        return str.replaceAll(" +\n", "\n").replaceAll("\n+", "\n");
    }
}
