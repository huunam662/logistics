package warehouse_management.com.warehouse_management.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    // Tìm kho theo mã code (duy nhất)
    Optional<Warehouse> findByCode(String code);

    // Tìm kho theo tên gần đúng (không phân biệt hoa thường)
    List<Warehouse> findByNameRegexIgnoreCase(String namePattern);

    // Tìm tất cả các kho theo trạng thái
    List<Warehouse> findByStatus(String status);

    // Tìm kho theo loại (PRODUCTION, ARRIVAL, DEPARTURE,...)
    List<Warehouse> findByType(String type);

    // Tìm các kho do một người quản lý
    List<Warehouse> findByManagedBy(ObjectId userId);
}