package warehouse_management.com.warehouse_management.repository.delivery_order;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;

import java.util.Optional;

public interface DeliveryOrderRepository extends MongoRepository<DeliveryOrder, ObjectId> {

    @Query("{'deliveryOrderCode': ?0}")
    Optional<DeliveryOrder> findByCode(String deliveryOrderCode);

}
