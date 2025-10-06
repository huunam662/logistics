package warehouse_management.com.warehouse_management.repository.inventory_item.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import cz.jirutka.rsql.parser.RSQLParser;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.request.ReportParamsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.response.ReportInventoryDto;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.inventory_item.CustomInventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class CustomInventoryItemRepositoryImpl implements CustomInventoryItemRepository {

    private final MongoTemplate mongoTemplate;
    private final WarehouseRepository warehouseRepository;
    private final InventoryItemRepository inventoryItemRepository;

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
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("vehicleId").isNull(),
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
                        .and("specifications.wheelInfo").as("wheelInfo")
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId()),
                        Criteria.where("vehicleId").isNull()
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
                        .and("specifications.wheelInfo").as("wheelInfo")
                        .and("specifications.valveCount").as("valveCount")
                        .and("specifications.otherDetails").as("otherDetails")
                        .and("pricing.purchasePrice").as("purchasePrice")
                        .and("pricing.salePriceR0").as("salePriceR0")
                        .and("pricing.salePriceR1").as("salePriceR1")
                        .and("pricing.actualSalePrice").as("actualSalePrice")
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("vehicleId").isNull(),
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
                        .and("specifications.wheelInfo").as("wheelInfo")
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("vehicleId").isNull(),
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
                        .and("specifications.wheelInfo").as("wheelInfo")
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "model", "commodityCode", "quantity", "description", "notes", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "model", "commodityCode", "notes", "quantity", "description", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "model", "commodityCode", "notes", "quantity", "description", "warehouseType")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate") //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
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
                        .and("pricing.otherPrice").as("otherPrice")

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
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId()),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId())
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
                        .and("specifications.wheelInfo").as("wheelInfo")
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.hasSideShift").as("hasSideShift")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
                        .and("pricing.agent").as("agent")   //
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryCentralWarehouseProductDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryCentralWarehouseProductDto> findPageInventoryCentralWarehouseConsignment(PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouse.type").is(WarehouseType.CONSIGNMENT.getId()),
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId()),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId())
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
                        .and("specifications.wheelInfo").as("wheelInfo")
                        .and("specifications.valveCount").as("valveCount")  //
                        .and("specifications.hasSideShift").as("hasSideShift")  //
                        .and("specifications.otherDetails").as("otherDetails")  //
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
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
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("warehouse.type").is(WarehouseType.DESTINATION.getId()),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "commodityCode", "status", "model", "quantity", "description", "notes", "contractNumber", "warehouseType")
                        .and("_id").as("id")
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
                        .and("pricing.agent").as("agent")   //
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryCentralWarehouseSparePartDto.class, aggregation, optionsReq);
    }

    @Override
    public Page<InventoryCentralWarehouseSparePartDto> findPageInventoryCentralWarehouseConsignmentSparePart(PageOptionsDto optionsReq) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("warehouse.type").is(WarehouseType.CONSIGNMENT.getId()),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.project("poNumber", "commodityCode", "status", "model", "quantity", "description", "notes", "contractNumber", "warehouseType")
                        .and("_id").as("id")
                        .and("pricing.purchasePrice").as("purchasePrice")   //
                        .and("pricing.salePriceR0").as("salePriceR0")   //
                        .and("pricing.salePriceR1").as("salePriceR1")   //
                        .and("pricing.actualSalePrice").as("actualSalePrice")   //
                        .and("pricing.otherPrice").as("otherPrice")
                        .and("pricing.agent").as("agent")   //
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryCentralWarehouseSparePartDto.class, aggregation, optionsReq);
    }

    @Override
    public List<InventoryPoWarehouseDto> findPoNumbersOfInventoryInStock(String warehouseType, List<String> inventoryTypes, String model, String warehouseId) {
        List<Criteria> filters = new ArrayList<>(List.of(
                Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE.getValue()),
                Criteria.where("inventoryType").in(inventoryTypes),
                Criteria.where("deletedAt").isNull(),
                Criteria.where("containerId").isNull(),
                Criteria.where("vehicleId").isNull()
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
                Criteria.where("poNumber").is(poNumber),
                Criteria.where("containerId").isNull(),
                Criteria.where("vehicleId").isNull()
        ));
        if (warehouseType != null) filters.add(Criteria.where("warehouse.type").is(warehouseType));
        if (warehouseId != null) filters.add(Criteria.where("warehouseId").is(new ObjectId(warehouseId)));
        List<AggregationOperation> aggOps = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(filters))
        ));
        if (filter != null && !filter.isBlank()) {
            Criteria filterCriteria = MongoRsqlUtils.parse(filter);
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

        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, InventoryItem.class);

        List<WriteModel<Document>> writeModels = new ArrayList<>();
        for (var item : inventoryItems) {

            Query query = new Query(Criteria.where("_id").is(item.getId()));

            Update update = new Update()
                    .set("quantity", item.getQuantity())
                    .set("status", item.getStatus().getId())
                    .set("warehouseId", item.getWarehouseId())
                    .set("containerId", item.getContainerId())
                    .set("logistics.departureDate", item.getLogistics().getDepartureDate())
                    .set("logistics.arrivalDate", item.getLogistics().getArrivalDate())
                    .set("logistics.consignmentDate", item.getLogistics().getConsignmentDate());

            ops.updateOne(query, update);
        }

        ops.execute();
    }

    @Transactional
    @Override
    public void bulkUpdateStatusAndQuantity(Collection<InventoryItem> inventoryItems) {
        if (inventoryItems.isEmpty()) return;

        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, InventoryItem.class);

        for (var item : inventoryItems) {

            Query query = new Query(Criteria.where("_id").is(item.getId()));

            Update update = new Update()
                    .set("quantity", item.getQuantity())
                    .set("status", item.getStatus().getId());

            ops.updateOne(query, update);
        }

        ops.execute();
    }

    @Transactional
    @Override
    public void bulkUpdateSpecAndPricing(Collection<InventoryItem> inventoryItems) {
        if (inventoryItems.isEmpty()) return;

        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, InventoryItem.class);

        for (var item : inventoryItems) {

            Query query = new Query(Criteria.where("_id").is(item.getId()));

            Update update = new Update()
                    .set("specifications", item.getSpecifications())
                    .set("pricing", item.getPricing());

            ops.updateOne(query, update);
        }

        ops.execute();
    }

    @Transactional
    @Override
    public void bulkUpdateComponentSerial(Collection<InventoryItem> inventoryItems) {
        if (inventoryItems.isEmpty()) return;

        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, InventoryItem.class);

        for (var item : inventoryItems) {

            Query query = new Query(Criteria.where("_id").is(item.getId()));

            Update update = new Update();

            if(InventoryType.SPARE_PART.getId().equals(item.getInventoryType())) {
                update = update.set("commodityCode", item.getCommodityCode());
            }
            else update = update.set("serialNumber", item.getSerialNumber());

            ops.updateOne(query, update);
        }

        ops.execute();
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

    @Transactional
    @Override
    public long bulkHardDelete(Collection<ObjectId> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        DeleteResult delete = mongoTemplate.remove(query, InventoryItem.class);
        return delete.getDeletedCount();
    }

    @Transactional
    @Override
    public void updateIsFullyComponent(ObjectId vehicleId, Boolean isFullyComponent) {
        Query query = new Query(Criteria.where("_id").is(vehicleId));
        Update update = new Update().set("isFullyComponent", isFullyComponent);
        mongoTemplate.updateFirst(query, update, InventoryItem.class);
    }

    @Override
    public List<InventoryItemModelDto> findAllModelsAndItems(List<String> inventoryTypes, List<ObjectId> warehouseIds, String filter) {
        List<String> statusIns = new ArrayList<>(List.of(InventoryItemStatus.IN_STOCK.getId()));
        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("inventoryType").in(inventoryTypes),
                        Criteria.where("status").in(statusIns),
                        Criteria.where("warehouseId").in(warehouseIds),
//                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("deletedAt").isNull()
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse", true)
        ));
        if (inventoryTypes.contains(InventoryType.SPARE_PART.getId())) {
//            pipelines.add(Aggregation.match(Criteria.where("warehouse.type").ne(WarehouseType.DEPARTURE.getId())));
        }
        else{
            statusIns.add(InventoryItemStatus.IN_TRANSIT.getId());
        }
        ProjectionOperation projection = Aggregation.project("warehouseId", "model", "productCode", "commodityCode", "quantity", "specifications", "pricing", "warehouse.type")
                .and("_id").as("inventoryItemId")
                .and("warehouse.type").as("warehouseType");
        pipelines.add(projection);

        if(filter != null && !filter.isBlank()) {
            Criteria filterCriteria = MongoRsqlUtils.parse(filter);
            pipelines.add(Aggregation.match(filterCriteria));
        }
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        AggregationResults<InventoryItemModelDto> aggResults = mongoTemplate.aggregate(aggregation, InventoryItem.class, InventoryItemModelDto.class);
        return aggResults.getMappedResults();
    }

    @Override
    public Page<ReportInventoryDto> findPageReportItemProductionConsignmentToDashBoard(ReportParamsDto params){

        WarehouseType typeReport = WarehouseType.fromId(params.getTypeReport());
        if (typeReport == null) throw LogicErrException.of("Loại kho hàng cần báo cáo không hợp lệ.");

        Aggregation aggregation = Aggregation.newAggregation(

                Aggregation.addFields()
                        .addField("createdAtToDate")
                        .withValue(
                                DateOperators.DateFromParts
                                        .dateFromParts()
                                        .year(DateOperators.Year.yearOf("createdAt"))
                                        .month(DateOperators.Month.monthOf("createdAt"))
                                        .day(DateOperators.DayOfMonth.dayOfMonth("createdAt"))
                        ).build(),

                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId())
                )),

                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),

                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.type").is(typeReport.getId()),
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouse.status").is(WarehouseStatus.ACTIVE.getValue())
                )),

                Aggregation.group("poNumber", "model", "inventoryType")
                        .first("createdAtToDate").as("loadToWarehouseDate")
                        .first("pricing.agent").as("agent")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryType").is(InventoryType.VEHICLE.getId()))
                                        .thenValueOf("quantity")
                                        .otherwise(0)
                        ).as("totalVehicle")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryType").is(InventoryType.ACCESSORY.getId()))
                                        .thenValueOf("quantity")
                                        .otherwise(0)
                        ).as("totalAccessory")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId()))
                                        .thenValueOf("quantity")
                                        .otherwise(0)
                        ).as("totalSparePart")
                        .first("warehouse.type").as("reportType"),

                Aggregation.project("poNumber", "model", "agent", "totalVehicle", "totalAccessory", "totalSparePart", "inventoryType", "loadToWarehouseDate")
                            .andInclude("reportType")
        );

        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, ReportInventoryDto.class, aggregation, params);
    }

    @Override
    public Page<ReportInventoryDto> findPageReportItemInTransitContainerToDashBoard(ReportParamsDto params){

        AggregationExpression nowDay = DateOperators.DateFromParts.dateFromParts()
                .year(DateOperators.Year.yearOf("$$NOW"))
                .month(DateOperators.Month.monthOf("$$NOW"))
                .day(DateOperators.DayOfMonth.dayOfMonth("$$NOW"));

        AggregationExpression arrivalDay = DateOperators.DateFromParts.dateFromParts()
                .year(DateOperators.Year.yearOf("container.arrivalDate"))
                .month(DateOperators.Month.monthOf("container.arrivalDate"))
                .day(DateOperators.DayOfMonth.dayOfMonth("container.arrivalDate"));

        ArithmeticOperators.Subtract dayLateOperatorsSubtract = ArithmeticOperators.Subtract.valueOf(nowDay).subtract(arrivalDay);

        Aggregation aggregation = Aggregation.newAggregation(

                Aggregation.addFields()
                        .addField("createdAtToDate")
                        .withValue(
                                DateOperators.DateFromParts
                                        .dateFromParts()
                                        .year(DateOperators.Year.yearOf("createdAt"))
                                        .month(DateOperators.Month.monthOf("createdAt"))
                                        .day(DateOperators.DayOfMonth.dayOfMonth("createdAt"))
                        ).build(),

                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("vehicleId").isNull()
                )),

                Aggregation.lookup("container", "containerId", "_id", "container"),
                Aggregation.unwind("container"),

                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("container.containerStatus").is(ContainerStatus.IN_TRANSIT.getId()),
                        Criteria.where("container.deletedAt").isNull()
                )),

                Aggregation.addFields()
                        .addField("departureToDate")
                        .withValue(
                                DateOperators.DateFromParts
                                        .dateFromParts()
                                        .year(DateOperators.Year.yearOf("container.departureDate"))
                                        .month(DateOperators.Month.monthOf("container.departureDate"))
                                        .day(DateOperators.DayOfMonth.dayOfMonth("container.departureDate"))
                        ).build(),

                Aggregation.addFields()
                        .addField("arrivalToDate")
                        .withValue(
                                DateOperators.DateFromParts
                                        .dateFromParts()
                                        .year(DateOperators.Year.yearOf("container.arrivalDate"))
                                        .month(DateOperators.Month.monthOf("container.arrivalDate"))
                                        .day(DateOperators.DayOfMonth.dayOfMonth("container.arrivalDate"))
                        ).build(),

                Aggregation.group("poNumber", "model", "inventoryType")
                        .first("createdAtToDate").as("loadToWarehouseDate")
                        .first("pricing.agent").as("agent")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryType").is(InventoryType.VEHICLE.getId()))
                                        .thenValueOf("quantity")
                                        .otherwise(0)
                        ).as("totalVehicle")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryType").is(InventoryType.ACCESSORY.getId()))
                                        .thenValueOf("quantity")
                                        .otherwise(0)
                        ).as("totalAccessory")
                        .sum(
                                ConditionalOperators.when(Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId()))
                                        .thenValueOf("quantity")
                                        .otherwise(0)
                        ).as("totalSparePart")
                        .first("departureToDate").as("departureDate")
                        .first("arrivalToDate").as("arrivalDate")
                        .first("container.containerCode").as("containerCode")
                        .first("container.containerStatus").as("containerStatus")
                        .first(
                                ConditionalOperators.when(Criteria.where("container.arrivalDate").ne(null))
                                        .then(
                                                ConditionalOperators.when(ComparisonOperators.Gt.valueOf(dayLateOperatorsSubtract).greaterThanValue(0))
                                                        .then(ArithmeticOperators.Divide.valueOf(dayLateOperatorsSubtract).divideBy(TimeUnit.DAYS.toMillis(1)))
                                                        .otherwise(0)
                                        )
                                        .otherwise(0)
                        ).as("daysLate"),

                Aggregation.project("poNumber", "model", "agent", "totalVehicle", "totalAccessory", "totalSparePart",
                        "inventoryType", "loadToWarehouseDate", "containerCode", "containerStatus", "departureDate", "arrivalDate", "daysLate"
                ).andExpression("'" + params.getTypeReport() + "'").as("reportType")
        );

        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, ReportInventoryDto.class, aggregation, params);
    }

    public List<InventoryProductDetailsDto> findProductsByWarehouseId(ObjectId warehouseId, String filter) {

        List<String> statusIn = new ArrayList<>();
        statusIn.add(InventoryItemStatus.IN_STOCK.getId());

        String warehouseType = warehouseRepository.findTypeById(warehouseId);

        if(warehouseType != null && warehouseType.equals(WarehouseType.DEPARTURE.getId()))
            statusIn.add(InventoryItemStatus.HOLD.getId());

        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").in(statusIn),
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId()),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("containerId").isNull()
                )),
                Aggregation.project("model", "category", "serialNumber", "productCode", "poNumber", "inventoryType", "initialCondition", "warehouseId", "notes", "specifications", "pricing", "logistics")
                        .and("_id").as("id")
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        ));

        if(filter != null && !filter.isBlank()) {
            Criteria filterCriteria = MongoRsqlUtils.parse(filter);
            pipelines.add(Aggregation.match(filterCriteria));
        }

        Aggregation agg = Aggregation.newAggregation(pipelines);
        return mongoTemplate.aggregate(agg, InventoryItem.class, InventoryProductDetailsDto.class).getMappedResults();
    }

    public List<InventoryProductDetailsDto> findProductsByWarehouseIdIn(List<ObjectId> warehouseIds, String filter) {

        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").in(warehouseIds),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("inventoryType").in(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId()),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("containerId").isNull()
                )),
                Aggregation.project("model", "category", "serialNumber", "productCode", "poNumber", "inventoryType", "initialCondition", "warehouseId", "notes", "specifications", "pricing", "logistics")
                        .and("_id").as("id")
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        ));

        if(filter != null && !filter.isBlank()) {
            Criteria filterCriteria = MongoRsqlUtils.parse(filter);
            pipelines.add(Aggregation.match(filterCriteria));
        }

        Aggregation agg = Aggregation.newAggregation(pipelines);
        return mongoTemplate.aggregate(agg, InventoryItem.class, InventoryProductDetailsDto.class).getMappedResults();
    }

    public List<InventorySparePartDetailsDto> findSparePartByWarehouseId(ObjectId warehouseId, String filter) {
        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("status").in(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("inventoryType").in(InventoryType.SPARE_PART.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("containerId").isNull()
                )),
                Aggregation.project("commodityCode", "poNumber", "quantity", "description", "inventoryType", "contractNumber", "pricing", "warehouseId", "model", "notes")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate")
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        ));

        if(filter != null && !filter.isBlank()) {
            Criteria filterCriteria = MongoRsqlUtils.parse(filter);
            pipelines.add(Aggregation.match(filterCriteria));
        }

        Aggregation agg = Aggregation.newAggregation(pipelines);
        return mongoTemplate.aggregate(agg, InventoryItem.class, InventorySparePartDetailsDto.class).getMappedResults();
    }

    public List<InventorySparePartDetailsDto> findSparePartByWarehouseIdIn(List<ObjectId> warehouseIds, String filter) {
        List<AggregationOperation> pipelines = new ArrayList<>(List.of(
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").in(warehouseIds),
                        Criteria.where("status").in(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("inventoryType").in(InventoryType.SPARE_PART.getId()),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("vehicleId").isNull(),
                        Criteria.where("containerId").isNull()
                )),
                Aggregation.project("commodityCode", "poNumber", "quantity", "description", "inventoryType", "contractNumber", "pricing", "warehouseId", "model", "notes")
                        .and("_id").as("id")
                        .and("logistics.orderDate").as("orderDate")
                        .and("warehouse.name").as("warehouseName")
                        .and("warehouse.code").as("warehouseCode")
        ));

        if(filter != null && !filter.isBlank()) {
            Criteria filterCriteria = MongoRsqlUtils.parse(filter);
            pipelines.add(Aggregation.match(filterCriteria));
        }

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
                Criteria.where("inventoryType").is(InventoryType.VEHICLE.getId()) //
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

    public Page<InventoryItemWarrantyDto> findItemForWarranty(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = new ArrayList<>();

        // Lấy sản phẩm là loại xe đã bán
        pipelines.add(Aggregation.match(new Criteria()
                .andOperator(
                        Criteria.where("deletedBy").is(null),
                        Criteria.where("inventoryType").is(InventoryType.VEHICLE),
                        Criteria.where("status").is(InventoryItemStatus.SOLD))));

        // Lấy những sản phẩm đang không bảo hành ở warranty hoặc ở trong warranty nhưng đang không bảo hành
        pipelines.add(Aggregation.lookup("warranty", "_id", "warrantyInventoryItem._id", "warranty"));
        pipelines.add(
                Aggregation.match(new Criteria().orOperator(
                        Criteria.where("warranty").size(0),
                        Criteria.where("warranty.status").ne(WarrantyStatus.IN_WARRANTY)
                ))
        );

        pipelines.add(Aggregation.lookup("delivery_order", "_id", "inventoryItems._id", "order"));
        pipelines.add(Aggregation.unwind("order"));
        pipelines.add(Aggregation.match(Criteria.where("order.status").ne(DeliveryOrderStatus.REJECTED)));

        pipelines.add(Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"));
        pipelines.add(Aggregation.unwind("warehouse"));
        pipelines.add(Aggregation.match(Criteria.where("warehouse.type").is(WarehouseType.DESTINATION)));

        pipelines.add(Aggregation.lookup("client", "order.customerId", "_id", "client"));

        pipelines.add(Aggregation.project("serialNumber", "model", "id", "productCode")
                .and("logistics.arrivalDate").as("arrivalDate")
                .and("client.name").as("clientName"));

        Aggregation agg = Aggregation.newAggregation(pipelines);

        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryItemWarrantyDto.class, agg, optionsDto);
    }

    public Page<InventoryItemRepairDto> findItemForRepair(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = new ArrayList<>();

        // Lấy sản phẩm là loại xe đã bán
        pipelines.add(Aggregation.match(new Criteria()
                .andOperator(
                        Criteria.where("deletedBy").is(null),
                        Criteria.where("inventoryType").is(InventoryType.VEHICLE),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK))));

        // Lấy những sản phẩm đang không sửa
        pipelines.add(Aggregation.lookup("repair", "_id", "repairInventoryItem._id", "repair"));
        pipelines.add(
                Aggregation.match(new Criteria().orOperator(
                        Criteria.where("repair").size(0),
                        Criteria.where("repair.status").is(RepairStatus.COMPLETED)
                ))
        );

        pipelines.add(Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"));
        pipelines.add(Aggregation.unwind("warehouse"));
        pipelines.add(Aggregation.match(Criteria.where("warehouse.type").is(WarehouseType.DESTINATION)));

        pipelines.add(Aggregation.project("serialNumber","model", "id", "productCode"));

        Aggregation agg = Aggregation.newAggregation(pipelines);

        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryItemRepairDto.class, agg, optionsDto);
    }

    public Page<ConfigVehicleSpecPageDto> findPageConfigVehicleSpec(PageOptionsDto optionsDto) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("inventoryType").is(InventoryType.VEHICLE.getId()),
                        Criteria.where("status").is(InventoryItemStatus.IN_CONFIG.getId()),
                        Criteria.where("deletedAt").isNull()
                )),

                Aggregation.lookup("inventory_item", "_id", "vehicleId", "components"),

                Aggregation.addFields()
                        .addField("componentsObj")
                        .withValue(
                                ArrayOperators.ArrayToObject.arrayToObject(
                                        VariableOperators.Map.itemsOf("components")
                                                .as("c")
                                                .andApply(
                                                        ArrayOperators.ConcatArrays.arrayOf(
                                                                List.of(
                                                                        ConditionalOperators.ifNull("$$c.componentType").then("UNKNOWN"),
                                                                        "$$c"
                                                                )
                                                        )
                                                )
                                )
                        ).build(),

                Aggregation.lookup("configuration_hist", "_id", "vehicleId", "configurationsHist"),

                Aggregation.addFields()
                        .addField("configurations")
                        .withValue(
                                ArrayOperators.Filter.filter("configurationsHist")
                                        .as("c")
                                        .by(
                                                ComparisonOperators.Eq.valueOf(
                                                        ConditionalOperators.IfNull.ifNull("$$c.performedBy").then("NULL")
                                                ).equalToValue("NULL")
                                        )
                        )
                        .build(),

                Aggregation.addFields()
                        .addField("configurationsObj")
                        .withValue(
                                ArrayOperators.ArrayToObject.arrayToObject(
                                        VariableOperators.Map.itemsOf("configurations")
                                                .as("c")
                                                .andApply(
                                                        ArrayOperators.ConcatArrays.arrayOf(
                                                                List.of(
                                                                        ConditionalOperators.ifNull("$$c.componentType").then("UNKNOWN"),
                                                                        "$$c"
                                                                )
                                                        )
                                                )
                                )
                        ).build(),

                Aggregation.addFields()
                        // Khung nâng
                        .addField("liftingFrame.value")
                        .withValue(
                                StringOperators.Concat.valueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.chassisType")).then(""))
                                        .concat(" - ")
                                        .concatValueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.liftingCapacityKg")).then("0"))
                                        .concat(" Kg - ")
                                        .concatValueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.liftingHeightMm")).then("0"))
                                        .concat(" mm")
                        )
                        .addField("liftingFrame.serialNumber").withValue("$componentsObj.LIFTING_FRAME.serialNumber")
                        .addField("liftingFrame.configStatus").withValue("$configurationsObj.LIFTING_FRAME.status")
                        .addField("liftingFrame.configType").withValue("$configurationsObj.LIFTING_FRAME.configType")
                        .addField("liftingFrame.configComponentType").withValue("$configurationsObj.LIFTING_FRAME.componentType")
                        // Bình điện
                        .addField("battery.value")
                        .withValue(
                                StringOperators.Concat.valueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.batteryInfo")).then(""))
                                        .concat(" - ")
                                        .concatValueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.batterySpecification")).then(""))
                        )
                        .addField("battery.serialNumber").withValue("$componentsObj.BATTERY.serialNumber")
                        .addField("battery.configStatus").withValue("$configurationsObj.BATTERY.status")
                        .addField("battery.configType").withValue("$configurationsObj.BATTERY.configType")
                        .addField("battery.configComponentType").withValue("$configurationsObj.BATTERY.componentType")
                        // Sạc
                        .addField("charger.value").withValue("$specifications.chargerSpecification")
                        .addField("charger.serialNumber").withValue("$componentsObj.CHARGER.serialNumber")
                        .addField("charger.configStatus").withValue("$configurationsObj.CHARGER.status")
                        .addField("charger.configType").withValue("$configurationsObj.CHARGER.configType")
                        .addField("charger.configComponentType").withValue("$configurationsObj.CHARGER.componentType")
                        // Động cơ
                        .addField("engine.value").withValue("$specifications.engineType")
                        .addField("engine.serialNumber").withValue("$componentsObj.ENGINE.commodityCode")
                        .addField("engine.configStatus").withValue("$configurationsObj.ENGINE.status")
                        .addField("engine.configType").withValue("$configurationsObj.ENGINE.configType")
                        .addField("engine.configComponentType").withValue("$configurationsObj.ENGINE.componentType")
                        // Càng nâng
                        .addField("fork.value").withValue("$specifications.forkDimensions")
                        .addField("fork.serialNumber").withValue("$componentsObj.FORK.commodityCode")
                        .addField("fork.configStatus").withValue("$configurationsObj.FORK.status")
                        .addField("fork.configType").withValue("$configurationsObj.FORK.configType")
                        .addField("fork.configComponentType").withValue("$configurationsObj.FORK.componentType")
                        // Van
                        .addField("valve.value").withValue("$specifications.valveCount")
                        .addField("valve.serialNumber").withValue("$componentsObj.VALVE.commodityCode")
                        .addField("valve.configStatus").withValue("$configurationsObj.VALVE.status")
                        .addField("valve.configType").withValue("$configurationsObj.VALVE.configType")
                        .addField("valve.configComponentType").withValue("$configurationsObj.VALVE.componentType")
                        // Side shift
                        .addField("sideShift.value").withValue("$specifications.hasSideShift")
                        .addField("sideShift.serialNumber").withValue("$componentsObj.SIDE_SHIFT.commodityCode")
                        .addField("sideShift.configStatus").withValue("$configurationsObj.SIDE_SHIFT.status")
                        .addField("sideShift.configType").withValue("$configurationsObj.SIDE_SHIFT.configType")
                        .addField("sideShift.configComponentType").withValue("$configurationsObj.SIDE_SHIFT.componentType")
                        // BÁNH XE
                        .addField("wheel.value").withValue("$specifications.wheelInfo")
                        .addField("wheel.serialNumber").withValue("$componentsObj.WHEEL.commodityCode")
                        .addField("wheel.configStatus").withValue("$configurationsObj.WHEEL.status")
                        .addField("wheel.configType").withValue("$configurationsObj.WHEEL.configType")
                        .addField("wheel.configComponentType").withValue("$configurationsObj.WHEEL.componentType")

                        .build(),

                Aggregation.project()
                        .and("_id").as("vehicleId")
                        .andInclude("productCode", "model", "serialNumber", "isFullyComponent", "initialCondition", "liftingFrame", "battery", "charger", "engine", "fork", "valve", "sideShift", "wheel")
                        .andExclude("_id")
        );

        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, ConfigVehicleSpecPageDto.class, aggregation, optionsDto);
    }

    @Override
    public Page<RepairVehicleSpecPageDto> findPageRepairVehicleSpec(PageOptionsDto optionsDto) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("inventoryType").is(InventoryType.VEHICLE.getId()),
                        Criteria.where("status").is(InventoryItemStatus.IN_REPAIR.getId()),
                        Criteria.where("deletedAt").isNull()
                )),

                Aggregation.lookup("inventory_item", "_id", "vehicleId", "components"),

                Aggregation.addFields()
                        .addField("componentsObj")
                        .withValue(
                                ArrayOperators.ArrayToObject.arrayToObject(
                                        VariableOperators.Map.itemsOf("components")
                                                .as("c")
                                                .andApply(
                                                        ArrayOperators.ConcatArrays.arrayOf(
                                                                List.of(
                                                                        ConditionalOperators.ifNull("$$c.componentType").then("UNKNOWN"),
                                                                        "$$c"
                                                                )
                                                        )
                                                )
                                )
                        ).build(),

                Aggregation.lookup("repair", "_id", "vehicleId", "repair"),

                Aggregation.addFields()
                        .addField("repairs")
                        .withValue(
                                ArrayOperators.Filter.filter("repair")
                                        .as("c")
                                        .by(
                                                ComparisonOperators.Eq.valueOf(
                                                        ConditionalOperators.IfNull.ifNull("$$c.performedBy").then("NULL")
                                                ).equalToValue("NULL")
                                        )
                        )
                        .build(),

                Aggregation.addFields()
                        .addField("repairsObj")
                        .withValue(
                                ArrayOperators.ArrayToObject.arrayToObject(
                                        VariableOperators.Map.itemsOf("repairs")
                                                .as("c")
                                                .andApply(
                                                        ArrayOperators.ConcatArrays.arrayOf(
                                                                List.of(
                                                                        ConditionalOperators.ifNull("$$c.componentType").then("UNKNOWN"),
                                                                        "$$c"
                                                                )
                                                        )
                                                )
                                )
                        ).build(),

                Aggregation.addFields()
                        // Khung nâng
                        .addField("liftingFrame.value")
                        .withValue(
                                StringOperators.Concat.valueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.chassisType")).then(""))
                                        .concat(" - ")
                                        .concatValueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.liftingCapacityKg")).then("0"))
                                        .concat(" Kg - ")
                                        .concatValueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.liftingHeightMm")).then("0"))
                                        .concat(" mm")
                        )
                        .addField("liftingFrame.serialNumber").withValue("$specificationsSerial.liftingFrameSerial")
                        .addField("liftingFrame.componentId").withValue("$componentsObj.LIFTING_FRAME._id")
                        .addField("liftingFrame.repairStatus").withValue("$repairsObj.LIFTING_FRAME.status")
                        .addField("liftingFrame.repairType").withValue("$repairsObj.LIFTING_FRAME.repairType")
                        .addField("liftingFrame.repairComponentType").withValue("$repairsObj.LIFTING_FRAME.componentType")
                        .addField("liftingFrame.repairExpectedCompletionDate").withValue("$repairsObj.LIFTING_FRAME.expectedCompletionDate")
                        // Bình điện
                        .addField("battery.value")
                        .withValue(
                                StringOperators.Concat.valueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.batteryInfo")).then(""))
                                        .concat(" - ")
                                        .concatValueOf(ConditionalOperators.IfNull.ifNull(ConvertOperators.ToString.toString("$specifications.batterySpecification")).then(""))
                        )
                        .addField("battery.serialNumber").withValue("$specificationsSerial.batterySerial")
                        .addField("battery.componentId").withValue("$componentsObj.BATTERY._id")
                        .addField("battery.repairStatus").withValue("$repairsObj.BATTERY.status")
                        .addField("battery.repairType").withValue("$repairsObj.BATTERY.repairType")
                        .addField("battery.repairComponentType").withValue("$repairsObj.BATTERY.componentType")
                        .addField("battery.repairExpectedCompletionDate").withValue("$repairsObj.BATTERY.expectedCompletionDate")
                        // Sạc
                        .addField("charger.value").withValue("$specifications.chargerSpecification")
                        .addField("charger.componentId").withValue("$componentsObj.CHARGER._id")
                        .addField("charger.serialNumber").withValue("$specificationsSerial.chargerSerial")
                        .addField("charger.repairStatus").withValue("$repairsObj.CHARGER.status")
                        .addField("charger.repairType").withValue("$repairsObj.CHARGER.repairType")
                        .addField("charger.repairComponentType").withValue("$repairsObj.CHARGER.componentType")
                        .addField("charger.repairExpectedCompletionDate").withValue("$repairsObj.CHARGER.expectedCompletionDate")
                        // Động cơ
                        .addField("engine.value").withValue("$specifications.engineType")
                        .addField("engine.componentId").withValue("$componentsObj.ENGINE._id")
                        .addField("engine.serialNumber").withValue("$specificationsSerial.engineSerial")
                        .addField("engine.repairStatus").withValue("$repairsObj.ENGINE.status")
                        .addField("engine.repairType").withValue("$repairsObj.ENGINE.repairType")
                        .addField("engine.repairComponentType").withValue("$repairsObj.ENGINE.componentType")
                        .addField("engine.repairExpectedCompletionDate").withValue("$repairsObj.ENGINE.expectedCompletionDate")
                        // Càng nâng
                        .addField("fork.value").withValue("$specifications.forkDimensions")
                        .addField("fork.componentId").withValue("$componentsObj.FORK._id")
                        .addField("fork.serialNumber").withValue("$specificationsSerial.forkSerial")
                        .addField("fork.repairStatus").withValue("$repairsObj.FORK.status")
                        .addField("fork.repairType").withValue("$repairsObj.FORK.repairType")
                        .addField("fork.repairComponentType").withValue("$repairsObj.FORK.componentType")
                        .addField("fork.repairExpectedCompletionDate").withValue("$repairsObj.FORK.expectedCompletionDate")
                        // Van
                        .addField("valve.value").withValue("$specifications.valveCount")
                        .addField("valve.componentId").withValue("$componentsObj.VALVE._id")
                        .addField("valve.serialNumber").withValue("$specificationsSerial.valveSerial")
                        .addField("valve.repairStatus").withValue("$repairsObj.VALVE.status")
                        .addField("valve.repairType").withValue("$repairsObj.VALVE.repairType")
                        .addField("valve.repairComponentType").withValue("$repairsObj.VALVE.componentType")
                        .addField("valve.repairExpectedCompletionDate").withValue("$repairsObj.VALVE.expectedCompletionDate")
                        // Side shift
                        .addField("sideShift.value").withValue("$specifications.hasSideShift")
                        .addField("sideShift.componentId").withValue("$componentsObj.SIDE_SHIFT._id")
                        .addField("sideShift.serialNumber").withValue("$specificationsSerial.sideShiftSerial")
                        .addField("sideShift.repairStatus").withValue("$repairsObj.SIDE_SHIFT.status")
                        .addField("sideShift.repairType").withValue("$repairsObj.SIDE_SHIFT.repairType")
                        .addField("sideShift.repairComponentType").withValue("$repairsObj.SIDE_SHIFT.componentType")
                        .addField("sideShift.repairExpectedCompletionDate").withValue("$repairsObj.SIDE_SHIFT.expectedCompletionDate")
                        // BÁNH XE
                        .addField("wheel.value").withValue("$specifications.wheelInfo")
                        .addField("wheel.componentId").withValue("$componentsObj.WHEEL._id")
                        .addField("wheel.serialNumber").withValue("$specificationsSerial.wheelSerial")
                        .addField("wheel.repairStatus").withValue("$repairsObj.WHEEL.status")
                        .addField("wheel.repairType").withValue("$repairsObj.WHEEL.repairType")
                        .addField("wheel.repairComponentType").withValue("$repairsObj.WHEEL.componentType")
                        .addField("wheel.repairExpectedCompletionDate").withValue("$repairsObj.WHEEL.expectedCompletionDate")

                        .build(),

                Aggregation.project()
                        .and("_id").as("vehicleId")
                        .andInclude("productCode", "model", "serialNumber", "isFullyComponent", "initialCondition", "liftingFrame", "battery", "charger", "engine", "fork", "valve", "sideShift", "wheel")
                        .andExclude("_id")
        );

        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, RepairVehicleSpecPageDto.class, aggregation, optionsDto);
    }

    public Page<ItemCodeModelSerialDto> findPageVehicleInStock(PageOptionsDto optionsDto){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("inventoryType").is(InventoryType.VEHICLE.getId()),
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("serialNumber").ne(null),
                        Criteria.where("deletedAt").isNull()
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouse.type").is(WarehouseType.DESTINATION.getId())
                )),
                Aggregation.project("productCode", "model", "serialNumber")
                        .and("_id").as("vehicleId")
        );

        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, ItemCodeModelSerialDto.class, aggregation, optionsDto);
    }


}