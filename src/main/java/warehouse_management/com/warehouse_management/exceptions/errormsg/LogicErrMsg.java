package warehouse_management.com.warehouse_management.exceptions.errormsg;

public final class LogicErrMsg {

    private LogicErrMsg() {
        // Prevent instantiation
    }

    public static final String VALUE_DUPLICATE = "logicerr.value_duplicate";
    public static final String NOT_FOUND_BY_CODE = "logicerr.not_found_by_code";
    public static final String NOT_FOUND_BY_ID = "logicerr.not_found_by_id";

    // Bảo hành
    public static final String WARRANTY_ITEM_HAVE_NOT_SOLD = "logicerr.warranty.item_have_not_sold";
    public static final String WARRANTY_CLIENT_NOT_FOUND = "logicerr.warranty.client_not_found";
    public static final String WARRANTY_ITEM_IN_WARRANTY = "logicerr.warranty.item_in_warranty";
    public static final String WARRANTY_NOT_FOUND = "logicerr.warranty.warranty_not_found";

    // Sửa chữa
    public static final String REPAIR_ITEM_IS_NOT_IN_STOCK = "logicerr.repair.item_is_not_in_stock";
    public static final String REPAIR_ITEM_IN_REPAIR = "logicerr.repair.item_in_repair";
    public static final String REPAIR_NOT_FOUND = "logicerr.repair.repair_not_found";
}