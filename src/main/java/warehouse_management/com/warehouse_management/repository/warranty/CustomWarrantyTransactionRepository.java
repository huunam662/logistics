package warehouse_management.com.warehouse_management.repository.warranty;

import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.WarrantyTransaction;

public interface CustomWarrantyTransactionRepository {
    public WarrantyTransaction switchStatus(ObjectId warrantyTransactionId, boolean isCompleted);
}
