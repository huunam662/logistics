package warehouse_management.com.warehouse_management.repository.warehouse.impl;

import warehouse_management.com.warehouse_management.repository.warehouse.CustomWarehouseRepository;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomWarehouseRepositoryImpl implements CustomWarehouseRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomWarehouseRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public long bulkSoftDelete(List<ObjectId> warehouseIds, ObjectId deletedBy) {
        Query query = new Query(Criteria.where("_id").in(warehouseIds));

        Update update = new Update()
                .set("deletedAt", LocalDateTime.now());
        // TODO: set user deleted
        //                .set("deletedBy", deletedBy);

        UpdateResult result = mongoTemplate.updateMulti(query, update, "warehouse");

        return result.getModifiedCount();
    }

    @Override
    public boolean softDeleteById(ObjectId warehouseId, ObjectId deletedBy, String newStatus) {
        Query query = new Query(Criteria.where("_id").is(warehouseId));

        Update update = new Update()
                .set("deletedAt", LocalDateTime.now())
                .set("status", newStatus);

        UpdateResult result = mongoTemplate.updateFirst(query, update, "warehouse");

        return result.getModifiedCount() == 1;
    }
}
