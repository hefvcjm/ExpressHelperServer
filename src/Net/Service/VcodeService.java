package Net.Service;

import Database.DBManage;
import Database.UsersDB;
import Infos.UserInfos;
import Net.RequestService;
import SmsService.SmsService;
import SmsService.VcodeManage;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
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
import java.util.Random;

/**
 * 获取验证码服务
 */
public class VcodeService implements RequestService {
    @Override
    public Object handleRequest(String content) {
        try {
            JSONObject json = new JSONObject(content);
            String phone = json.getString("phone");
            String code = String.format("%06d", new Random().nextInt(999999));
            try {
                UsersDB.getInstance(DBManage.getInstance()).insert(new UserInfos(new JSONObject().put("phone", phone)
                        .put("state", UserInfos.State.STATE_NOT_LOGIN.getName()).toString()));
                SendSmsResponse response = SmsService.sendSms(phone, SmsService.TEMPLATE_CODE, new JSONObject().put("code", code).toString());
                String rspCode = response.getCode();
                if (rspCode != null && rspCode.equals("OK")) {
                    System.out.println("rspCode:" + rspCode);
                    System.out.println("Message=" + response.getMessage());
                    //开启线程查看短信内容
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            QuerySendDetailsResponse querySendDetailsResponse = null;
                            try {
                                querySendDetailsResponse = SmsService.querySendDetails(response.getBizId(), json.getString("phone"));
                            } catch (ClientException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Content=" + querySendDetailsResponse.getSmsSendDetailDTOs().get(0).getContent());
                        }
                    }).start();
                    System.out.println("发送验证码成功！");
                    VcodeManage.getInstance().remove(phone);
                    VcodeManage.getInstance().add(phone, code);
                    return "{\"msg\"" + ":" + "\"发送验证码成功！\"}";
                }
            } catch (ClientException e) {
                e.printStackTrace();
                return null;
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
        System.out.println("发送验证码出错！");
        return "{\"msg\"" + ":" + "\"发送验证码出错！\"}";
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
