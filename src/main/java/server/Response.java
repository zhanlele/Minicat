package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 封装responose对象需要依赖于outputStream
 * 该对象需要提供核心方法，输出()
 *
 * @author quanle
 * @date 2020/4/4 9:52 PM
 */
public class Response {

    private OutputStream outputStream;

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }

    /**
     * 随后要根据url来获取到静态资源的绝对路径，进一步根据绝对路径读取该静态资源文件，
     * 最终通过输出流输出
     *
     * @param path
     */
    /*public void outputHtml(String path) throws IOException {
        //获取静态资源文件的绝对路径
        String absoluteResourcePath = StaticResourceUtil.getAbsoulePath(path);
        //输出静态资源
        File file = new File(absoluteResourcePath);
        if (file.exists() && file.isFile()) {
            //读取静态资源文件输出静态资源
            StaticResourceUtil.outoutStaicResource(new FileInputStream(file), outputStream);
        } else {
            //输出404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }*/

    /**
     * 输出静态资源文件的方法 : 要根据url来获取到静态资源的绝对路径，进一步根据绝对路径读取该静态资源文件，最终通过输出流输出
     *
     * @param url    url
     * @param mapper 映射实体对象
     * @throws IOException
     */
    public void outputHtml() throws IOException {
        //1.获得Context
        //3.如果没有找到资源输出404
        output(HttpProtocolUtil.getHttpHeader404());
    }
}
