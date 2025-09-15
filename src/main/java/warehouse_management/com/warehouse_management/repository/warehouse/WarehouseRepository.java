package warehouse_management.com.warehouse_management.repository.warehouse;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import warehouse_management.com.warehouse_management.dto.warehouse.response.GetDepartureWarehouseForContainerDto;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.dto.warehouse.response.IdAndNameWarehouseDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.IdProjection;
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

    @Aggregation(pipeline = {
            "{$match:  {_id: {$in: ?0}}}",
            "{$project: {type: 1, _id: 0}}"
    })
    List<String> findAllTypeInIds(List<ObjectId> ids);

    @Aggregation(pipeline = {
            "{$match: {type: ?0, deletedAt: null}}",
            "{$project: {id: '$_id', name: 1}}"
    })
    List<IdAndNameWarehouseDto> findIdsByType(String type);

}