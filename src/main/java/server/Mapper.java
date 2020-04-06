package server;


import java.util.ArrayList;
import java.util.List;

/**
 * @author quanle
 * @date 2020/4/5 8:25 PM
 */
public class Mapper {

    private List<MappedHost> mappedHosts;

    public Mapper(List<MappedHost> mappedHosts) {
        this.mappedHosts = mappedHosts;
    }

    public List<MappedHost> getMappedHosts() {
        return mappedHosts;
    }
// ------------------------------------------------------- Host Inner Class

    protected static final class MappedHost {

        private String name;
        private String url;
        private List<MappedContext> contexts;


        /**
         * Constructor used for the primary Host
         *
         * @param name The name of the virtual host
         */
        public MappedHost(String name, String url, List<MappedContext> contexts) {
            this.name = name;
            this.url = url;
            this.contexts = contexts;
        }

        public String getName() {
            return name;
        }

        public MappedHost setName(String name) {
            this.name = name;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public MappedHost setUrl(String url) {
            this.url = url;
            return this;
        }

        public List<MappedContext> getContexts() {
            return contexts;
        }

        public MappedHost setContexts(List<MappedContext> contexts) {
            this.contexts = contexts;
            return this;
        }
    }


    // ---------------------------------------------------- Context Inner Class


    protected static final class MappedContext {

        public final String name;
        public final String path;
        public volatile List<MappedWrapper> wrappers;

        public MappedContext(String name, String path) {
            this.name = name;
            this.path = path;
            wrappers = new ArrayList<>();
        }

        public void setWrappers(MappedWrapper exactWrapper) {
            wrappers.add(exactWrapper);
        }

        public String getPath() {
            return path;
        }

        public List<MappedWrapper> getWrappers() {
            return wrappers;
        }

        public String getName() {
            return name;
        }
    }
    // ---------------------------------------------------- Wrapper Inner Class


    protected static class MappedWrapper {

        public final String urlPattern;
        public final HttpServlet servlet;

        public MappedWrapper(
                String urlPattern,
                HttpServlet servlet) {
            this.urlPattern = urlPattern;
            this.servlet = servlet;
        }


        public String getUrlPattern() {
            return urlPattern;
        }

        public HttpServlet getServlet() {
            return servlet;
        }
    }
}
