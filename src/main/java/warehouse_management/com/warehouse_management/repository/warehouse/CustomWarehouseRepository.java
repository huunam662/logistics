package warehouse_management.com.warehouse_management.repository.warehouse;

import org.bson.types.ObjectId;

import java.util.List;

public interface CustomWarehouseRepository {
    long bulkSoftDelete(List<ObjectId> warehouseIds, ObjectId deletedBy);
}
