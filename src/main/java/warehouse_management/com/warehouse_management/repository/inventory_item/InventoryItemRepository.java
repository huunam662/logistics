package warehouse_management.com.warehouse_management.repository.inventory_item;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends MongoRepository<InventoryItem, ObjectId>,
        CustomInventoryItemRepository {

    Optional<InventoryItem> findByProductCode(String productCode);
    Optional<InventoryItem> findBySerialNumber(String serialNumber);

    Optional<InventoryItem> findByCommodityCodeAndDescription(String commodityCode, String description);

    Optional<InventoryItem> findByComponentTypeAndWarehouseId(String componentType, ObjectId warehouseId);

    boolean existsBySerialNumber(String serialNumber);

    boolean existsByProductCode(String productCode);

    // tuỳ chọn thêm
    long countByWarehouseId(ObjectId warehouseId);

    long countByContainerId(ObjectId containerId);

    @Query("{'_id': {'$in': ?0}}")
    List<InventoryItem> findByIdIn(Collection<ObjectId> ids);

    @Query("{'commodityCode': {'$in': ?0}, 'warehouseId': ?1, 'status': ?2}")
    List<InventoryItem> findSparePartByCommodityCodeIn(Collection<String> commodityCodes, ObjectId warehouseId, String inventoryStatus);

    @Query("{'commodityCode': {'$in': ?0}, 'status': ?1}")
    List<InventoryItem> findSparePartByCommodityCodeIn(Collection<String> commodityCodes, String inventoryStatus);

    @Query("{'commodityCode': {'$in': ?0}, 'containerId': ?1}")
    List<InventoryItem> findSparePartByCommodityCodeIn(Collection<String> commodityCodes, ObjectId containerId);

    @Query("{'containerId': ?0}")
    List<InventoryItem> findByContainerId(ObjectId containerId);

    @Query("{'commodityCode': ?0}")
    Optional<InventoryItem> findByCommodityCode(String commodityCode);

    @Query("{'commodityCode': ?0, 'warehouseId': ?1}")
    Optional<InventoryItem> findByCommodityCodeAndWarehouseId(String commodityCode, ObjectId warehouseId);

    @Query("{'commodityCode': ?0, 'warehouseId': ?1}")
    List<InventoryItem> findByCommodityCodeAndWarehouseIdList(String commodityCode, ObjectId warehouseId);

    @Query("{'commodityCode': ?0, 'warehouseId': ?1, 'status': ?2}")
    Optional<InventoryItem> findByCommodityCodeAndWarehouseId(String commodityCode, ObjectId warehouseId, String status);

    @Query(value = "{'_id': ?0}", fields = "{'warehouseId': 1}")
    Optional<InventoryItem> findWarehouseIdById(ObjectId itemId);

    @Aggregation(pipeline = {
            "{$match: {containerId: ?0, status: ?1}}",
            "{$project: {_id: 1}}"
    })
    List<ObjectId> findIdsByContainerIdAndStatus(ObjectId containerId, String status);

    @Query("{vehicleId: ?0, componentType: ?0}")
    Optional<InventoryItem> findByVehicleIdAndComponentType(ObjectId vehicleId, String componentType);

    @Query("{ '_id': { $in: ?0 } }")
    @Update("{ '$set': { 'status': ?1 } }")
    void updateBulkStatusInventoryItem(List<ObjectId> itemIdList, InventoryItemStatus status);

    @Query("{'_id': ?0}")
    @Update("{'$set':  {'vehicleId': ?1}}")
    void updateVehicleIdById(ObjectId id, ObjectId vehicleId);
}