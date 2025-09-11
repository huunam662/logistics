package warehouse_management.com.warehouse_management.repository.repair;

import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.RepairTransaction;

public interface RepairTransactionRepository extends MongoRepository<RepairTransaction, String> {

}
