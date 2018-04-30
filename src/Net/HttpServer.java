package Net;

import SmsService.VcodeManage;
import org.apache.http.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class HttpServer {
    private static int port = 8443;

    public static void main(String[] args) throws Exception {

        VcodeManage vcodeManage = VcodeManage.getInstance();
//        SSLContext sslContext = null;
//        if (port == 8443) {
//            // Initialize SSL context
//            URL url = HttpServer.class.getResource("/my.keystore");
//            if (url == null) {
//                System.out.println("Keystore not found");
//                System.exit(1);
//            }
//            sslContext = SSLContexts.custom()
//                    .loadKeyMaterial(url, "secret".toCharArray(), "secret".toCharArray())
//                    .build();
//        }

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        final org.apache.http.impl.bootstrap.HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("HTTP/1.1")
                .setSocketConfig(socketConfig)
//                .setSslContext(sslContext)
                .registerHandler("*", new MyHttpRequestHandler())
                .create();

        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown(5, TimeUnit.SECONDS);
            }
        });
    }


    static class MyHttpRequestHandler implements HttpRequestHandler {

        public void handle(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }
            String target = request.getRequestLine().getUri();
            System.out.println(request.getRequestLine().toString());
            Header type = request.getFirstHeader("type");
            String requestType;
            if (type != null) {
                CharArrayBuffer buf = new CharArrayBuffer(type.toString().length());
                buf.append(type.toString());
                int i = buf.indexOf(':');
                requestType = buf.substringTrimmed(i + 1, buf.length());
            } else {
                requestType = null;
            }

            if (method.equals("POST")) {
                String content;
                for (Header header : request.getAllHeaders()) {
                    System.out.println(header.getName() + ":" + header.getValue());
                }
                if (request instanceof HttpEntityEnclosingRequest) {
                    HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                    byte[] entityContent = EntityUtils.toByteArray(entity);
                    content = new String(entityContent);
                    System.out.println("content:" + content);
                } else {
                    content = null;
                }
                RequestService requestService = new ServiceFactory().getService(requestType);
                String rsp;
                if (requestService != null) {
                    System.out.println("requestService != null");
                    rsp = requestService.handleRequest(content);
                } else {
                    System.out.println("requestService == null");
                    rsp = null;
                }
                StringEntity responesEntity;
                if (rsp != null) {
                    responesEntity = new StringEntity(rsp,
                            ContentType.create("application/json", "UTF-8"));
                    response.setStatusCode(HttpStatus.SC_OK);
                    response.setEntity(responesEntity);
                } else {
                    responesEntity = new StringEntity("Error",
                            ContentType.create("text/html", "UTF-8"));
                    response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                    response.setEntity(responesEntity);
                    System.out.println("response headers:");
                    for (Header header : response.getAllHeaders()) {
                        System.out.println(header.getName() + ":" + header.getValue());
                    }
                    System.out.println("response entities:" + EntityUtils.toString(response.getEntity()));
                }
            } else if (method.equals("GET")) {

            } else if (method.equals("HEAD")) {

            }


        }

    }

}
