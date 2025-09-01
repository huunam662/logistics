package warehouse_management.com.warehouse_management.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.AuditLog;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, ObjectId> {
}