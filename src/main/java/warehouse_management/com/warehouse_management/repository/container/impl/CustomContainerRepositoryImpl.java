package warehouse_management.com.warehouse_management.repository.container.impl;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.ContainerStatus;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.repository.container.CustomContainerRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

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

    @Override
    public Page<ContainerResponseDto> getPageContainers(PageOptionsDto req) {
        MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(
                Criteria.where("deletedAt").is(null)
        ));
        LookupOperation lookupFromWarehouse = Aggregation.lookup("warehouse", "fromWareHouseId", "_id", "fromWarehouseInfo");
        UnwindOperation unwindFromWarehouse = Aggregation.unwind("fromWarehouseInfo", true);

        LookupOperation lookupToWarehouse = Aggregation.lookup("warehouse", "toWarehouseId", "_id", "toWarehouseInfo");
        UnwindOperation unwindToWarehouse = Aggregation.unwind("toWarehouseInfo", true);

        ProjectionOperation projectStage = Aggregation.project()
                .and("_id").as("id")
                .and("containerCode").as("containerCode")
                .and("containerStatus").as("containerStatus")
                .and("departureDate").as("departureDate")
                .and("arrivalDate").as("arrivalDate")
                .and("completionDate").as("completionDate")
                .and("note").as("note")
                .and("fromWarehouseInfo").as("fromWarehouse")
                .and("toWarehouseInfo").as("toWarehouse")
                .and(
                        ArrayOperators.Reduce.arrayOf("$inventoryItems")
                                .withInitialValue(0)
                                .reduce(
                                        ArithmeticOperators.Add.valueOf("$$value")
                                                .add(
                                                        ArithmeticOperators.Multiply.valueOf(
                                                                        ConditionalOperators.ifNull("$$this.pricing.purchasePrice").then(0)
                                                                )
                                                                .multiplyBy("$$this.quantity")
                                                )

                                )
                ).as("totalAmounts");

        Aggregation aggregation = Aggregation.newAggregation(
                matchStage,
                lookupFromWarehouse,
                unwindFromWarehouse,
                lookupToWarehouse,
                unwindToWarehouse,
                projectStage
        );

        return MongoRsqlUtils.queryAggregatePage(
                Container.class,
                ContainerResponseDto.class,
                aggregation,
                req
        );
    }
}
