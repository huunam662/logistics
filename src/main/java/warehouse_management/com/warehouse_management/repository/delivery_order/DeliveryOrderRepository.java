package warehouse_management.com.warehouse_management.repository.delivery_order;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.enumerate.DeliveryOrderStatus;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;

import java.util.List;
import java.util.Optional;

public interface DeliveryOrderRepository extends MongoRepository<DeliveryOrder, ObjectId>, CustomDeliveryOrderRepository {

    @Query("{'deliveryOrderCode': ?0}")
    Optional<DeliveryOrder> findByCode(String deliveryOrderCode);

    @Query("{'inventoryItems.commodityCode': ?0, 'inventoryItems.warehouseId': ?1}")
    List<DeliveryOrder> findByCommodityCode(String commodityCode, ObjectId warehouseId);

    @Query("{'inventoryItems.productCode': ?0, 'inventoryItems.warehouseId': ?1}")
    DeliveryOrder findByProductCode(String productCode, ObjectId warehouseId);

    @Query("{'inventoryItems._id': ?0, 'status': {$ne : ?1}, 'deletedBy': { '$exists': false }}")
    Optional<DeliveryOrder> findDeliveryOrderByItemNotEqualDeliveryOrderStatus(ObjectId item, DeliveryOrderStatus status);
}
