package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtils {

    public static String loadResource(String resource) throws IOException {
        String path = "src/test/resources/" + resource;

        StringBuilder sb;
        BufferedReader buf = null;
        try {
            InputStream is = new FileInputStream(path);
            buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
        } finally {
            if (buf != null) {
                buf.close();
            }
        }

        return sb.toString();
    }

}
