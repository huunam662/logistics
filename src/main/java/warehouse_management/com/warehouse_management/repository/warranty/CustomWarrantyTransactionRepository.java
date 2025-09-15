package warehouse_management.com.warehouse_management.repository.warranty;

import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.dto.warranty.request.CreateWarrantyTransactionDTO;
import warehouse_management.com.warehouse_management.model.WarrantyTransaction;

import java.util.List;

public interface CustomWarrantyTransactionRepository {
    public WarrantyTransaction switchStatus(ObjectId warrantyTransactionId, boolean isCompleted);
    public List<WarrantyTransaction> updateAll(List<CreateWarrantyTransactionDTO> updateWarrantyTransactionDTOList);
}
