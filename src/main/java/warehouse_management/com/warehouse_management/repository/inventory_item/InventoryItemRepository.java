package warehouse_management.com.warehouse_management.repository.inventory_item;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.ItemCodeModelSerialDto;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InventoryItemRepository extends MongoRepository<InventoryItem, ObjectId>,
        CustomInventoryItemRepository {

    Optional<InventoryItem> findByProductCode(String productCode);

    Optional<InventoryItem> findBySerialNumber(String serialNumber);

    Optional<InventoryItem> findByCommodityCodeAndDescriptionAndWarehouseId(String commodityCode, String description, ObjectId warehouseId);

    @Query("{'componentType': ?0, warehouseId: ?1, deletedAt: null}")
    Optional<InventoryItem> findByComponentTypeAndWarehouseId(String componentType, ObjectId warehouseId);

    boolean existsBySerialNumber(String serialNumber);

    boolean existsByProductCode(String productCode);

    // tuỳ chọn thêm
    long countByWarehouseId(ObjectId warehouseId);

    long countByContainerId(ObjectId containerId);

    @Query("{'_id': {'$in': ?0}, deletedAt: null}")
    List<InventoryItem> findByIdIn(Collection<ObjectId> ids);

    @Query("{'_id': {'$in': ?0}, 'status': ?1, deletedAt: null}")
    List<InventoryItem> findByIdInAndStatus(Collection<ObjectId> ids, String status);

    @Query("{'commodityCode': {'$in': ?0}, 'warehouseId': ?1, 'status': ?2, deletedAt: null}")
    List<InventoryItem> findSparePartByCommodityCodeIn(Collection<String> commodityCodes, ObjectId warehouseId, String inventoryStatus);

    @Query("{'commodityCode': {'$in': ?0}, 'status': ?1, deletedAt: null}")
    List<InventoryItem> findSparePartByCommodityCodeIn(Collection<String> commodityCodes, String inventoryStatus);

    @Query("{'commodityCode': {'$in': ?0}, 'containerId': ?1, deletedAt: null}")
    List<InventoryItem> findSparePartByCommodityCodeIn(Collection<String> commodityCodes, ObjectId containerId);

    @Query("{'containerId': ?0, deletedAt: null}")
    List<InventoryItem> findByContainerId(ObjectId containerId);

    @Query("{'commodityCode': ?0, deletedAt: null}")
    Optional<InventoryItem> findByCommodityCode(String commodityCode);

    @Query("{'commodityCode': ?0, 'warehouseId': ?1, deletedAt: null}")
    Optional<InventoryItem> findByCommodityCodeAndWarehouseId(String commodityCode, ObjectId warehouseId);

    @Query("{'commodityCode': ?0, 'warehouseId': ?1, deletedAt: null}")
    List<InventoryItem> findByCommodityCodeAndWarehouseIdList(String commodityCode, ObjectId warehouseId);

    @Query("{'commodityCode': ?0, 'warehouseId': ?1, 'status': ?2, deletedAt: null}")
    Optional<InventoryItem> findByCommodityCodeAndWarehouseId(String commodityCode, ObjectId warehouseId, String status);

    @Query(value = "{'_id': ?0, deletedAt: null}", fields = "{'warehouseId': 1}")
    Optional<InventoryItem> findWarehouseIdById(ObjectId itemId);

    @Aggregation(pipeline = {
            "{$match: {containerId: ?0, status: ?1, deletedAt: null}}",
            "{$project: {_id: 1}}"
    })
    List<ObjectId> findIdsByContainerIdAndStatus(ObjectId containerId, String status);

    @Query("{vehicleId: ?0, componentType: ?1, deletedAt: null}")
    Optional<InventoryItem> findByVehicleIdAndComponentType(ObjectId vehicleId, String componentType);

    @Query("{ '_id': { $in: ?0 } }")
    @Update("{ '$set': { 'status': ?1 } }")
    void updateBulkStatusInventoryItem(List<ObjectId> itemIdList, InventoryItemStatus status);

    @Query("{'_id': ?0}")
    @Update("{'$set':  {'vehicleId': ?1}}")
    void updateVehicleIdById(ObjectId id, ObjectId vehicleId);

    @Aggregation(pipeline = {
            "{$match: {vehicleId: ?0, deletedAt: null}}",
            "{$project: {componentType: 1, _id: 0}}"
    })
    List<String> findComponentTypeByVehicleId(ObjectId vehicleId);


    @Aggregation(pipeline = {
            "{$match: {componentType: ?0, status: 'IN_STOCK', vehicleId: null, deletedAt: null}}",
            "{$lookup: {from: 'warehouse', localField: 'warehouseId', foreignField: '_id', as: 'warehouse'}}",
            "{$unwind: '$warehouse'}",
            "{$match: {'warehouse.deletedAt': null}}",
            "{$project: {componentId: '$_id', componentType: 1, serialNumber: 1, commodityCode: 1, warehouseCode: '$warehouse.code', warehouseName: '$warehouse.name'}}"
    })
    List<Map<String, Object>> findWarehouseContainsComponent(String componentType);

    @Aggregation(pipeline = {
            "{$match: {componentType: ?0, vehicleId: {'$ne': null}, deletedAt: null}}",
            "{$lookup: {from: 'inventory_item', localField: 'vehicleId', foreignField: '_id', as: 'vehicle'}}",
            "{$unwind: '$vehicle'}",
            "{$match: {'vehicle.status': 'IN_REPAIR', 'vehicle.deletedAt': null}}",
            "{$project: {vehicleId: '$vehicle._id', productCode: '$vehicle.productCode', serialNumber: '$vehicle.serialNumber', model: '$vehicle.model'}}"
    })
    List<ItemCodeModelSerialDto> findVehicleByComponentTypeAndInRepair(String componentType);

    @Aggregation(pipeline = {
            "{$match: {vehicleId: ?0, componentType: ?1, deletedAt: null}}",
            "{$project: {commodityCode: 1, productCode: 1, salePriceR0: '$pricing.salePriceR0', salePriceR1: '$pricing.salePriceR1'}}"
    })
    Optional<Map<String, Object>> findCodeAndPriceByVehicleIdAndComponentType(ObjectId vehicleId, String componentType);

}