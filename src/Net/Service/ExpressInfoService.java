package Net.Service;

import Database.DBManage;
import Database.UsersDB;
import Infos.UserInfos;
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 查询快递信息服务
 */
public class ExpressInfoService implements RequestService {
    @Override
    public Object handleRequest(String content) {
        try {
            JSONObject json = new JSONObject(content);
            String phone = json.getString("phone");
            ResultSet state_sets = UsersDB.getInstance(DBManage.getInstance())
                    .query(String.format("select state from user_infos where phone=\"%s\"", phone));
            try {
                if (state_sets.getRow() == 0) {
                    System.out.println(phone + " state:" + "用户尚未注册");
                    return new JSONObject().put("msg", "用户尚未注册").toString();
                } else {
                    state_sets.next();
                    String user_state = state_sets.getString(1);
                    if (user_state.equals(UserInfos.State.STATE_LOGINED.getName())
                            || user_state.equals(UserInfos.State.STATE_ONLINE.getName())) {
                        ResultSet sets = UsersDB.getInstance(DBManage.getInstance())
                                .query(String.format("" +
                                        "select barcode,company,location,code,deadline,state " +
                                        "from express_infos " +
                                        "where phone=\"%s\"", phone));
                        int col = sets.getMetaData().getColumnCount();
                        ResultSetMetaData rsd = sets.getMetaData();
                        for (int i = 1; i <= col; i++) {
                            System.out.println(rsd.getColumnName(i));
                        }
                        JSONObject rsp_json = new JSONObject();
                        int k = 0;
                        while (sets.next()) {
                            k++;
                            JSONObject js = new JSONObject();
                            for (int i = 1; i <= col; i++) {
                                js.put(rsd.getColumnName(i), sets.getString(i));
                            }
                            rsp_json.put("" + k, js.toString());
                        }
                        rsp_json.put("total", "" + k);
                        return rsp_json.toString();
                    } else {
                        System.out.println(phone + " state:" + user_state);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
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
            responesEntity = new StringEntity("Error",
                    ContentType.create("text/html", "UTF-8"));
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
