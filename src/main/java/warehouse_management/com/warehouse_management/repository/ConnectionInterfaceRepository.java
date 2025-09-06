package warehouse_management.com.warehouse_management.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.ConnectionInterface;

public interface ConnectionInterfaceRepository extends MongoRepository<ConnectionInterface, ObjectId> {
    ConnectionInterface findByInterfaceCode(String interfaceCode);
}
