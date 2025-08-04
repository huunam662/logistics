package warehouse_management.com.warehouse_management.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.util.Optional;

public interface InventoryItemRepository extends MongoRepository<InventoryItem, ObjectId> {

    Optional<InventoryItem> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    // tuỳ chọn thêm
    long countByWarehouseId(ObjectId warehouseId);

    long countByContainerId(ObjectId containerId);

}