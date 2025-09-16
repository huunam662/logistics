package warehouse_management.com.warehouse_management.repository.warehouse.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.WarehouseForOrderDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.GetDepartureWarehouseForContainerDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.warehouse.CustomWarehouseRepository;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

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

    @Override
    public Page<WarehouseResponseDto> findPageWarehouse(PageOptionsDto optionsReq){
        Query query = new Query();
        query.addCriteria(Criteria.where("deletedAt").isNull());
        query.fields().exclude("createdBy");
        query.fields().exclude("updatedBy");
        query.fields().exclude("updatedAt");
        return MongoRsqlUtils.queryPage(Warehouse.class, WarehouseResponseDto.class, query, optionsReq);
    }

    @Override
    public List<GetDepartureWarehouseForContainerDto> getDepartureWarehousesForContainer(String warehouseType) {
        MatchOperation matchStage = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("type").is(warehouseType)
                )
        );

        ProjectionOperation projectStage = Aggregation.project("code", "name")
                .and("_id").as("warehouseId")
                .and("code").as("warehouseCode")
                .and("name").as("warehouseName");

        // Xây dựng pipeline hoàn chỉnh
        Aggregation aggregation = Aggregation.newAggregation(
                matchStage,
                projectStage
        );

        AggregationResults<GetDepartureWarehouseForContainerDto> results = mongoTemplate.aggregate(
                aggregation,
                Warehouse.class, // Lớp entity đầu vào
                GetDepartureWarehouseForContainerDto.class // Lớp DTO đầu ra
        );

        return results.getMappedResults();
    }

    @Override
    public List<WarehouseForOrderDto> getWarehousesForOrder() {
        MatchOperation matchStage = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("type").not().in("PRODUCTION", "CONSIGNMENT")
                )
        );
        Aggregation aggregation = Aggregation.newAggregation(
                matchStage
        );

        ProjectionOperation projectStage = Aggregation.project( "name")
                .and("_id").as("id");

        AggregationResults<WarehouseForOrderDto> results = mongoTemplate.aggregate(
                aggregation,
                Warehouse.class, // Lớp entity đầu vào
                WarehouseForOrderDto.class // Lớp DTO đầu ra
        );

        return results.getMappedResults();
    }


}
