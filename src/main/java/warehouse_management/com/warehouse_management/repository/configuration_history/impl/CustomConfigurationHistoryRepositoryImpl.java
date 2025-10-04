package warehouse_management.com.warehouse_management.repository.configuration_history.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.VehicleConfigurationPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.ComponentType;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.configuration_history.CustomConfigurationHistoryRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
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

    @Override
    public Page<VehicleConfigurationPageDto> findPageVehicleConfigurationPage(PageOptionsDto optionsReq) {

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
                        "configurationCode",
                                "vehicleId",
                                "componentOldSerial",
                                "componentReplaceSerial",
                                "componentType",
                                "configType",
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

        return MongoRsqlUtils.queryAggregatePage(ConfigurationHistory.class, VehicleConfigurationPageDto.class, aggregation, optionsReq);
    }

    @Transactional
    @Override
    public void updatePerformed(String code, String performedBy) {
        Query query = new Query(Criteria.where("configurationCode").is(code));
        Update update = new Update()
                            .set("performedBy", performedBy)
                            .set("performedAt", LocalDateTime.now());
        mongoTemplate.updateMulti(query, update, ConfigurationHistory.class);
    }

    @Transactional
    @Override
    public void updatePerformed(ObjectId id, String performedBy) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("performedBy", performedBy)
                .set("performedAt", LocalDateTime.now());
        mongoTemplate.updateMulti(query, update, ConfigurationHistory.class);
    }

    @Transactional
    @Override
    public void bulkUpdatePerformed(Collection<ObjectId> ids, String performedBy) {
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update()
                .set("performedBy", performedBy)
                .set("performedAt", LocalDateTime.now());
        mongoTemplate.updateMulti(query, update, ConfigurationHistory.class);
    }

    @Transactional
    @Override
    public void updateStatus(ConfigurationHistory configurationHistory) {

        Query query = new Query(Criteria.where("_id").is(configurationHistory.getId()));
        Update update = new Update()
                .set("status", configurationHistory.getStatus())

                .set("completedBy", configurationHistory.getCompletedBy())
                .set("confirmedBy", configurationHistory.getConfirmedBy())
                .set("performedBy", configurationHistory.getPerformedBy())

                .set("completedAt", configurationHistory.getCompletedAt())
                .set("confirmedAt", configurationHistory.getConfirmedAt())
                .set("performedAt", configurationHistory.getPerformedAt());

        mongoTemplate.updateFirst(query, update, ConfigurationHistory.class);
    }
}
