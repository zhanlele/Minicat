package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * 把请求信息封装为Request对象（根据InputSteam输入流封装）
 *
 * @author quanle
 * @date 2020/4/4 9:52 PM
 */
public class Request {

    private String method;
    private String url;

    private InputStream inputStream;

    public Request() {
    }

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        //从输入流中获取请求信息
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }

        byte[] bytes = new byte[count];
        inputStream.read(bytes);
        String inputString = new String(bytes);
        String firstLineStr = inputString.split("\\n")[0];
        String[] strings = firstLineStr.split(" ");
        this.method = strings[0];
        this.url = strings[1];

        System.out.println("============>>>>>method: " + method);
        System.out.println("============>>>>>url: " + url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
