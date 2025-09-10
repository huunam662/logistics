package warehouse_management.com.warehouse_management.repository.configuration_history.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryDepartureDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.ItemType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseStatus;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.configuration_history.ConfigurationHistoryRepository;
import warehouse_management.com.warehouse_management.repository.configuration_history.CustomConfigurationHistoryRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class CustomConfigurationHistoryRepositoryImpl implements CustomConfigurationHistoryRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomConfigurationHistoryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Transactional
    @Override
    public List<ConfigurationHistory> bulkInsert(Collection<ConfigurationHistory> configurationHistories) {
        if (configurationHistories.isEmpty()) return new ArrayList<>();
        return mongoTemplate.insertAll(configurationHistories).stream().toList();
    }

    @Override
    public Page<ConfigurationHistory> findPageCH(PageOptionsDto optionsReq) {
        if (optionsReq.getSortBy() == null || optionsReq.getSortBy().isEmpty()) {
            optionsReq.setSortBy(List.of("createdAt"));
            optionsReq.setDirection(Sort.Direction.DESC);
        }
        return MongoRsqlUtils.queryPage(ConfigurationHistory.class, ConfigurationHistory.class, optionsReq);
    }


    @Override
    public Page<ConfigurationHistory> findPageCHCurrent(PageOptionsDto optionsReq) {
        String filter = optionsReq.getFilter();
        if (filter == null || filter.isBlank()) {
            filter = "isLatest==true";
        } else {
            // Kết hợp filter hiện tại với isLatest==true
            filter = filter + ";isLatest==true";
        }
        optionsReq.setFilter(filter);

        // Nếu chưa có sort, mặc định sort theo createdAt giảm dần
        if (optionsReq.getSortBy() == null || optionsReq.getSortBy().isEmpty()) {
            optionsReq.setSortBy(List.of("createdAt"));
            optionsReq.setDirection(Sort.Direction.DESC);
        }
        return MongoRsqlUtils.queryPage(ConfigurationHistory.class, ConfigurationHistory.class, optionsReq);
    }
}
