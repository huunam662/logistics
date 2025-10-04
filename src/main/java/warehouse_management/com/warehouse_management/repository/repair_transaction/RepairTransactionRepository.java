package warehouse_management.com.warehouse_management.repository.repair_transaction;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.model.RepairTransaction;

import java.util.List;

public interface RepairTransactionRepository extends MongoRepository<RepairTransaction, ObjectId>, CustomRepairTransactionRepository {

    @Query("{repairId: ?0, deletedAt: null}")
    List<RepairTransaction> findByRepairId(ObjectId repairId);

    @Query("{repairId: ?0, _id: {$in: ?1}, deletedAt: null}")
    List<RepairTransaction> findAllByRepairIdAndIdIn(ObjectId repairId, List<ObjectId> ids);

    @Query("{repairId: ?0, isRepaired: ?1, deletedAt: null}")
    List<RepairTransaction> findAllByRepairIdAndIdInAndIsRepaired(ObjectId repairId, Boolean isRepaired);

    @Aggregation(pipeline = {
            "{$match: {repairId: ?0, isRepaired: ?1, deletedAt: null}}",
            "{$project: {_id: 1}}"
    })
    List<ObjectId> findAllIdByRepairIdAndIdInAndIsRepaired(ObjectId repairId, Boolean isRepaired);

    @Query("{_id: {$in: ?0}}")
    List<RepairTransaction> findAllByIdIn(List<ObjectId> ids);

    @Query("{repairId: ?0}")
    List<RepairTransaction> findAllByRepairId(ObjectId repairId);
}
