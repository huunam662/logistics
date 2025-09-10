package warehouse_management.com.warehouse_management.repository.configuration_history;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface ConfigurationHistoryRepository extends CustomConfigurationHistoryRepository, MongoRepository<ConfigurationHistory, ObjectId> {

    // Lấy bản cấu hình mới nhất theo vehicleId
    Optional<ConfigurationHistory> findTopByVehicleIdOrderByCreatedAtDesc(ObjectId vehicleId);

    // Lấy bản cấu hình mới nhất theo vehicleId dựa trên isLatest = true
    Optional<ConfigurationHistory> findFirstByVehicleIdAndIsLatestTrue(ObjectId vehicleId);
}