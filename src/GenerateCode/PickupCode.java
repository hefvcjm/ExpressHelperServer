package GenerateCode;

import Database.DBManage;
import Database.ExpressDB;
import Infos.ExpressInfos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 产生取货码和条形码
 */
public class PickupCode {

    public static String getCode() {
        ArrayList<String> codeLists = getCodeInUse();
        String code = String.format("%04d", new Random().nextInt(9999));
        while (codeLists.contains(code)) {
            code = String.format("%04d", new Random().nextInt(9999));
        }
        return code;
    }

    public static String getBarcode() {
        ArrayList<String> barcodeLists = getBarcodeUsed();
        String barcode = String.format("%05d", new Random().nextInt(99999))
                + String.format("%05d", new Random().nextInt(99999))
                + String.format("%03d", new Random().nextInt(999));
        while (barcodeLists.contains(barcode)) {
            barcode = String.format("%05d", new Random().nextInt(99999))
                    + String.format("%05d", new Random().nextInt(99999))
                    + String.format("%03d", new Random().nextInt(999));
        }
        return barcode;
    }

    /**
     * 获取正在使用中的取货码
     *
     * @return 正在使用中的取货码
     */
    private static ArrayList<String> getCodeInUse() {
        ArrayList<String> codeLists = new ArrayList<>();
        ExpressDB db = ExpressDB.getInstance(DBManage.getInstance());
        try {
            ResultSet rs = db.query(String.format("select code from express_infos where state in (\"%s\",\"%s\")"
                    , ExpressInfos.State.WAITING_FOR_PICKING_UP.getName()
                    , ExpressInfos.State.DELAY_PICKING_UP.getName()));
            if (rs != null) {
                while (rs.next()) {
                    codeLists.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codeLists;
    }

    /**
     * 获取已经使用的条形码
     *
     * @return 已经使用的条形码
     */
    private static ArrayList<String> getBarcodeUsed() {
        ArrayList<String> barcodeLists = new ArrayList<>();
        ExpressDB db = ExpressDB.getInstance(DBManage.getInstance());
        try {
            ResultSet rs = db.query("select barcode from express_infos");
            if (rs != null) {
                while (rs.next()) {
                    barcodeLists.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return barcodeLists;
    }
}
