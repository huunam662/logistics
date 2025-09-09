package warehouse_management.com.warehouse_management.repository.warranty;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import warehouse_management.com.warehouse_management.enumerate.WarrantyStatus;
import warehouse_management.com.warehouse_management.model.Warranty;

import java.util.Optional;

public interface WarrantyRepository extends MongoRepository<Warranty, String>, CustomWarrantyRepository {

    @Query("{'warrantyInventoryItem._id': ?0, 'status': ?1, 'deletedBy': { '$exists': false }}")
    Optional<Warranty> findWarrantyByItemAndEqualStatus(ObjectId itemId, WarrantyStatus status);

    @Query(value = "{ '_id': ?0 }")
    @Update(value = "{ '$set' : { 'status' : ?1 } }")
    void updateStatus(ObjectId id, WarrantyStatus status);

}
