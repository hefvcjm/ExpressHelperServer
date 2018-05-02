package Database;

import Infos.ExpressInfos;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * 快递信息数据库
 */
public class ExpressDB {

    //数据库管理，用去连接和SQL操作
    DBManage usersDBM;

    private volatile static ExpressDB instance;

    private ExpressDB(DBManage dbManage) {
        this.usersDBM = dbManage;
    }

    public static ExpressDB getInstance(DBManage dbManage) {
        if (instance == null) {
            synchronized (UsersDB.class) {
                if (instance == null) {
                    instance = new ExpressDB(dbManage);
                }
            }
        }
        return instance;
    }

    private String insertSQL(ExpressInfos expressInfos) {
        String sql = "insert into express_Infos (";
        if (expressInfos.getBarcode().length() == 0) {
            return null;
        }
        JSONObject json = new JSONObject(expressInfos.toString());
        Set<String> keys = json.keySet();
        ArrayList<String> values = new ArrayList<>();
        String format = "(";
        for (String key : keys) {
            if (json.getString(key).length() != 0) {
                sql = sql + key + ",";
                format = format + "\"%s\",";
                values.add(json.getString(key));
            }
        }
        sql = sql.substring(0, sql.length() - 1) + ") values " + format.substring(0, format.length() - 1) + ")";
        sql = String.format(sql, values.toArray());
        return sql;
    }

    public boolean insert(ExpressInfos expressInfos) {
        return usersDBM.insert(insertSQL(expressInfos));
    }

    public boolean update(String sql) {
        return usersDBM.update(sql);
    }

    public boolean delete(String sql) {
        return usersDBM.delete(sql);
    }

    public ResultSet query(String sql) {
        return usersDBM.query(sql);
    }

    public void close() {
        usersDBM.close();
    }

    public static void main(String[] args) {
        ExpressDB expressDB = ExpressDB.getInstance(DBManage.getInstance());
        JSONObject json = new JSONObject();
        json.put("barcode", "1234567890123");
        json.put("phone", "12345678901");
        json.put("name", "赵天龙");
        json.put("company", "顺丰");
        json.put("location", "西十五");
        json.put("code", "1442");
        json.put("state", ExpressInfos.State.DELAY_PICKING_UP.getName());
        json.put("deadline", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        ExpressInfos expressInfos = new ExpressInfos(json.toString());
        expressDB.insert(expressInfos);
//        usersDB.delete("delete from user_infos where phone = \"12345678903\"");
        expressDB.close();
    }

}
