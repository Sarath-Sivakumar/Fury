package app.personal.MVVM.DB;

import android.os.Build;

import java.util.UUID;

public class IDs {

    //FirebaseIDs.
    public static final String
            users = "USERS",
            password = "PASSWORD",
            number = "NUMBER",
            username = "USERNAME",
            device = "DEVICE",
            unknown = "default",
            id = "ID",
            deviceId = getPhoneID();

    //Firebase instance.
    public static final String
            instance = "https://fury-d3622-default-rtdb.asia-southeast1.firebasedatabase.app";


    private static String getPhoneID() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10)
                + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10)
                + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10)
                + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "null";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

}
