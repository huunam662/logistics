package warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.WarehouseTransferTicketDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;
import warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.CustomWarehouseTransferTicketRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomWarehouseTransferTicketRepositoryImpl implements CustomWarehouseTransferTicketRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<WarehouseTransferTicketDto> findPageWarehouseTransferTicket(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("warehouse", "originWarehouseId", "_id", "originWarehouse"),
                Aggregation.unwind("originWarehouse"),
                Aggregation.lookup("warehouse", "destinationWarehouseId", "_id", "destinationWarehouse"),
                Aggregation.unwind("destinationWarehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("originWarehouse.deletedAt").isNull(),
                        Criteria.where("destinationWarehouse.deletedAt").isNull()
                )),
                Aggregation.project("id", "status", "inventoryItemIds", "originWarehouseId", "destinationWarehouseId", "requesterId", "approverId", "rejectReason", "createdBy", "updatedBy", "createdAt", "updatedAt")
                        .and("originWarehouse.name").as("originWarehouseName")
                        .and("originWarehouse.address").as("originWarehouseAddress")
                        .and("destinationWarehouse.name").as("destinationWarehouseName")
                        .and("destinationWarehouse.address").as("destinationWarehouseAddress")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(WarehouseTransferTicket.class, WarehouseTransferTicketDto.class, aggregation, optionsDto);
    }
}
