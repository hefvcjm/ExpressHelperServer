package test;

import Database.DBManage;
import Database.ExpressDB;
import GenerateCode.PickupCode;
import Infos.ExpressInfos;
import org.json.JSONObject;

public class test {

    public static void main(String[] args) {
        ExpressDB expressDB = ExpressDB.getInstance(DBManage.getInstance());
        int n = 10;
        for (int i = 0; i < n; i++) {
            expressDB.insert(new ExpressInfos(new JSONObject()
                    .put("phone", "15802918993")
                    .put("name", "hefvcjm")
                    .put("company", "顺丰快递")
                    .put("location", "西安交通大学西十五快递助手")
                    .put("deadline", "2018-05-01 00:00:00")
                    .put("state", "待取货")
                    .put("code", PickupCode.getCode())
                    .put("barcode", PickupCode.getBarcode())
                    .toString()));
        }
    }
}
