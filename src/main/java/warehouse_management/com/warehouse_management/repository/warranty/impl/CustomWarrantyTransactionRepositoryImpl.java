package warehouse_management.com.warehouse_management.repository.warranty.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.WarrantyTransaction;
import warehouse_management.com.warehouse_management.repository.warranty.CustomWarrantyTransactionRepository;

@Repository
@RequiredArgsConstructor
public class CustomWarrantyTransactionRepositoryImpl implements CustomWarrantyTransactionRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public WarrantyTransaction switchStatus(ObjectId warrantyTransactionId, boolean isCompleted) {
        Query query = new Query(Criteria.where("_id").is(warrantyTransactionId));
        Update update = new Update().set("isCompleted", isCompleted);

        return mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                WarrantyTransaction.class);
    }
}
