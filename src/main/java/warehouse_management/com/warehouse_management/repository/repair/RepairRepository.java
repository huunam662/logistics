package warehouse_management.com.warehouse_management.repository.repair;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.enumerate.ComponentType;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.Repair;

import java.util.List;
import java.util.Optional;

public interface RepairRepository extends MongoRepository<Repair, ObjectId>, CustomRepairRepository {

    @Query("{'repairInventoryItem._id': ?0, 'status': ?1, 'deletedBy': { '$exists': false }}")
    Optional<Repair> findRepairByItemAndEqualStatus(ObjectId itemId, RepairStatus status);

    @Query("{vehicleId: ?0, componentType: ?1, repairType: ?2, performedBy: null, deletedAt: null}")
    Optional<Repair> findByVehicleIdAndComponentTypeAndRepairType(ObjectId vehicleId, String componentType, String repairType);

    @Query(value = "{vehicleId: ?0, componentType: ?1, repairType: ?2, performedBy: null, deletedAt: null}", exists = true)
    Boolean existsByVehicleIdAndComponentTypeAndRepairType(ObjectId vehicleId, String componentType, String repairType);

    @Query("{repairCode: ?0}")
    Optional<Repair> findByRepairCode(String repairCode);

    @Query(value = "{vehicleId: ?0, status: {$ne: 'COMPLETED'}, deletedAt: null}", sort = "{createdAt: -1}")
    List<Repair> findAllUnCompletedByVehicleId(ObjectId vehicleId);

    @Query(value = "{vehicleId: ?0, status: 'COMPLETED', performedBy: null, deletedAt: null}", sort = "{completedAt: -1}")
    List<Repair> findAllCompletedAndUnPerformedByVehicleId(ObjectId vehicleId);

    @Aggregation(pipeline = {
            "{$match: {vehicleId: ?0, repairType: {$ne: 'REPAIR'}, performedBy: null, deletedAt: null}}",
            "{$project: {_id: 0, componentType: 1}}"
    })
    List<String> findAllComponentUnRepairAndUnCompletedByVehicleId(ObjectId vehicleId);

    @Aggregation(pipeline = {
            "{$match: {vehicleId: ?0, repairType: 'REPAIR', performedBy: null, deletedAt: null}}",
            "{$project: {_id: 0, componentType: 1}}"
    })
    List<String> findAllComponentRepairAndUnCompletedByVehicleId(ObjectId vehicleId);
}
