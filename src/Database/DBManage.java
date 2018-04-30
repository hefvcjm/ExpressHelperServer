package Database;

import java.sql.*;

/**
 * 提供数据库SQL执行的类
 * 内部实现数据库连接
 */
public class DBManage {

    private volatile static DBManage instance;
    //数据库连接实例
    private volatile static Connection conn = null;
    //Statement用来执行SQL语句
    private volatile static Statement stmt = null;

    //驱动程序名
    private String driver = "com.mysql.cj.jdbc.Driver";

    //url
    String url = "jdbc:mysql://localhost:3306/competition?useSSL=false&serverTimezone=Hongkong&useUnicode=true&characterEncoding=utf-8";
    //用户名
    private String user = "root";
    //密码
    private String password = "hefvcjm";

    private DBManage(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        try {
            try {
                try {
                    Class.forName(driver).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DBManage() {
        try {
            try {
                Class.forName(driver).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBManage getInstance(String url, String user, String password) {
        if (instance == null) {
            synchronized (DBManage.class) {
                if (instance == null) {
                    instance = new DBManage(url, user, password);
                }
            }
        }
        return instance;
    }

    public static DBManage getInstance() {
        if (instance == null) {
            synchronized (DBManage.class) {
                if (instance == null) {
                    instance = new DBManage();
                }
            }
        }
        return instance;
    }

    public void close() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    private int execute(String sql) {
        int i = -1;
        try {
            i = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public int insert(String sql) {
        return execute(sql);
    }

    public int update(String sql) {
        return execute(sql);
    }

    public int delete(String sql) {
        return execute(sql);
    }

    private int create(String sql) {
        return execute(sql);
    }

    public static void main(String[] args) {
        DBManage dbManage = DBManage.getInstance();
//        dbManage.insert("insert into user_infos(phone,name) value(\"12345678902\",\"test1\")");
//        dbManage.update("update user_infos set lastlogin=\"2017-01-01 00:00:00\"");
        try {
            ResultSet sets = dbManage.query("select * from user_infos");
            int col = sets.getMetaData().getColumnCount();
            while (sets.next()) {
                for (int i = 1; i <= col; i++) {
                    System.out.print(sets.getString(i) + "\t");
                    if ((i == 2) && (sets.getString(i).length() < 8)) {
                        System.out.print("\t");
                    }
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbManage.close();
    }

}
