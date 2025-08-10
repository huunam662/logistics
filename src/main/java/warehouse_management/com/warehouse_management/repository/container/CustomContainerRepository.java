package warehouse_management.com.warehouse_management.repository.container;

import org.bson.types.ObjectId;
import java.util.List;

public interface CustomContainerRepository {
    boolean softDeleteById(ObjectId containerId, ObjectId deletedBy);
    long bulkSoftDelete(List<ObjectId> containerIds, ObjectId deletedBy);
}