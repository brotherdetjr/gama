package brotherdetjr.gama;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Thread.currentThread;

public final class ResourceUtils {

    private ResourceUtils() {
        throw new AssertionError();
    }

    public static InputStream asInputStream(String path) {
        return currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    public static String asString(String path) {
        try {
            return IOUtils.toString(asInputStream(path), "utf8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
