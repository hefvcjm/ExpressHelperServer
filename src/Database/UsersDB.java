package Database;

import Infos.UserInfos;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * 用户信息数据库
 */
public class UsersDB {

    //数据库管理，用去连接和SQL操作
    DBManage usersDBM;

    private volatile static UsersDB instance;

    private UsersDB(DBManage dbManage) {
        this.usersDBM = dbManage;
    }

    public static UsersDB getInstance(DBManage dbManage) {
        if (instance == null) {
            synchronized (UsersDB.class) {
                if (instance == null) {
                    instance = new UsersDB(dbManage);
                }
            }
        }
        return instance;
    }

    private String insertSQL(UserInfos userInfos) {
        String sql = "insert into user_infos (";
        if (userInfos.getPhone().length() == 0) {
            return null;
        }
        JSONObject json = new JSONObject(userInfos.toString());
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
        System.out.println(sql);
        return sql;
    }

    public int insert(UserInfos userInfos) {
        return usersDBM.insert(insertSQL(userInfos));
    }

    public int update(String sql) {
        return usersDBM.update(sql);
    }

    public int delete(String sql) {
        return usersDBM.delete(sql);
    }

    public ResultSet query(String sql) {
        System.out.println(sql);
        return usersDBM.query(sql);
    }

    public void close() {
        usersDBM.close();
    }

    public static void main(String[] args) {
        UsersDB usersDB = UsersDB.getInstance(DBManage.getInstance());
        JSONObject json = new JSONObject();
        json.put("phone", "12345678903");
        json.put("name", "hefvcjm");
        json.put("email", "123@email.com");
        json.put("password", "asdfga");
        json.put("lastlogin", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        UserInfos userInfos = new UserInfos(json.toString());
        usersDB.insert(userInfos);
//        usersDB.delete("delete from user_infos where phone = \"12345678903\"");
        usersDB.close();
    }

}
