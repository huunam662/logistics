package warehouse_management.com.warehouse_management.repository.warehouse_transaction;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseTransactionRepository extends MongoRepository<WarehouseTransaction, ObjectId>, CustomWarehouseTransactionRepository {
    List<WarehouseTransaction> findByStatus(String status);

    List<WarehouseTransaction> findByRequesterId(ObjectId userId);

    List<WarehouseTransaction> findByApproverId(ObjectId userId);

    @Query(value = "{ '_id': ?0 }",
            fields = "{ 'inventoryItems': 1, 'createdAt': 1, 'reason': 1, 'stockInDepartment': 1, " +
                    "'stockOutDepartment': 1, 'ticketCode': 1, 'title': 1, 'shipUnitInfo': 1 }")
    Optional<WarehouseTransaction> findByIdWithReportFields(ObjectId id);
}