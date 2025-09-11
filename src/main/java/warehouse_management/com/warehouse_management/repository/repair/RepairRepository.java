package warehouse_management.com.warehouse_management.repository.repair;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.model.Repair;

import java.util.Optional;

public interface RepairRepository extends MongoRepository<Repair, String>, CustomRepairRepository {

    @Query("{'repairInventoryItem._id': ?0, 'status': ?1, 'deletedBy': { '$exists': false }}")
    Optional<Repair> findRepairByItemAndEqualStatus(ObjectId itemId, RepairStatus status);
}
