package warehouse_management.com.warehouse_management.utils;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.Warehouse;

@Component
public class GeneralResource {
    //    CONSTANT
    public static final int PXK_PNK_DATASET_ROW_IDX = 17;

    public static Warehouse getWarehouseById(MongoTemplate mongoTemplate, ObjectId warehouseId) {
        Warehouse rs = mongoTemplate.findById(warehouseId, Warehouse.class);
        if (rs == null)
            throw LogicErrException.of("Kho không tồn tại").setHttpStatus(HttpStatus.NOT_FOUND);
        return rs;
    }
}
