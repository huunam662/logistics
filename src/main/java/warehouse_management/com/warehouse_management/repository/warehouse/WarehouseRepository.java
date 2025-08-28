package warehouse_management.com.warehouse_management.repository.warehouse;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.model.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends MongoRepository<Warehouse, ObjectId>,
CustomWarehouseRepository {
    @Query("{ 'deletedAt': null }")
    List<Warehouse> findAll();
}