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
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.inventory_item.CustomInventoryItemRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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


    @Override
    public Page<InventoryDestinationDto> findPageInventoryDestination(ObjectId warehouseId, PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId())
                )),
                Aggregation.project("poNumber", "status", "productCode", "manufacturingYear", "model", "inventoryType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("specifications.hasSideShift").as("hasSideShift") //
                        .and("specifications.liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("specifications.chassisType").as("chassisType")    //
                        .and("specifications.liftingHeightMm").as("liftingHeightMm")    //
                        .and("specifications.engineType").as("engineType")  //
                        .and("specifications.batteryInfo").as("batteryInfo")    //
                        .and("specifications.batterySpecification").as("batterySpecification")  //
                        .and("specifications.chargerSpecification").as("chargerSpecification")  //
                        .and("specifications.forkDimensions").as("forkDimensions")  //
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
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
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId())
                )),
                Aggregation.project("poNumber", "productCode", "model", "inventoryType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate")
                        .and("specifications.hasSideShift").as("hasSideShift")
                        .and("specifications.liftingCapacityKg").as("liftingCapacityKg")
                        .and("specifications.chassisType").as("chassisType")
                        .and("specifications.liftingHeightMm").as("liftingHeightMm")
                        .and("specifications.engineType").as("engineType")
                        .and("specifications.batteryInfo").as("batteryInfo")
                        .and("specifications.batterySpecification").as("batterySpecification")
                        .and("specifications.chargerSpecification").as("chargerSpecification")
                        .and("specifications.forkDimensions").as("forkDimensions")
                        .and("specifications.valveCount").as("valveCount")
                        .and("specifications.otherDetails").as("otherDetails")
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
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").in(InventoryItemStatus.IN_STOCK.getId(), InventoryItemStatus.OTHER.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").in(InventoryType.ACCESSORY.getId(), InventoryType.VEHICLE.getId())
                )),
                Aggregation.project("poNumber", "status", "productCode", "manufacturingYear", "model", "inventoryType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("logistics.arrivalDate").as("arrivalDate") //
                        .and("specifications.hasSideShift").as("hasSideShift")  //
                        .and("specifications.liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("specifications.chassisType").as("chassisType")    //
                        .and("specifications.liftingHeightMm").as("liftingHeightMm")    //
                        .and("specifications.engineType").as("engineType")  //
                        .and("specifications.batteryInfo").as("batteryInfo")    //
                        .and("specifications.batterySpecification").as("batterySpecification")  //
                        .and("specifications.chargerSpecification").as("chargerSpecification")  //
                        .and("specifications.forkDimensions").as("forkDimensions")  //
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
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
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").in(InventoryType.ACCESSORY.getId(), InventoryType.VEHICLE.getId())
                )),
                Aggregation.project("poNumber", "status", "productCode", "manufacturingYear", "model", "inventoryType", "category", "serialNumber", "notes", "initialCondition", "warehouseType")
                        .and("_id").as("id")
                        .and("specifications.hasSideShift").as("hasSideShift") //
                        .and("specifications.liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("specifications.chassisType").as("chassisType")    //
                        .and("specifications.liftingHeightMm").as("liftingHeightMm")    //
                        .and("specifications.engineType").as("engineType")  //
                        .and("specifications.batteryInfo").as("batteryInfo")    //
                        .and("specifications.batterySpecification").as("batterySpecification")  //
                        .and("specifications.chargerSpecification").as("chargerSpecification")  //
                        .and("specifications.forkDimensions").as("forkDimensions")  //
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
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
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
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
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
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
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
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
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
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
    public Page<InventoryCentralWarehouseProductDto> findPageInventoryCentralWarehouse(PageOptionsDto optionsReq){
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId())
                )),
                Aggregation.project("poNumber", "productCode", "status", "model", "category", "inventoryType", "serialNumber", "manufacturingYear", "initialCondition", "notes", "warehouseType")
                        .and("_id").as("id")
                        .and("specifications.liftingCapacityKg").as("liftingCapacityKg")    //
                        .and("specifications.chassisType").as("chassisType")    //
                        .and("specifications.liftingHeightMm").as("liftingHeightMm")    //
                        .and("specifications.engineType").as("engineType")  //
                        .and("specifications.batteryInfo").as("batteryInfo")    //
                        .and("specifications.batterySpecification").as("batterySpecification")  //
                        .and("specifications.chargerSpecification").as("chargerSpecification")  //
                        .and("specifications.forkDimensions").as("forkDimensions")  //
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.hasSideShift").as("hasSideShift")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.agent").as("agent")   //
                        .and("logistics.arrivalDate").as("arrivalDate") //
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryCentralWarehouseProductDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryCentralWarehouseSparePartDto> findPageInventoryCentralWarehouseSparePart(PageOptionsDto optionsReq){
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "commodityCode", "status", "model", "quantity", "description", "notes", "contractNumber", "warehouseType")
                        .and("_id").as("id")
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.agent").as("agent")   //
                        .and("logistics.arrivalDate").as("arrivalDate") //
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryCentralWarehouseSparePartDto.class, aggregation, optionsReq);
    }

    @Override
    public List<InventoryPoWarehouseDto> findPoNumbersOfInventoryInStock(String warehouseType, List<String> inventoryTypes, String poNumber, String warehouseId){
        List<Criteria> filters = new ArrayList<>(List.of(
                Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                Criteria.where("deletedAt").isNull(),
                Criteria.where("inventoryType").in(inventoryTypes),
                Criteria.where("poNumber").regex(poNumber, "i") // giống like '%%'
        ));
        if(warehouseId != null) filters.add(Criteria.where("warehouseId").is(new ObjectId(warehouseId)));
        if(warehouseType != null) filters.add(Criteria.where("warehouse.type").is(warehouseType));
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
                Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                Criteria.where("deletedAt").isNull(),
                Criteria.where("poNumber").is(poNumber)
        ));
        if(warehouseType != null) filters.add(Criteria.where("warehouse.type").is(warehouseType));
        if(warehouseId != null) filters.add(Criteria.where("warehouseId").is(new ObjectId(warehouseId)));
        List<AggregationOperation> aggOps = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(filters))
        ));
        if(filter != null && !filter.isBlank()){
            Criteria filterCriteria = MongoRsqlUtils.RsqlParser.parse(filter, Map.of());
            aggOps.add(Aggregation.match(filterCriteria));
        }
        AggregationResults<InventoryItemPoNumberDto> aggResults = mongoTemplate.aggregate(Aggregation.newAggregation(aggOps), InventoryItem.class, InventoryItemPoNumberDto.class);
        return aggResults.getMappedResults();

    }

    @Transactional
    @Override
    public List<InventoryItem> insertAll(Collection<InventoryItem> inventoryItems){
        if(inventoryItems.isEmpty()) return new ArrayList<>();
        return mongoTemplate.insertAll(inventoryItems).stream().toList();
    }

    @Transactional
    @Override
    public void bulkUpdateTransfer(Collection<InventoryItem> inventoryItems){
        if(inventoryItems.isEmpty()) return;
        MongoCollection<Document> coll = mongoTemplate.getCollection(mongoTemplate.getCollectionName(InventoryItem.class));
        List<WriteModel<Document>> writeModels = new ArrayList<>();
        for(var item : inventoryItems){
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
    public List<InventoryProductDetailsDto> findInventoryProductDetailsInIds(ObjectId ids) {
        List<AggregationOperation> pipelines = List.of(
            Aggregation.match(new Criteria().andOperator(
                    Criteria.where("containerId").is(ids),
                    Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId())
            )),
            Aggregation.project("model", "category", "serialNumber", "productCode", "poNumber", "inventoryType", "initialCondition", "notes", "specifications", "pricing", "logistics")
                    .and("_id").as("id")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        AggregationResults<InventoryProductDetailsDto> aggResults = mongoTemplate.aggregate(aggregation, InventoryItem.class, InventoryProductDetailsDto.class);
        return aggResults.getMappedResults();
    }

    @Override
    public List<InventorySparePartDetailsDto> findInventorySparePartDetailsInIds(ObjectId ids) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("containerId").is(ids),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.project("commodityCode", "poNumber", "quantity", "orderDate", "description", "inventoryType", "notes", "contractNumber", "pricing")
                        .and("_id").as("id")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        AggregationResults<InventorySparePartDetailsDto> aggResults = mongoTemplate.aggregate(aggregation, InventoryItem.class, InventorySparePartDetailsDto.class);
        return aggResults.getMappedResults();
    }
}