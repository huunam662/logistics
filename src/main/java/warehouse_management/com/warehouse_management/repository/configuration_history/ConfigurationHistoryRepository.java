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

    @Query(value = "{performedBy: {$ne: null}, vehicleId: ?0}", sort = "{createdAt: -1}")
    List<ConfigurationHistory> findByVehicleIdOrderByCreatedAtDesc(ObjectId vehicleId);

    @Query("{configurationCode: ?0, vehicleId: ?1}")
    Optional<ConfigurationHistory> findByCodeAndVehicleId(String code, ObjectId vehicleId);

    @Query("{vehicleId: ?0, componentType: ?1, configType: ?1, performedBy: null}")
    Optional<ConfigurationHistory> findByVehicleIdAndComponentTypeAndConfigType(ObjectId vehicleId, String componentType, String configType);

    @Query(value = "{vehicleId: ?0, status: {$ne: 'COMPLETED'}}", sort = "{createdAt: -1}")
    List<ConfigurationHistory> findAllUnCompletedByVehicleId(ObjectId vehicleId);
}