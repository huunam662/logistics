package warehouse_management.com.warehouse_management.repository.warehouse_transaction;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;

import java.util.List;

@Repository
public interface WarehouseTransactionRepository extends MongoRepository<WarehouseTransaction, ObjectId>, CustomWarehouseTransactionRepository {
    List<WarehouseTransaction> findByStatus(String status);

    List<WarehouseTransaction> findByRequesterId(ObjectId userId);

    List<WarehouseTransaction> findByApproverId(ObjectId userId);
}