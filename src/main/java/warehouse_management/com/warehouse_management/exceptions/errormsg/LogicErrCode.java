package warehouse_management.com.warehouse_management.exceptions.errormsg;

import java.util.Map;

//    Tạm thêm, Khi nào cần tới code cho message thì dùng
public class LogicErrCode {
    public static final String F001 = "F001";
    private static final Map<String, String> CODE_TO_MESSAGE_KEY = Map.of(
            F001, "logicerr.value_duplicate"
    );

    public static String getMessageKey(String code) {
        return CODE_TO_MESSAGE_KEY.get(code);
    }
}