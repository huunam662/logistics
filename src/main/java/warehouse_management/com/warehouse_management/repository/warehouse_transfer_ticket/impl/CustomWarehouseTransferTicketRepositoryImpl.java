package warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transfer_ticket.response.WarehouseTransferTicketPageDto;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;
import warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.CustomWarehouseTransferTicketRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomWarehouseTransferTicketRepositoryImpl implements CustomWarehouseTransferTicketRepository {


    @Override
    public Page<WarehouseTransferTicketPageDto> findPageWarehouseTransferTicket(PageOptionsDto optionsDto) {
        List<AggregationOperation> pipelines = List.of(
                Aggregation.lookup("user", "createdBy", "_id", "user"),
                Aggregation.unwind("user", true),
                Aggregation.match(Criteria.where("deletedAt").isNull()),
                Aggregation.project("title", "reason", "status", "createdAt", "approvedAt")
                        .and("_id").as("id")
                        .and("user.username").as("requesterName")
        );
        Aggregation aggregation = Aggregation.newAggregation(pipelines);
        return MongoRsqlUtils.queryAggregatePage(WarehouseTransferTicket.class, WarehouseTransferTicketPageDto.class, aggregation, optionsDto);
    }
}
