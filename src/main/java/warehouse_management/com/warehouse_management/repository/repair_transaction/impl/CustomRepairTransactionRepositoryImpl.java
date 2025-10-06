package warehouse_management.com.warehouse_management.repository.repair_transaction.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Repair;
import warehouse_management.com.warehouse_management.model.RepairTransaction;
import warehouse_management.com.warehouse_management.repository.repair_transaction.CustomRepairTransactionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomRepairTransactionRepositoryImpl implements CustomRepairTransactionRepository {

    private final MongoTemplate mongoTemplate;

    @Transactional
    @Override
    public List<RepairTransaction> bulkInsert(List<RepairTransaction> repairTransactions) {
        if (repairTransactions.isEmpty()) return new ArrayList<>();
        return mongoTemplate.insertAll(repairTransactions).stream().toList();
    }

    @Transactional
    @Override
    public void bulkUpdateReasonAndIsRepaired(List<RepairTransaction> repairTransactions) {
        if (repairTransactions == null || repairTransactions.isEmpty())
            return;

        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, RepairTransaction.class);

        for (var repairTrans : repairTransactions) {

            Query query = new Query(Criteria.where("_id").is(repairTrans.getId()));

            Update update = new Update()
                    .set("reason", repairTrans.getReason())
                    .set("isRepaired", repairTrans.getIsRepaired());

            ops.updateOne(query, update);
        }

        ops.execute();
    }

    @Transactional
    @Override
    public void updateIsRepaired(ObjectId repairTransactionId, boolean isRepaired, String repairedBy) {

        Query query = new Query(Criteria.where("_id").is(repairTransactionId));

        Update update = new Update().set("isRepaired", isRepaired)
                        .set("repairedBy", repairedBy)
                        .set("repairedAt", LocalDateTime.now());

        mongoTemplate.updateFirst(query, update, RepairTransaction.class);
    }

    @Transactional
    @Override
    public void bulkUpdateIsRepaired(List<ObjectId> repairTransactionIds, boolean isRepaired) {
        if (repairTransactionIds == null || repairTransactionIds.isEmpty())
            return;

        Query query = new Query(Criteria.where("_id").in(repairTransactionIds));

        Update update = new Update().set("isRepaired", isRepaired);

        mongoTemplate.updateMulti(query, update, RepairTransaction.class);
    }

    @Transactional
    @Override
    public void bulkDelete(List<ObjectId> repairTransactionIds) {
        if (repairTransactionIds == null || repairTransactionIds.isEmpty())
            return;

        Query query = new Query(Criteria.where("_id").in(repairTransactionIds));

        mongoTemplate.remove(query, RepairTransaction.class);
    }
}
