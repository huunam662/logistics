package warehouse_management.com.warehouse_management.repository.repair_transaction;

import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.RepairTransaction;

import java.util.List;

public interface CustomRepairTransactionRepository {

    List<RepairTransaction> bulkInsert(List<RepairTransaction> repairTransactions);

    void bulkUpdateReasonAndIsRepaired(List<RepairTransaction> repairTransactions);

    void updateIsRepaired(ObjectId repairTransactionId, boolean isRepaired, String repairedBy);

    void bulkUpdateIsRepaired(List<ObjectId> repairTransactionIds, boolean isRepaired);

    void bulkDelete(List<ObjectId> repairTransactionIds, String fullName);
}
