package Net.Service;

import Database.DBManage;
import Database.UsersDB;
import Infos.UserInfos;
import Net.RequestService;
import SmsService.VcodeManage;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 验证登录服务
 */
public class LoginService implements RequestService {
    @Override
    public Object handleRequest(String content) {
        try {
            JSONObject json = new JSONObject(content);
            String phone = json.getString("phone");
            String code = json.getString("code");
            if (VcodeManage.getInstance().check(phone, code)) {
                System.out.println("登录成功！");
                UsersDB.getInstance(DBManage.getInstance()).update(
                        String.format("update user_infos set state = \"%s\",lastlogin=\"%s\" where phone= \"%s\""
                                , UserInfos.State.STATE_LOGINED.getName()
                                , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                , phone));
                VcodeManage.getInstance().remove(phone);
                return "{\"msg\"" + ":" + "\"登录成功！\"}";
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
        System.out.println("验证码无效！");
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
