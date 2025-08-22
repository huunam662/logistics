package warehouse_management.com.warehouse_management.repository.warehouse_transaction.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.response.WarehouseTransactionPageDto;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTranType;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;
import warehouse_management.com.warehouse_management.repository.warehouse_transaction.CustomWarehouseTransactionRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomWarehouseTransactionRepositoryImpl implements CustomWarehouseTransactionRepository {


    @Override
    public Page<WarehouseTransactionPageDto> findPageWarehouseTransferTicket(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("user", "createdBy", "_id", "user"),
                Aggregation.unwind("user", true),
                Aggregation.match(Criteria.where("deletedAt").isNull()),
                Aggregation.project("title", "ticketCode", "reason", "status", "createdAt", "approvedAt")
                        .and("_id").as("id")
                        .and("user.username").as("requesterName")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(WarehouseTransaction.class, WarehouseTransactionPageDto.class, aggregation, optionsDto);
    }
    @Override
    public Page<WarehouseTransactionPageDto> findPageWarehouseTransferTicket(PageOptionsDto optionsDto, WarehouseTranType tranType) {
        Criteria criteria = Criteria.where("deletedAt").isNull();
        if (tranType != null) {
            if (tranType == WarehouseTranType.WAREHOUSE_INOUT) {
                criteria.and("tranType").in(WarehouseTranType.WAREHOUSE_IN.getId(), WarehouseTranType.WAREHOUSE_OUT.getId());
            } else {
                criteria.and("tranType").is(tranType.getId());
            }
        }

        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("user", "createdBy", "_id", "user"),
                Aggregation.unwind("user", true),
                Aggregation.match(criteria),
                Aggregation.project("title", "ticketCode", "reason", "status", "createdAt", "approvedAt")
                        .and("_id").as("id")
                        .and("user.username").as("requesterName")
        );

        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(WarehouseTransaction.class, WarehouseTransactionPageDto.class, aggregation, optionsDto);
    }
}
