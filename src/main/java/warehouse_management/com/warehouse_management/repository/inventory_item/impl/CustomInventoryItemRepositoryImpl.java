package warehouse_management.com.warehouse_management.repository.inventory_item.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.report_inventory.request.ReportParamsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.response.ReportInventoryDto;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.inventory_item.CustomInventoryItemRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class CustomInventoryItemRepositoryImpl implements CustomInventoryItemRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomInventoryItemRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<InventoryItemProductionVehicleTypeDto> getItemsFromVehicleWarehouse(ObjectId warehouseId, PageOptionsDto optionsReq) {
        MatchOperation matchStage = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("deletedAt").is(null),
                        Criteria.where("warehouseId").is(warehouseId)
                )
        );

        ProjectionOperation projectStage = Aggregation.project(
                        "productCode", "serialNumber", "model", "status", "manufacturingYear"
                )
                .and("liftingCapacityKg").as("liftingCapacityKg")
                .and("chassisType").as("chassisType")
                .and("liftingHeightMm").as("liftingHeightMm")
                .and("engineType").as("engineType")
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


    @Override
    public Page<InventoryDestinationDto> findPageInventoryDestination(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").in(ItemType.VEHICLE.getId(), ItemType.ACCESSORY.getId())
                )),
                Aggregation.project("poNumber", "status", "productCode", "manufacturingYear", "model", "itemType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("hasSideShift").as("hasSideShift") //
                        .and("liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("chassisType").as("chassisType")    //
                        .and("liftingHeightMm").as("liftingHeightMm")    //
                        .and("engineType").as("engineType")  //
                        .and("batteryInfo").as("batteryInfo")    //
                        .and("batterySpecification").as("batterySpecification")  //
                        .and("chargerSpecification").as("chargerSpecification")  //
                        .and("forkDimensions").as("forkDimensions")  //
                        .and("valveCount").as("valveCount")  //
                        .and("otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.agent").as("agent")   //
                        .and("logistics.arrivalDate").as("arrivalDate") //
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryDestinationDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryProductionDto> findPageInventoryProduction(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").in(ItemType.VEHICLE.getId(), ItemType.ACCESSORY.getId())
                )),
                Aggregation.project("poNumber", "productCode", "model", "itemType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate")
                        .and("hasSideShift").as("hasSideShift")
                        .and("liftingCapacityKg").as("liftingCapacityKg")
                        .and("chassisType").as("chassisType")
                        .and("liftingHeightMm").as("liftingHeightMm")
                        .and("engineType").as("engineType")
                        .and("batteryInfo").as("batteryInfo")
                        .and("batterySpecification").as("batterySpecification")
                        .and("chargerSpecification").as("chargerSpecification")
                        .and("forkDimensions").as("forkDimensions")
                        .and("valveCount").as("valveCount")
                        .and("otherDetails").as("otherDetails")
                        .and("pricing.purchasePrice").as("purchasePrice")
                        .and("pricing.salePriceR0").as("salePriceR0")
                        .and("pricing.salePriceR1").as("salePriceR1")
                        .and("pricing.actualSalePrice").as("actualSalePrice")
                        .and("pricing.agent").as("agent")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryProductionDto.class, aggregation, optionsReq);

    }

    @Override
    public Page<InventoryDepartureDto> findPageInventoryDeparture(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("container", "containerId", "_id", "container"),
                Aggregation.unwind("container", true),
                Aggregation.lookup("warehouse", "container.toWarehouseId", "_id", "containerToWarehouse"),
                Aggregation.unwind("containerToWarehouse", true),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").in(ItemType.ACCESSORY.getId(), ItemType.VEHICLE.getId())
                )),
                Aggregation.project("poNumber", "status", "productCode", "manufacturingYear", "model", "itemType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("logistics.arrivalDate").as("arrivalDate") //
                        .and("hasSideShift").as("hasSideShift")  //
                        .and("liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("chassisType").as("chassisType")    //
                        .and("liftingHeightMm").as("liftingHeightMm")    //
                        .and("engineType").as("engineType")  //
                        .and("batteryInfo").as("batteryInfo")    //
                        .and("batterySpecification").as("batterySpecification")  //
                        .and("chargerSpecification").as("chargerSpecification")  //
                        .and("forkDimensions").as("forkDimensions")  //
                        .and("valveCount").as("valveCount")  //
                        .and("otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("container.containerStatus").as("containerStatus")  //
                        .and("container.containerCode").as("containerCode")  //
                        .and("container.departureDate").as("containerDepartureDate") //
                        .and("container.arrivalDate").as("containerArrivalDate") //
                        .and("containerToWarehouse.name").as("containerToWarehouse")    //

        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryDepartureDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryConsignmentDto> findPageInventoryConsignment(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").in(ItemType.ACCESSORY.getId(), ItemType.VEHICLE.getId())
                )),
                Aggregation.project("poNumber", "status", "productCode", "manufacturingYear", "model", "itemType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("hasSideShift").as("hasSideShift") //
                        .and("liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("chassisType").as("chassisType")    //
                        .and("liftingHeightMm").as("liftingHeightMm")    //
                        .and("engineType").as("engineType")  //
                        .and("batteryInfo").as("batteryInfo")    //
                        .and("batterySpecification").as("batterySpecification")  //
                        .and("chargerSpecification").as("chargerSpecification")  //
                        .and("forkDimensions").as("forkDimensions")  //
                        .and("valveCount").as("valveCount")  //
                        .and("otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.agent").as("agent")   //
                        .and("logistics.arrivalDate").as("arrivalDate") //
                        .and("logistics.consignmentDate").as("consignmentDate") //
                        .and("warehouse.name").as("warehouseName")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryConsignmentDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryProductionSparePartsDto> findPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").is(ItemType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "model", "commodityCode", "quantity", "description", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryProductionSparePartsDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryDepartureSparePartsDto> findPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").is(ItemType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "model", "commodityCode", "notes", "quantity", "description", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryDepartureSparePartsDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryDestinationSparePartsDto> findPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").is(ItemType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "model", "commodityCode", "notes", "quantity", "description", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryDestinationSparePartsDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryConsignmentSparePartsDto> findPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("itemType").is(ItemType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "model", "commodityCode", "notes", "quantity", "description", "warehouseType")
                        .and("_id").as("id")
                        .and("warehouse.name").as("warehouseName")  //
                        .and("warehouse.contractNumber").as("contractNumber")   //
                        .and("logistics.orderDate").as("orderDate") //
                        .and("logistics.consignmentDate").as("consignmentDate") //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryConsignmentSparePartsDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryCentralWarehouseProductDto> findPageInventoryCentralWarehouse(PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouse.type").is(WarehouseType.DESTINATION.getId()),
                        Criteria.where("itemType").in(ItemType.VEHICLE.getId(), ItemType.ACCESSORY.getId())
                )),
                Aggregation.project("poNumber", "productCode", "status", "model", "category", "itemType", "serialNumber", "manufacturingYear", "initialCondition", "notes", "warehouseType")
                        .and("_id").as("id")
                        .and("liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("chassisType").as("chassisType")    //
                        .and("liftingHeightMm").as("liftingHeightMm")    //
                        .and("engineType").as("engineType")  //
                        .and("batteryInfo").as("batteryInfo")    //
                        .and("batterySpecification").as("batterySpecification")  //
                        .and("chargerSpecification").as("chargerSpecification")  //
                        .and("forkDimensions").as("forkDimensions")  //
                        .and("valveCount").as("valveCount")  //
                        .and("hasSideShift").as("hasSideShift")  //
                        .and("otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.agent").as("agent")   //
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryCentralWarehouseProductDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryCentralWarehouseSparePartDto> findPageInventoryCentralWarehouseSparePart(PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouse.type").is(WarehouseType.DESTINATION.getId()),
                        Criteria.where("itemType").is(ItemType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "commodityCode", "status", "model", "quantity", "description", "notes", "contractNumber", "warehouseType")
                        .and("_id").as("id")
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.agent").as("agent")   //
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryCentralWarehouseSparePartDto.class, aggregation, optionsReq);
    }

    @Override
    public List<InventoryPoWarehouseDto> findPoNumbersOfInventoryInStock(String warehouseType, List<String> itemTypes, String poNumber, String model, String warehouseId) {
        List<Criteria> filters = new ArrayList<>(List.of(
                Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE.getValue()),
                Criteria.where("itemType").in(itemTypes),
                Criteria.where("deletedAt").isNull(),
                Criteria.where("poNumber").regex(poNumber, "i") // giống like '%%'
        ));
        if (model != null) filters.add(Criteria.where("model").is(model));
        if (warehouseId != null) filters.add(Criteria.where("warehouseId").is(new ObjectId(warehouseId)));
        if (warehouseType != null) filters.add(Criteria.where("warehouse.type").is(warehouseType));
        List<AggregationOperation> aggOps = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(filters)),
                Aggregation.group("poNumber"),
                Aggregation.project().and("_id").as("poNumber")
                        .andExclude("_id")
        );
        AggregationResults<InventoryPoWarehouseDto> aggResults = mongoTemplate.aggregate(Aggregation.newAggregation(aggOps), InventoryItem.class, InventoryPoWarehouseDto.class);
        return aggResults.getMappedResults();

    }

    @Override
    public List<InventoryItemPoNumberDto> findInventoryInStockByPoNumber(String warehouseType, String warehouseId, String poNumber, String filter) {
        List<Criteria> filters = new ArrayList<>(List.of(
                Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE.getValue()),
                Criteria.where("warehouse.deletedAt").isNull(),
                Criteria.where("deletedAt").isNull(),
                Criteria.where("poNumber").is(poNumber)
        ));
        if (warehouseType != null) filters.add(Criteria.where("warehouse.type").is(warehouseType));
        if (warehouseId != null) filters.add(Criteria.where("warehouseId").is(new ObjectId(warehouseId)));
        List<AggregationOperation> aggOps = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(filters))
        ));
        if (filter != null && !filter.isBlank()) {
            Criteria filterCriteria = MongoRsqlUtils.RsqlParser.parse(filter, Map.of());
            aggOps.add(Aggregation.match(filterCriteria));
        }
        AggregationResults<InventoryItemPoNumberDto> aggResults = mongoTemplate.aggregate(Aggregation.newAggregation(aggOps), InventoryItem.class, InventoryItemPoNumberDto.class);
        return aggResults.getMappedResults();

    }

    @Transactional
    @Override
    public List<InventoryItem> bulkInsert(Collection<InventoryItem> inventoryItems) {
        if (inventoryItems.isEmpty()) return new ArrayList<>();
        return mongoTemplate.insertAll(inventoryItems).stream().toList();
    }

    @Transactional
    @Override
    public void bulkUpdateTransfer(Collection<InventoryItem> inventoryItems) {
        if (inventoryItems.isEmpty()) return;
        MongoCollection<Document> coll = mongoTemplate.getCollection(mongoTemplate.getCollectionName(InventoryItem.class));
        List<WriteModel<Document>> writeModels = new ArrayList<>();
        for (var item : inventoryItems) {
            Bson filter = Filters.eq("_id", item.getId());
            Bson update = Updates.combine(
                    Updates.set("quantity", item.getQuantity()),
                    Updates.set("warehouseId", item.getWarehouseId()),
                    Updates.set("status", item.getStatus().getId()),
                    Updates.set("containerId", item.getContainerId()),
                    Updates.set("logistics.departureDate", item.getLogistics().getDepartureDate()),
                    Updates.set("logistics.arrivalDate", item.getLogistics().getArrivalDate()),
                    Updates.set("logistics.consignmentDate", item.getLogistics().getConsignmentDate())
            );
            writeModels.add(new UpdateOneModel<>(filter, update));
        }
        coll.bulkWrite(writeModels);
    }

    @Transactional
    @Override
    public void bulkUpdateStatusAndQuantity(Collection<InventoryItem> inventoryItems) {
        if (inventoryItems.isEmpty()) return;
        MongoCollection<Document> coll = mongoTemplate.getCollection(mongoTemplate.getCollectionName(InventoryItem.class));
        List<WriteModel<Document>> writeModels = new ArrayList<>();
        for (var item : inventoryItems) {
            Bson filter = Filters.eq("_id", item.getId());
            Bson update = Updates.combine(
                    Updates.set("quantity", item.getQuantity()),
                    Updates.set("status", item.getStatus().getId())
            );
            writeModels.add(new UpdateOneModel<>(filter, update));
        }
        coll.bulkWrite(writeModels);
    }

    @Transactional
    @Override
    public void updateStatusAndUnRefContainer(Collection<ObjectId> ids, String status) {
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update().set("status", status).set("containerId", null);
        mongoTemplate.updateMulti(query, update, InventoryItem.class);
    }

    @Transactional
    @Override
    public void updateStatusAndWarehouseAndUnRefContainer(Collection<ObjectId> ids, ObjectId warehouseId, String status) {
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update().set("status", status).set("containerId", null).set("warehouseId", warehouseId);
        mongoTemplate.updateMulti(query, update, InventoryItem.class);
    }

    @Override
    public void updateStatusByIdIn(Collection<ObjectId> ids, String status) {
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update().set("status", status);
        mongoTemplate.updateMulti(query, update, InventoryItem.class);
    }

    @Override
    public void updateStatusAndWarehouseByIdIn(Collection<ObjectId> ids, ObjectId warehouseId, String status) {
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update().set("status", status).set("warehouseId", warehouseId);
        mongoTemplate.updateMulti(query, update, InventoryItem.class);
    }

    @Transactional
    @Override
    public long softDelete(ObjectId id, ObjectId deletedBy) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("deletedAt", LocalDateTime.now())
                .set("deletedBy", deletedBy);
        UpdateResult result = mongoTemplate.updateFirst(query, update, InventoryItem.class);
        return result.getModifiedCount();
    }

    @Transactional
    @Override
    public long bulkSoftDelete(Collection<ObjectId> ids, ObjectId deletedBy) {
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update().set("deletedAt", LocalDateTime.now())
                .set("deletedBy", deletedBy);
        UpdateResult result = mongoTemplate.updateMulti(query, update, InventoryItem.class);
        return result.getModifiedCount();
    }

    @Override
    public long bulkHardDelete(Collection<ObjectId> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        DeleteResult delete = mongoTemplate.remove(query, InventoryItem.class);
        return delete.getDeletedCount();
    }

    @Override
    public List<InventoryItemModelDto> findAllModelsAndItems(List<String> itemTypes, List<ObjectId> warehouseIds, String model) {
        List<String> statusIns = new ArrayList<>(List.of(InventoryItemStatus.IN_STOCK.getId()));
        if (!itemTypes.contains(ItemType.SPARE_PART.getId())) {
            statusIns.add(InventoryItemStatus.IN_TRANSIT.getId());
        }
        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("itemType").in(itemTypes),
                        Criteria.where("status").in(statusIns),
                        Criteria.where("model").regex(model, "i"),
                        Criteria.where("warehouseId").in(warehouseIds),
                        Criteria.where("deletedAt").isNull()
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse", true)
        ));
        if (itemTypes.contains(ItemType.SPARE_PART.getId())) {
            pipelines.add(Aggregation.match(Criteria.where("warehouse.type").ne(WarehouseType.DEPARTURE.getId())));
        }
        ProjectionOperation projection = Aggregation.project("warehouseId", "model", "productCode", "commodityCode", "quantity", "specifications", "warehouse.type")
                .and("_id").as("inventoryItemId")
                .and("warehouse.type").as("warehouseType");
        pipelines.add(projection);
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        AggregationResults<InventoryItemModelDto> aggResults = mongoTemplate.aggregate(aggregation, InventoryItem.class, InventoryItemModelDto.class);
        return aggResults.getMappedResults();
    }

    @Override
    public Page<ReportInventoryDto> findPageReportInventoryToDashBoard(ReportParamsDto params) {

        GroupOperation group = Aggregation.group("poNumber", "model", "createdAt")
                .first("pricing.agent").as("agent")
                .sum(
                        ConditionalOperators.when(Criteria.where("itemType").is(ItemType.VEHICLE.getId()))
                                .thenValueOf("quantity")
                                .otherwise(0)
                ).as("totalVehicle")
                .sum(
                        ConditionalOperators.when(Criteria.where("itemType").is(ItemType.ACCESSORY.getId()))
                                .thenValueOf("quantity")
                                .otherwise(0)
                ).as("totalAccessory")
                .sum(
                        ConditionalOperators.when(Criteria.where("itemType").is(ItemType.SPARE_PART.getId()))
                                .thenValueOf("quantity")
                                .otherwise(0)
                ).as("totalSparePart");

        ProjectionOperation project = Aggregation.project("agent", "totalVehicle", "totalAccessory", "totalSparePart")
                .and("_id.poNumber").as("poNumber")
                .and("_id.model").as("model")
                .and("_id.createdAt").as("loadToWarehouseDate");

        List<AggregationOperation> lookups = new ArrayList<>();

        List<Criteria> filter = new ArrayList<>(List.of(Criteria.where("deletedAt").isNull()));

        if ("CONTAINER".equals(params.getTypeReport())) {
            lookups.add(Aggregation.lookup("container", "containerId", "_id", "container"));
            lookups.add(Aggregation.unwind("container"));
            ArithmeticOperators.Subtract dayLateOperatorsSubtract = ArithmeticOperators.Subtract.valueOf("$$NOW").subtract("container.arrivalDate");
            group = group.first("container.containerCode").as("containerCode")
                    .first("container.containerStatus").as("containerStatus")
                    .first("container.departureDate").as("departureDate")
                    .first("container.arrivalDate").as("arrivalDate")
                    .first(
                            ConditionalOperators.when(Criteria.where("container.arrivalDate").ne(null))
                                    .then(
                                            ConditionalOperators.when(ComparisonOperators.Gt.valueOf(dayLateOperatorsSubtract).greaterThanValue(0))
                                                    .then(ArithmeticOperators.Divide.valueOf(dayLateOperatorsSubtract).divideBy(TimeUnit.DAYS.toMillis(1)))
                                                    .otherwise(0)
                                    )
                                    .otherwise(0)
                    ).as("daysLate");
            project = project.andInclude("containerCode", "containerStatus", "departureDate", "arrivalDate", "daysLate");
        } else {
            lookups.add(Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"));
            lookups.add(Aggregation.unwind("warehouse"));
            filter.add(Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()));
        }

        List<AggregationOperation> pipelines = new ArrayList<>();
        pipelines.add(Aggregation.match(new Criteria().andOperator(filter)));
        pipelines.addAll(lookups);
        if ("CONTAINER".equals(params.getTypeReport()))
            pipelines.add(Aggregation.match(new Criteria().andOperator(
                    Criteria.where("container.containerStatus").is(ContainerStatus.IN_TRANSIT.getId()),
                    Criteria.where("container.deletedAt").isNull()
            )));
        else {
            WarehouseType typeReport = WarehouseType.fromId(params.getTypeReport());
            if (typeReport == null) throw LogicErrException.of("Loại kho hàng cần báo cáo không hợp lệ.");
            pipelines.add(Aggregation.match(new Criteria().andOperator(
                    Criteria.where("warehouse.type").is(typeReport.getId()),
                    Criteria.where("warehouse.deletedAt").isNull(),
                    Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE.getValue())
            )));
        }
        pipelines.add(group);
        pipelines.add(project);
        Aggregation agg = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, ReportInventoryDto.class, agg, params);
    }

    public List<InventoryProductDetailsDto> findProductsByWarehouseId(ObjectId warehouseId, String poNumber) {
        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").in(InventoryItemStatus.IN_STOCK.getId(), InventoryItemStatus.HOLD.getId()),
                        Criteria.where("itemType").in(ItemType.VEHICLE.getId(), ItemType.ACCESSORY.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("poNumber").regex(poNumber, "i")
                )),
                Aggregation.project("model", "category", "serialNumber", "productCode", "poNumber", "itemType", "initialCondition", "notes", "specifications", "pricing", "logistics")
                        .and("_id").as("id")
        ));
        Aggregation agg = Aggregation.newAggregation(pipelines);
        return mongoTemplate.aggregate(agg, InventoryItem.class, InventoryProductDetailsDto.class).getMappedResults();
    }

    public List<InventorySparePartDetailsDto> findSparePartByWarehouseId(ObjectId warehouseId, String poNumber) {
        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").in(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("itemType").in(ItemType.SPARE_PART.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("poNumber").regex(poNumber, "i")
                )),
                Aggregation.project("commodityCode", "poNumber", "quantity", "orderDate", "description", "itemType", "notes", "contractNumber", "pricing")
                        .and("_id").as("id")
        ));
        Aggregation agg = Aggregation.newAggregation(pipelines);
        return mongoTemplate.aggregate(agg, InventoryItem.class, InventorySparePartDetailsDto.class).getMappedResults();
    }

    public List<InventoryProductDetailsDto> findVehicles(PageOptionsDto options) {
        List<AggregationOperation> pipelines = new ArrayList<>();

        // Base pipeline
        pipelines.add(Aggregation.match(
                Criteria.where("deletedAt").isNull()
        ));
        pipelines.add(Aggregation.match(
                Criteria.where("itemType").is(ItemType.VEHICLE.getId()) //
        ));

        Aggregation agg = Aggregation.newAggregation(pipelines);

        // Map filterable fields (client filter → real field in DB)
        Map<String, String> mapper = Map.of(
                "warehouseId", "warehouseId",
                "status", "status",
                "poNumber", "poNumber"
        );
        return MongoRsqlUtils.queryAggregateList(
                InventoryItem.class,
                InventoryProductDetailsDto.class,
                agg,
                mapper,
                options
        );
    }

}