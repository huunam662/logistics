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

}
