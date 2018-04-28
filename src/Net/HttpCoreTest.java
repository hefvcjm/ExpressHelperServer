package Net;


import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpCoreTest {

    public static void main(String[] args) {
        HttpRequest request = new BasicHttpRequest("GET","/",HttpVersion.HTTP_1_1);
        System.out.println(request.getRequestLine().getMethod());
        System.out.println(request.getRequestLine().getUri());
        System.out.println(request.getProtocolVersion());
        System.out.println(request.getRequestLine().toString());

        System.out.println();

        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,HttpStatus.SC_OK,"OK");
        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());

        System.out.println();

        StringEntity myEntity = new StringEntity("important message", Consts.UTF_8);
        System.out.println(myEntity.getContentType());
        System.out.println(myEntity.getContentLength());
        try {
            System.out.println(EntityUtils.toString(myEntity));
            System.out.println(EntityUtils.toByteArray(myEntity).length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
