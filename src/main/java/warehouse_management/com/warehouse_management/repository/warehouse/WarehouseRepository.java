package warehouse_management.com.warehouse_management.repository.warehouse;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.pojo.IdProjection;
import java.util.List;

public interface WarehouseRepository extends MongoRepository<Warehouse, ObjectId>,
CustomWarehouseRepository {
    @Query("{ 'deletedAt': null }")
    List<Warehouse> findAll();

    @Query(value = "{ 'type': ?0 }", fields = "{ '_id': 1 }")
    List<IdProjection> findAllIdsByType(WarehouseType type);

    @Aggregation(pipeline = {
            "{$match:  {_id: ?0}}",
            "{$project: {type: 1, _id: 0}}"
    })
    String findTypeById(ObjectId id);
}