package warehouse_management.com.warehouse_management.repository.repair.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.VehicleConfigurationPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDto;
import warehouse_management.com.warehouse_management.dto.repair.response.VehicleRepairPageDto;
import warehouse_management.com.warehouse_management.enumerate.ComponentType;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.Repair;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.repair.CustomRepairRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomRepairRepositoryImpl implements CustomRepairRepository {
    private final MongoTemplate mongoTemplate;
    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public Page<RepairResponseDto> findItemWithFilter(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = new ArrayList<>();

        pipelines.add(Aggregation.match(Criteria.where("deletedBy").is(null)));

        // Lấy lịch sử phiếu sửa chữa cho đơn sửa chữa
        pipelines.add(Aggregation.lookup("repair_transaction", "_id", "repairId","repairTransactions"));

        pipelines.add(Aggregation.project("note", "status", "createdAt", "deletedBy", "repairTransactions", "completedDate", "expectedCompletionDate")
                .and("repairInventoryItem.productCode").as("repairInventoryItemProductCode")
                .and("repairInventoryItem.model").as("repairInventoryItemModel")
                .and("repairInventoryItem.serialNumber").as("repairInventoryItemSerialNumber"));

        Aggregation agg = Aggregation.newAggregation(pipelines);

        return MongoRsqlUtils.queryAggregatePage(Repair.class, RepairResponseDto.class, agg, optionsDto);
    }

    @Override
    public Repair updateStatus(ObjectId id, RepairStatus status) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("status", status);

        if (status == RepairStatus.COMPLETED) {
            update.set("completedDate", LocalDateTime.now());
            // Chỉnh trạng thái của xe lại là đang sữa chữa
            inventoryItemRepository.updateBulkStatusInventoryItem(List.of(), InventoryItemStatus.IN_REPAIR);
        }

        return mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Repair.class);
    }

    @Override
    public Repair updateStatus(ObjectId id, RepairStatus status, ObjectId itemId) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("status", status);

        if (status == RepairStatus.COMPLETED) {
            update.set("completedDate", LocalDateTime.now());
            // Chỉnh trạng thái của xe lại là đang sữa chữa
            inventoryItemRepository.updateBulkStatusInventoryItem(List.of(itemId), InventoryItemStatus.IN_STOCK);
        }

        return mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Repair.class);
    }

    @Override
    public void bulkUpdateStatus(List<Repair> repairs) {
        if (repairs.isEmpty()) return;

        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Repair.class);

        for (var repairTrans : repairs) {

            Query query = new Query(Criteria.where("_id").is(repairTrans.getId()));

            Update update = new Update().set("status", repairTrans.getStatus());

            ops.updateOne(query, update);
        }

        ops.execute();
    }

    @Transactional
    @Override
    public void updateStatus(Repair repair) {

        Query query = new Query(Criteria.where("_id").is(repair.getId()));
        Update update = new Update()
                .set("status", repair.getStatus())

                .set("completedBy", repair.getCompletedBy())
                .set("confirmedBy", repair.getConfirmedBy())

                .set("completedAt", repair.getCompletedAt())
                .set("confirmedAt", repair.getConfirmedAt());

        mongoTemplate.updateFirst(query, update, Repair.class);
    }

    @Transactional
    @Override
    public void updatePerformed(String repairCode, String performedBy) {

        Query query = new Query(Criteria.where("repairCode").is(repairCode));
        Update update = new Update()
                .set("performedBy", performedBy)
                .set("performedAt", LocalDateTime.now());

        mongoTemplate.updateFirst(query, update, Repair.class);
    }

    @Override
    public Page<VehicleRepairPageDto> findPageVehicleRepairPage(PageOptionsDto optionsReq) {

        ConditionalOperators.Switch componentNameCase = ConditionalOperators.switchCases(
                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("componentType").equalToValue(ComponentType.FORK.getId())).then(ComponentType.FORK.getValue()),
                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("componentType").equalToValue(ComponentType.VALVE.getId())).then(ComponentType.VALVE.getValue()),
                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("componentType").equalToValue(ComponentType.ENGINE.getId())).then(ComponentType.ENGINE.getValue()),
                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("componentType").equalToValue(ComponentType.BATTERY.getId())).then(ComponentType.BATTERY.getValue()),
                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("componentType").equalToValue(ComponentType.CHARGER.getId())).then(ComponentType.CHARGER.getValue()),
                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("componentType").equalToValue(ComponentType.SIDE_SHIFT.getId())).then(ComponentType.SIDE_SHIFT.getValue()),
                ConditionalOperators.Switch.CaseOperator.when(ComparisonOperators.Eq.valueOf("componentType").equalToValue(ComponentType.LIFTING_FRAME.getId())).then(ComponentType.LIFTING_FRAME.getValue())
        ).defaultTo(null);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("deletedAt").isNull()),

                Aggregation.lookup("inventory_item", "vehicleId", "_id", "vehicle"),
                Aggregation.unwind("vehicle"),
                Aggregation.lookup("warehouse", "vehicle.warehouseId", "_id", "warehouseVehicle"),
                Aggregation.unwind("warehouseVehicle"),

                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("vehicle.deletedAt").isNull(),
                        Criteria.where("warehouseVehicle.deletedAt").isNull()
                )),

                Aggregation.project(
                                "repairCode",
                                "vehicleId",
                                "componentSerial",
                                "componentType",
                                "repairType",
                                "description",
                                "status",
                                "confirmedAt",
                                "completedAt",
                                "createdAt",
                                "confirmedBy",
                                "completedBy",
                                "createdBy"
                        )
                        .and("_id").as("id")
                        .and(componentNameCase).as("componentName")
                        .and("vehicle.productCode").as("vehicleProductCode")
                        .and("vehicle.model").as("vehicleModel")
                        .and("vehicle.serialNumber").as("vehicleSerial")
                        .and("warehouseVehicle.name").as("warehouseName")
        );

        return MongoRsqlUtils.queryAggregatePage(Repair.class, VehicleRepairPageDto.class, aggregation, optionsReq);
    }
}
