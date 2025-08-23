package warehouse_management.com.warehouse_management.utils;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.enumerate.WarehouseSubTranType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTranType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.Warehouse;

@Component
public class GeneralResource {
    //    CONSTANT
    public static final int PXK_PNK_DATASET_ROW_IDX = 17;
    public static final int PXKDCNB_DATASET_ROW_IDX = 18;

    public static Warehouse getWarehouseById(MongoTemplate mongoTemplate, ObjectId warehouseId) {
        Warehouse rs = mongoTemplate.findById(warehouseId, Warehouse.class);
        if (rs == null)
            throw LogicErrException.of("Kho không tồn tại").setHttpStatus(HttpStatus.NOT_FOUND);
        return rs;
    }

    public static String generateTranTicketCode(WarehouseTranType tranType, WarehouseSubTranType subTranType) {
        if (tranType == null) {
            throw new IllegalArgumentException("tranType cannot be null");
        }

        String prefix = switch (tranType) {
            case DEST_TO_DEST_TRANSFER -> "PNKDCNB";
            default -> "";
        };

        // Nếu không có prefix thì trả thẳng timestamp
        if (prefix.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }

        return prefix + "-" + System.currentTimeMillis();
    }

}
