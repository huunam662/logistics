package warehouse_management.com.warehouse_management.repository.warranty.impl;

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
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyResponseDTO;
import warehouse_management.com.warehouse_management.enumerate.WarrantyStatus;
import warehouse_management.com.warehouse_management.model.Warranty;
import warehouse_management.com.warehouse_management.repository.warranty.CustomWarrantyRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomWarrantyRepositoryImpl implements CustomWarrantyRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<WarrantyResponseDTO> findItemWithFilter(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = new ArrayList<>();

        pipelines.add(Aggregation.match(Criteria.where("deletedBy").is(null)));

        // Lấy lịch sử phiếu bảo hành cho đơn bảo hành
        pipelines.add(Aggregation.lookup("warranty_transaction", "_id", "warrantyId","warrantyTransactions"));

        pipelines.add(Aggregation.project("clientName", "note", "status", "createdAt", "deletedBy", "warrantyTransactions", "completedDate")
                .and("warrantyInventoryItem.productCode").as("warrantyInventoryItemProductCode")
                .and("warrantyInventoryItem.model").as("warrantyInventoryItemModel")
                .and("warrantyInventoryItem.serialNumber").as("warrantyInventoryItemSerialNumber")
                .and("warrantyInventoryItem.logistics.arrivalDate").as("arrivalDate")
                .and("client.name").as("clientName"));

        Aggregation agg = Aggregation.newAggregation(pipelines);

        return MongoRsqlUtils.queryAggregatePage(Warranty.class, WarrantyResponseDTO.class, agg, optionsDto);
    }

    @Override
    public Warranty updateStatus(ObjectId id, WarrantyStatus status) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("status", status);

        if (status == WarrantyStatus.COMPLETE) {
            update.set("completedDate", LocalDateTime.now());
        }

        return mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Warranty.class);
    }
}
