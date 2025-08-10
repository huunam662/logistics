package warehouse_management.com.warehouse_management.repository.container.impl;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.enumerate.ContainerStatus;
import warehouse_management.com.warehouse_management.repository.container.CustomContainerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomContainerRepositoryImpl implements CustomContainerRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomContainerRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean softDeleteById(ObjectId containerId, ObjectId deletedBy) {
        Query query = new Query(Criteria.where("_id").is(containerId).and("deletedAt").isNull());

        Update update = new Update()
                .set("deletedAt", LocalDateTime.now())
                .set("deletedBy", deletedBy);

        UpdateResult result = mongoTemplate.updateFirst(query, update, "container");
        return result.getModifiedCount() == 1;
    }

    @Override
    public long bulkSoftDelete(List<ObjectId> containerIds, ObjectId deletedBy) {
        Query query = new Query(Criteria.where("_id").in(containerIds).and("deletedAt").isNull());

        Update update = new Update()
                .set("deletedAt", LocalDateTime.now())
                .set("deletedBy", deletedBy);
        // 3. Thực thi updateMany
        UpdateResult result = mongoTemplate.updateMulti(query, update, "container");

        return result.getModifiedCount();
    }
}
