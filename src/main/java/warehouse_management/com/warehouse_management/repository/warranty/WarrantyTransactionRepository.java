package warehouse_management.com.warehouse_management.repository.warranty;

import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.WarrantyTransaction;

public interface WarrantyTransactionRepository extends MongoRepository<WarrantyTransaction, String>, CustomWarrantyTransactionRepository{
}
