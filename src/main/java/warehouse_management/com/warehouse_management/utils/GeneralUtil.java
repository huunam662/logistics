package warehouse_management.com.warehouse_management.utils;

import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.model.InventoryItem;

@Component
public class GeneralUtil {
    //    CONSTANT
    public static final int PXK_PNK_DATASET_ROW_IDX = 17;
    public static final int PXKDCNB_DATASET_ROW_IDX = 18;
    //    CONNECTION INTERFACE
    public static final String AUTH_LOGIN = "AUTH_LOGIN";
    public static final String AUTH_GET_PERMISSION = "AUTH_GET_PERMISSION";
    public static final String AUTH_GET_INFO = "AUTH_GET_INFO";
    public static final String GET_CUSTOMERS = "GET_CUSTOMERS";
    public static final String GET_CUSTOMERS_ALL = "GET_CUSTOMERS_ALL";
    public static final String GET_USERS_BY_ROLE = "GET_USERS_BY_ROLE";
    public static final String CREATE_OFFICE_FROM_WAREHOUSE = "CREATE_OFFICE_FROM_WAREHOUSE";
    public static final String CREATE_CONSIGNMENT_CUSTOMER = "CREATE_CONSIGNMENT_CUSTOMER";

    public static final String secretKey = "rO0ABXNyABFqYXZhLnV0aWwuUmFuZG9tU3RyZWFtVmFsdWVxPGRG1ZYr6g6B8u8fFw==";

    public String buildLiftingFrameLabel(InventoryItem item) {
        return item.getSpecifications().getChassisType() + "-" + item.getSpecifications().getLiftingCapacityKg() + "-" + item.getSpecifications().getLiftingHeightMm();

    }

    public String buildBatteryLabel(InventoryItem item) {
        return item.getSpecifications().getBatteryInfo() + "-" + item.getSpecifications().getBatterySpecification();
    }

    public String buildChargerLabel(InventoryItem item) {
        return item.getSpecifications().getChargerSpecification();
    }

    public String buildVehicleMetaData(InventoryItem item) {
        return "Xe" + item.getProductCode();
    }

    /**
     * Generate warehouse code cho customer ký gửi
     * Format: KG_{lastName}_{3_số_cuối_sdt}
     * Nếu sdt rỗng thì dùng 000
     */
    public String generateWarehouseCode(String lastName, String cellPhone) {
        String prefix = "KG";
        String lastNamePart = lastName != null ? lastName.toUpperCase() : "UNKNOWN";
        
        String phoneSuffix;
        if (cellPhone != null && !cellPhone.trim().isEmpty() && cellPhone.length() >= 3) {
            // Lấy 3 số cuối của số điện thoại
            phoneSuffix = cellPhone.substring(cellPhone.length() - 3);
        } else {
            phoneSuffix = "000";
        }
        
        return prefix + "_" + lastNamePart + "_" + phoneSuffix;
    }

}
