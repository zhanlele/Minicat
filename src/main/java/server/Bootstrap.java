package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * mincat的主类
 *
 * @author quanle
 * @date 2020/4/4 9:29 PM
 */
public class Bootstrap {


    /**
     * 定义socket监听的端口号
     **/
    private int port;
    private Mapper mapper;


    /**
     * minicat启动需要初始化展开的一些操作
     */
    public void start() throws Exception {
        //加载
//        loadServlet();
        loadServer();
        //定义线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue, threadFactory, handler);


        //完成minicat1.0版本（浏览器请求返回一个固定的字符串到页面）
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("==========>>>>Minicat start on port: " + port);

        /**
         * 使用线程池
         */
        while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, mapper);
            threadPoolExecutor.execute(requestProcessor);
        }
    }


    /**
     * 加载web.xml
     */
    private void loadServlet(String path, Mapper.MappedContext mappedContext) {
        try {
            File file = new File(path + "/WEB-INF/web.xml");
            InputStream resourceAsStream = new FileInputStream(file);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletname = servletnameElement.getStringValue();
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletclass = servletclassElement.getStringValue();

                Element servletMapping = (Element) rootElement.selectSingleNode(
                        "/web-app/servlet-mapping[servlet-name='" + servletname + "']");
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();

                //获取servlet对象
                /*QuanleClassLoader quanleClassLoader = new QuanleClassLoader();
                Class<?> clazz = quanleClassLoader.findClass(path + File.separator, servletclass);
                Object obj = clazz.getDeclaredConstructor().newInstance();*/


                Mapper.MappedWrapper mappedWrapper = new Mapper.MappedWrapper(urlPattern,
                        (HttpServlet) Class.forName(servletclass).newInstance());
                mappedContext.setWrappers(mappedWrapper);
            }
        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载server.xml
     */
    private void loadServer() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//Server");
            if (selectNodes == null || selectNodes.size() == 0) {
                return;
            }
            List<Mapper.MappedHost> hosts = new ArrayList<>();
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                Element serviceElement = (Element) element.selectSingleNode("Service");
                Element connector = (Element) serviceElement.selectSingleNode("Connector");
                port = Integer.parseInt(connector.attribute("port").getValue());

                Element engineElement = (Element) connector.selectSingleNode("Engine");
                Element hostElement = (Element) engineElement.selectSingleNode("Host");
                String hostName = hostElement.attribute("name").getValue();

                String appBasePath = hostElement.attribute("appBase").getValue();
                List<Mapper.MappedContext> contexts = new ArrayList<>();
                File file = new File(appBasePath);
                //解析文件，获取servlet以及url-pattern
                if (file.exists()) {
                    File[] files = file.listFiles();
                    if (files == null || files.length == 0) {
                        return;
                    }
                    for (File f : files) {
                        if (f.isDirectory()) {
                            //demo1/demo2文件夹
                            Mapper.MappedContext mappedContext = new Mapper.MappedContext(f.getName(),
                                    f.getAbsolutePath());
                            loadServlet(f.getAbsolutePath(), mappedContext);
                            contexts.add(mappedContext);
                        }
                    }
                }
                //封装host
                Mapper.MappedHost mappedHost = new Mapper.MappedHost(hostName, appBasePath, contexts);
                hosts.add(mappedHost);
            }
            mapper = new Mapper(hosts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * minicat的启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
