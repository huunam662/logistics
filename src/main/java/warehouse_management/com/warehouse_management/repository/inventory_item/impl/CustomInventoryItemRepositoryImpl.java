package warehouse_management.com.warehouse_management.repository.inventory_item.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemProductionVehicleTypeDto;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.inventory_item.CustomInventoryItemRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomInventoryItemRepositoryImpl implements CustomInventoryItemRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomInventoryItemRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<InventoryItemProductionVehicleTypeDto> getItemsFromVehicleWarehouse(ObjectId warehouseId, PageOptionsReq optionsReq) {
        MatchOperation matchStage = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("deletedAt").is(null),
                        Criteria.where("warehouseId").is(warehouseId)
                )
        );

        ProjectionOperation projectStage = Aggregation.project(
                        "productCode", "serialNumber", "model", "status", "manufacturingYear"
                )
                .and("specifications.liftingCapacityKg").as("liftingCapacityKg")
                .and("specifications.chassisType").as("chassisType")
                .and("specifications.liftingHeightMm").as("liftingHeightMm")
                .and("specifications.engineType").as("engineType")
                .and("_id").as("id");

        List<AggregationOperation> pipeline = new ArrayList<>();
        pipeline.add(matchStage);
        pipeline.add(projectStage);

        Aggregation aggregation = Aggregation.newAggregation(pipeline);

        return MongoRsqlUtils.queryAggregatePage(
                InventoryItem.class,          // Lớp đầu vào cho aggregation
                InventoryItemProductionVehicleTypeDto.class, // Lớp đầu ra (DTO)
                aggregation,                  // Pipeline đã xây dựng
                optionsReq                    // Yêu cầu từ client
        );
    }
}