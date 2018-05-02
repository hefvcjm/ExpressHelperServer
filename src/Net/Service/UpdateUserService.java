package Net.Service;

import Database.DBManage;
import Database.UsersDB;
import Net.RequestService;
import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * 更新用户信息，比如状态、密码等
 */
public class UpdateUserService implements RequestService {
    @Override
    public Object handleRequest(String content) {
        try {
            JSONObject json = new JSONObject(content);
            String phone = json.getString("phone");
            String str = "";
            Set<String> keys = json.keySet();
            keys.remove("phone");
            for (String key : keys) {
                str += key + String.format("=\"%s\",", json.getString(key));
            }
            str = str.substring(0, str.length() - 1);//去掉最后一个逗号
            if (str.length() != 0) {
                UsersDB.getInstance(DBManage.getInstance()).update(
                        String.format("update user_infos set %s where phone= \"%s\""
                                , str, phone));
                return "{\"msg\"" + ":" + "\"用户信息更新成功！\"}";
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void response(Object rsp, HttpResponse response, HttpContext context) {
        StringEntity responesEntity;
        if (rsp != null) {
            responesEntity = new StringEntity((String) rsp,
                    ContentType.create("application/json", "UTF-8"));
            HttpCoreContext coreContext = HttpCoreContext.adapt(context);
            HttpConnection conn = coreContext.getConnection(HttpConnection.class);
            response.setStatusCode(HttpStatus.SC_OK);
            response.setEntity(responesEntity);
        } else {
            responesEntity = new StringEntity(new JSONObject().put("msg","Error").toString(),
                    ContentType.create("application/json", "UTF-8"));
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            response.setEntity(responesEntity);
            System.out.println("response headers:");
            for (Header header : response.getAllHeaders()) {
                System.out.println(header.getName() + ":" + header.getValue());
            }
            try {
                System.out.println("response entities:" + EntityUtils.toString(response.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
