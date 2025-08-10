package warehouse_management.com.warehouse_management.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.Container;

import java.util.List;

@Repository
public interface ContainerRepository extends MongoRepository<Container, ObjectId> {

    Container findByContainerCode(String containerCode);

    boolean existsByContainerCode(String containerCode);

    @Query("{ 'deletedAt': null }")
    List<Container> findAll();

}