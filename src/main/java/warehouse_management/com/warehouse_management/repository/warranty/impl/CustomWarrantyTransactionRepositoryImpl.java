package warehouse_management.com.warehouse_management.repository.warranty.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.warranty.request.CreateWarrantyTransactionDTO;
import warehouse_management.com.warehouse_management.model.WarrantyTransaction;
import warehouse_management.com.warehouse_management.repository.warranty.CustomWarrantyTransactionRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomWarrantyTransactionRepositoryImpl implements CustomWarrantyTransactionRepository {
    private final MongoTemplate mongoTemplate;
    private final CustomAuthentication customAuthentication;

    @Override
    public WarrantyTransaction switchStatus(ObjectId warrantyTransactionId, boolean isCompleted) {
        Query query = new Query(Criteria.where("_id").is(warrantyTransactionId));
        Update update = new Update().set("isCompleted", isCompleted);

        return mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                WarrantyTransaction.class);
    }

    @Override
    public List<WarrantyTransaction> updateAll(List<CreateWarrantyTransactionDTO> updateWarrantyTransactionDTOList) {
        if (!updateWarrantyTransactionDTOList.isEmpty()) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, WarrantyTransaction.class);

            for (CreateWarrantyTransactionDTO updateWarrantyTransactionDTO : updateWarrantyTransactionDTOList) {
                Query query = new Query(Criteria.where("_id").is(updateWarrantyTransactionDTO.getWarrantyTransactionId()));
                Update update = new Update()
                        .set("sparePartWarranty", updateWarrantyTransactionDTO.getSparePartWarranty())
                        .set("reason", updateWarrantyTransactionDTO.getReason())
                        .set("isCompleted", updateWarrantyTransactionDTO.getIsCompleted())
                        .set("updateByName", customAuthentication.getUser().getFullName());

                bulkOps.updateOne(query, update);
            }

            bulkOps.execute();
        }

        return mongoTemplate
                .find(new Query(Criteria
                                .where("_id")
                                .in(updateWarrantyTransactionDTOList
                                        .stream()
                                        .map(CreateWarrantyTransactionDTO::getWarrantyTransactionId)
                                        .toList())),
                        WarrantyTransaction.class);
    }
}
