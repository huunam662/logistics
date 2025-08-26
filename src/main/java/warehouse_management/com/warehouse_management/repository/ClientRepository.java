package warehouse_management.com.warehouse_management.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.client.response.ClientRes;
import warehouse_management.com.warehouse_management.model.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends MongoRepository<Client, ObjectId> {
    // Lấy tất cả clients còn active dưới dạng DTO
    @Query(value = "{ 'deletedAt': null }",
            fields = "{ '_id': 1, 'name': 1, 'address': 1 }")
    List<ClientRes> findAllActiveClientRes();

    // Lấy 1 client theo id dưới dạng DTO (chưa bị xóa)
    @Query(value = "{ '_id': ?0, 'deletedAt': null }",
            fields = "{ '_id': 1, 'name': 1, 'address': 1 }")
    Optional<ClientRes> findActiveClientResById(ObjectId id);

    // Lấy 1 client full entity (để update / delete)
    @Query(value = "{ '_id': ?0, 'deletedAt': null }")
    Optional<Client> findActiveClientById(ObjectId id);

}
