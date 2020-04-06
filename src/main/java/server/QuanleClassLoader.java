package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author quanle
 * @date 2020/4/6 10:00 AM
 */
public class QuanleClassLoader extends ClassLoader {

    private final static String p = "WEB-INF/classes/";

    public Class<?> findClass(String filePath, String name) {
        try {
            String replaceName = name.replaceAll("\\.", "\\/");
            String path = filePath + p + replaceName + ".class";
            FileInputStream in = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            in.close();
            byte[] classBytes = baos.toByteArray();
            return defineClass(classBytes, 0, classBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
