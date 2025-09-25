package warehouse_management.com.warehouse_management.repository.configuration_history;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;

import java.util.List;
import java.util.Optional;


public interface ConfigurationHistoryRepository extends CustomConfigurationHistoryRepository, MongoRepository<ConfigurationHistory, ObjectId> {

    // Lấy bản cấu hình mới nhất theo vehicleId
    Optional<ConfigurationHistory> findTopByVehicleIdOrderByCreatedAtDesc(ObjectId vehicleId);

    @Query(value = "{performedBy: {$ne: null}, vehicleId: ?0, deletedAt: null}", sort = "{createdAt: -1}")
    List<ConfigurationHistory> findByVehicleIdOrderByCreatedAtDesc(ObjectId vehicleId);

    @Query("{configurationCode: ?0, vehicleId: ?1, deletedAt: null}")
    Optional<ConfigurationHistory> findByCodeAndVehicleId(String code, ObjectId vehicleId);

    @Query("{vehicleId: ?0, componentType: ?1, configType: ?2, performedBy: null, deletedAt: null}")
    Optional<ConfigurationHistory> findByVehicleIdAndComponentTypeAndConfigType(ObjectId vehicleId, String componentType, String configType);

    @Query(value = "{vehicleId: ?0, componentType: ?1, configType: ?2, performedBy: null, deletedAt: null}", exists = true)
    Boolean existsByVehicleIdAndComponentTypeAndConfigType(ObjectId vehicleId, String componentType, String configType);

    @Aggregation(pipeline = {
            "{$match: {componentType: ?0, configType: 'SWAP', status: {$ne: 'COMPLETED'}, deletedAt: null}}",
            "{$project: {_id: 0, vehicleId: 1}}"
    })
    List<ObjectId> findAllVehicleIdSwapAndUnCompletedByComponentType(String componentType);

    @Query(value = "{vehicleId: ?0, status: {$ne: 'COMPLETED'}, deletedAt: null}", sort = "{createdAt: -1}")
    List<ConfigurationHistory> findAllUnCompletedByVehicleId(ObjectId vehicleId);

    @Aggregation(pipeline = {
            "{$match: {vehicleId: ?0, configType: 'SWAP', status: {$ne: 'COMPLETED'}, deletedAt: null}}",
            "{$project: {_id: 0, componentType: 1}}"
    })
    List<String> findAllComponentSwapAndUnCompletedByVehicleId(ObjectId vehicleId);

    @Aggregation(pipeline = {
            "{$match: {vehicleId: ?0, configType: {$ne: 'SWAP'}, status: {$ne: 'COMPLETED'}, deletedAt: null}}",
            "{$project: {_id: 0, componentType: 1}}"
    })
    List<String> findAllComponentUnSwapAndUnCompletedByVehicleId(ObjectId vehicleId);

    @Query("{configurationCode: ?0, vehicleId: {$ne: ?1}, deletedAt: null}")
    Optional<ConfigurationHistory> findByConfigurationCodeAndDiffVehicleId(String configurationCode, ObjectId vehicleId);
}