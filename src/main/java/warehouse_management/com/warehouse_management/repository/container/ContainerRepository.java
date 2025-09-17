package warehouse_management.com.warehouse_management.repository.container;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.Container;

import java.util.List;

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

    @Aggregation(pipeline = {
            "{$match:  {_id: ?0}, deletedAt: null}",
            "{$project: {containerCode: 1, _id: 0}}"
    })
    String findCodeById(ObjectId id);

}