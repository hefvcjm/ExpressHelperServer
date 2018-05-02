package Net.Service;

import Database.DBManage;
import Database.ExpressDB;
import GenerateCode.PickupCode;
import Infos.ExpressInfos;
import Net.RequestService;
import SmsService.SmsService;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * 快递助手新增快件服务
 */
public class AddExpressService implements RequestService {
    @Override
    public Object handleRequest(String content) {
        try {
            JSONObject json = new JSONObject(content);
            Set<String> keys = json.keySet();
            if (!keys.contains("phone") || !keys.contains("company") || !keys.contains("name") || !keys.contains("location")) {
                System.out.println("添加快递信息不够");
                return null;
            }
            for (String key : keys) {
                if (!key.equals("phone") && !key.equals("company") && !key.equals("name") && !key.equals("location")) {
                    json.remove(key);
                }
            }
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, 3);// num为增加的天数，可以改变的
            Date date = ca.getTime();
            json.put("deadline", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date))
                    .put("barcode", PickupCode.getBarcode())
                    .put("code", PickupCode.getCode())
                    .put("state", ExpressInfos.State.WAITING_FOR_PICKING_UP.getName());
            ExpressDB.getInstance(DBManage.getInstance()).insert(new ExpressInfos(json.toString()));
            try {
                //json={"code":"????","company":"company_name","deadline":"deadline_info","location":"location_info"}
                SendSmsResponse response = SmsService.sendSms(json.getString("phone"), SmsService.TEMPLATE_EXPRESS
                        , new JSONObject().put("code", json.getString("code"))
                                .put("company", json.getString("company"))
                                .put("deadline", json.getString("deadline"))
                                .put("location", json.getString("location")).toString());
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
                    System.out.println("到货信息已发送给用户");
                }
            } catch (ClientException e) {
                e.printStackTrace();
                System.out.println("到货信息已发送中出错");
                return null;
            }
            return "{\"msg\"" + ":" + "\"添加物流到货信息成功！\"}";

        } catch (JSONException je) {
            je.printStackTrace();
            System.out.println("JSONException");
            return null;
        }
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
            System.out.println("rsp==null");
            responesEntity = new StringEntity(new JSONObject().put("msg", "Error").toString(),
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
