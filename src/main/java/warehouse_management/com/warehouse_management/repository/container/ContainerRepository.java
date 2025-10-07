package warehouse_management.com.warehouse_management.repository.container;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.Container;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContainerRepository extends MongoRepository<Container, ObjectId>,
CustomContainerRepository{

    Container findByContainerCode(String containerCode);

    @Query(value = "{'containerCode': ?0, deletedAt: null}", exists = true)
    boolean existsByContainerCode(String containerCode);

    @Query(value = "{'containerCode': ?0, '_id': {$ne: ?1}, deletedAt: null}", exists = true)
    boolean existsByContainerCode(String containerCode, ObjectId containerId);

    @Query("{ 'deletedAt': null }")
    List<Container> findAll();

    @Query("{'_id': {'$in': ?0}, deletedAt: null}")
    List<Container> findAllInIds(List<ObjectId> ids);

    @Query(value = "{ '_id': ?0 }",
            fields = "{ 'containerCode': 1, 'inventoryItems': 1, 'createdAt': 1, " +
                    "'fromWareHouseId': 1, 'toWarehouseId': 1, 'containerStatus': 1, " +
                    "'departureDate': 1, 'arrivalDate': 1, 'note': 1 }")
    Optional<Container> findByIdForReport(ObjectId id);

}