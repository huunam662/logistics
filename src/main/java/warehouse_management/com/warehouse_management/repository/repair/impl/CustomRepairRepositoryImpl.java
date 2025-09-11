package warehouse_management.com.warehouse_management.repository.repair.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDTO;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
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
    public Page<RepairResponseDTO> findItemWithFilter(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = new ArrayList<>();

        pipelines.add(Aggregation.match(Criteria.where("deletedBy").is(null)));

        // Lấy lịch sử phiếu sửa chữa cho đơn sửa chữa
        pipelines.add(Aggregation.lookup("repair_transaction", "_id", "repairId","repairTransactions"));

        pipelines.add(Aggregation.project("note", "status", "createdAt", "deletedBy", "repairTransactions", "completedDate", "expectedCompletionDate")
                .and("repairInventoryItem.productCode").as("repairInventoryItemProductCode")
                .and("repairInventoryItem.model").as("repairInventoryItemModel")
                .and("repairInventoryItem.serialNumber").as("repairInventoryItemSerialNumber"));

        Aggregation agg = Aggregation.newAggregation(pipelines);

        return MongoRsqlUtils.queryAggregatePage(Repair.class, RepairResponseDTO.class, agg, optionsDto);
    }

    @Override
    public Repair updateStatus(ObjectId id, RepairStatus status) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("status", status);

        if (status == RepairStatus.COMPLETE) {
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

        if (status == RepairStatus.COMPLETE) {
            update.set("completedDate", LocalDateTime.now());
            // Chỉnh trạng thái của xe lại là đang sữa chữa
            inventoryItemRepository.updateBulkStatusInventoryItem(List.of(itemId), InventoryItemStatus.IN_STOCK);
        }

        return mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Repair.class);
    }
}
