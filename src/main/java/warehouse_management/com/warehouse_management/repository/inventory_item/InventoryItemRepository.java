package warehouse_management.com.warehouse_management.repository.inventory_item;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends MongoRepository<InventoryItem, ObjectId>,
        CustomInventoryItemRepository {

    Optional<InventoryItem> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    // tuỳ chọn thêm
    long countByWarehouseId(ObjectId warehouseId);

    long countByContainerId(ObjectId containerId);

    @Query("{'_id': {'$in': ?0}}")
    List<InventoryItem> findByIdIn(List<ObjectId> ids);

    @Query("{'containerId': ?0}")
    List<InventoryItem> findByContainerId(ObjectId containerId);


}