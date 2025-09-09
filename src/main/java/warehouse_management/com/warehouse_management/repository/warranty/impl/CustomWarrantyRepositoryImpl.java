package warehouse_management.com.warehouse_management.repository.warranty.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyResponseDTO;
import warehouse_management.com.warehouse_management.model.Warranty;
import warehouse_management.com.warehouse_management.repository.warranty.CustomWarrantyRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

@Repository
@RequiredArgsConstructor
public class CustomWarrantyRepositoryImpl implements CustomWarrantyRepository {

    @Override
    public Page<WarrantyResponseDTO> findItemWithFilter(PageOptionsDto optionsDto) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("deletedBy").is(null)),
                Aggregation.project("clientName", "note", "status", "warrantyTransactions", "createdAt", "deletedBy")
                        .and("warrantyInventoryItem.productCode").as("warrantyInventoryItemProductCode")
                        .and("warrantyInventoryItem.model").as("warrantyInventoryItemModel")
                        .and("warrantyInventoryItem.serialNumber").as("warrantyInventoryItemSerialNumber")
                        .and("warrantyInventoryItem.logistics.arrivalDate").as("arrivalDate")
                        .and("client.name").as("clientName")
        );

        return MongoRsqlUtils.queryAggregatePage(Warranty.class, WarrantyResponseDTO.class, agg, optionsDto);
    }
}
