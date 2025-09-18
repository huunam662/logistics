package warehouse_management.com.warehouse_management.repository.configuration_history;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;

import java.util.List;
import java.util.Optional;


public interface ConfigurationHistoryRepository extends CustomConfigurationHistoryRepository, MongoRepository<ConfigurationHistory, ObjectId> {

    // Lấy bản cấu hình mới nhất theo vehicleId
    Optional<ConfigurationHistory> findTopByVehicleIdOrderByCreatedAtDesc(ObjectId vehicleId);

    @Aggregation(pipeline = {
            "{$match: {vehicleId: ?0, deletedAt: null}}",
            "{$lookup: {from: 'client', localField: 'vehicleId', foreignField: '_id', as: 'vehicle'}}",
            "{$unwind: '$vehicle'}",
            "{$match: {}}"
    })
    List<ConfigurationHistory> findByVehicleIdOrderByCreatedAtDesc(ObjectId vehicleId);
}