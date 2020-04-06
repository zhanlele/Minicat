package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author quanle
 * @date 2020/4/4 10:05 PM
 */
public class StaticResourceUtil {

    public static String getAbsoulePath(String path) {
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replaceAll("\\\\", "/") + path;
    }


    /**
     * 根据url和映射实体对象获得上下文内容对象
     *
     * @param url    url
     * @param mapper 映射实体对象
     * @return
     */
    public static Mapper.MappedContext getContext(String url, Mapper mapper) {
        List<Mapper.MappedHost> hostList = mapper.getMappedHosts();
        //1.根据请求路径的主机名匹配主机实例对象
        if (hostList == null || hostList.isEmpty()) {
            return null;
        }
        for (Mapper.MappedHost host : hostList) {
            List<Mapper.MappedContext> contextList = host.getContexts();
            //2.匹配中请求路径的项目名匹配Context并返回
            if (contextList == null || contextList.isEmpty()) {
                return null;
            }
            for (Mapper.MappedContext context : contextList) {
                if (context.getName().equals(url.split("/")[1])) {
                    return context;
                }
            }
        }
        return null;
    }

    /**
     * 读取静态资源文件输入流，通过输出流输出
     */
    public static void outoutStaicResource(InputStream inputStream, OutputStream outputStream) throws IOException {
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }
        int resourceSize = count;
        //输出http请求头，然后再输出具体内容
        outputStream.write(HttpProtocolUtil.getHttpHeader200(resourceSize).getBytes());
        //读取内容输出
        long written = 0;//已经读取的内容长度
        int byteSize = 1024;//计划每次缓冲的长度
        byte[] bytes = new byte[byteSize];
        while (written < resourceSize) {
            if (written + byteSize > resourceSize) {
                byteSize = (int) (resourceSize - written);
            }
            inputStream.read(bytes);
            outputStream.write(bytes);
            outputStream.flush();
            written += byteSize;
        }
    }
}
