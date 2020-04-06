package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * @author quanle
 * @date 2020/4/4 11:45 PM
 */
public class RequestProcessor extends Thread {

    private Socket socket;
    private Mapper mapper;

    public RequestProcessor(Socket socket, Mapper mapper) {
        this.socket = socket;
        this.mapper = mapper;
    }

    //    private Map<String, HttpServlet> servletMap;
//
//
//    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
//        this.socket = socket;
//        this.servletMap = servletMap;
//    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());


            //请求URL
            String url = request.getUrl();
            String[] urlArr = url.split("/");
            //根据URL匹配资源
            Servlet servlet = getServletByUrl(url, mapper);
            //静态资源处理
            if (servlet == null) {
                response.outputHtml();
            } else {
                //动态资源servlet请求
                //执行servlet
                HttpServlet httpServlet = (HttpServlet) servlet;
                httpServlet.service(request, response);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据url获得动态资源servlet的方法
     *
     * @param url    url
     * @param mapper 映射实体对象
     */
    private Servlet getServletByUrl(String url, Mapper mapper) {
        //1.获得Context
        Mapper.MappedContext context = StaticResourceUtil.getContext(url, mapper);
        //2.根据资源名匹配Wrapper中的资源并返回
        if (context == null) {
            return null;
        }
        List<Mapper.MappedWrapper> wrapperList = context.getWrappers();
        if (wrapperList == null || wrapperList.isEmpty()) {
            return null;
        }
        for (Mapper.MappedWrapper wrapper : wrapperList) {
            if (wrapper.getUrlPattern().equals(url)) {
                return wrapper.getServlet();
            }
        }
        return null;
    }
}
