package warehouse_management.com.warehouse_management.repository.inventory_item.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import cz.jirutka.rsql.parser.RSQLParser;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.InventoryWarehouseContainer;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryPoWarehouseRes;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemCustomRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class InventoryItemRepositoryImpl implements InventoryItemCustomRepository {

    private final MongoTemplate mongoTemplate;

    public InventoryItemRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventoryDestination(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.PRODUCT_ACCESSORIES.getId())
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(Criteria.where("warehouse.deletedAt").isNull())
        );
        Map<String, String> rsqlPropertyMapper = Map.of(
                "arrivalDate", "logistics.arrivalDate"
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainer.class, aggQuery, rsqlPropertyMapper, optionsReq);
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventoryProduction(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.PRODUCT_ACCESSORIES.getId())
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(Criteria.where("warehouse.deletedAt").isNull())
        );
        Map<String, String> rsqlPropertyMapper = Map.of(
                "orderDate", "logistics.orderDate",
                "arrivalDate", "logistics.arrivalDate"
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainer.class, aggQuery, rsqlPropertyMapper, optionsReq);

    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventoryDeparture(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.PRODUCT_ACCESSORIES.getId())
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.lookup("container", "containerId", "_id", "container"),
                Aggregation.unwind("container"),
                Aggregation.lookup("warehouse", "container.toWarehouseId", "_id", "container.toWarehouse"),
                Aggregation.unwind("container.toWarehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("container.deletedAt").isNull(),
                        Criteria.where("container.toWarehouse.deletedAt").isNull()
                ))
        );
        Map<String, String> rsqlPropertyMapper = Map.of(
                "orderDate", "logistics.orderDate",
                "arrivalDate", "logistics.arrivalDate",
                "container.toWarehouse", "container.toWarehouse.name"
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainer.class, aggQuery, rsqlPropertyMapper, optionsReq);
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventoryConsignment(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.PRODUCT_ACCESSORIES.getId())
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(Criteria.where("warehouse.deletedAt").isNull())
        );
        Map<String, String> rsqlPropertyMapper = Map.of(
                "arrivalDate", "logistics.arrivalDate",
                "consignmentDate", "logistics.consignmentDate"
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainer.class, aggQuery, rsqlPropertyMapper, optionsReq);
    }

    private Page<InventoryWarehouseContainer> findInventoryWarehouseContainers(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(Criteria.where("warehouse.deletedAt").isNull())
        );
        Map<String, String> rsqlPropertyMapper = Map.of(
                "orderDate", "logistics.orderDate"
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainer.class, aggQuery, rsqlPropertyMapper, optionsReq);
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsReq optionsReq) {
        return findInventoryWarehouseContainers(warehouseId, optionsReq);
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsReq optionsReq) {
        return findInventoryWarehouseContainers(warehouseId, optionsReq);
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsReq optionsReq) {
        return findInventoryWarehouseContainers(warehouseId, optionsReq);
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouseId").is(warehouseId),
                        Criteria.where("deletedAt").isNull(),
                        Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId())
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(Criteria.where("warehouse.deletedAt").isNull())
        );
        Map<String, String> rsqlPropertyMapper = Map.of(
                "orderDate", "logistics.orderDate",
                "warehouseName", "warehouse.name",
                "consignmentDate", "logistics.consignmentDate"
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainer.class, aggQuery, rsqlPropertyMapper, optionsReq);
    }

    @Override
    public Page<InventoryWarehouseContainer> findPageInventoryCentralWarehouse(PageOptionsReq optionsReq){
        Criteria isInStockInventory = new Criteria().andOperator(
                Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                Criteria.where("deletedAt").isNull()
        );
        Criteria isWarehouseDestination = new Criteria().andOperator(
                Criteria.where("warehouse.type").is(WarehouseType.DESTINATION.getId()),
                Criteria.where("warehouse.deletedAt").isNull()
        );
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(isInStockInventory),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.match(isWarehouseDestination)
        );
        Map<String, String> rsqlPropertyMapper = Map.of(
                "arrivalDate", "logistics.arrivalDate",
                "warehouseType", "warehouse.type"
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainer.class, agg, rsqlPropertyMapper, optionsReq);
    }

    @Override
    public List<InventoryPoWarehouseRes> findInventoryInStockPoNumbers(String warehouseType, String filter, Sort sort){
        List<AggregationOperation> aggOps = new ArrayList<>(List.of(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                        Criteria.where("deletedAt").isNull()
                )),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.group("poNumber", "warehouse._id")
                        .first("warehouse").as("warehouse"),
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("warehouse.deletedAt").isNull(),
                        Criteria.where("warehouse.type").is(warehouseType)
                )),
                Aggregation.project()
                        .andExclude("_id")
                        .and("poNumber").as("poNumber")
                        .and(context -> new Document("_id", "$warehouse._id")
                                .append("name", "$warehouse.name")).as("warehouse")
        ));
        if(filter != null && !filter.isBlank()){
            Criteria filterCriteria = new RSQLParser().parse(filter).accept(new MongoRsqlUtils.MongoRsqlVisitor(Map.of()));
            aggOps.add(Aggregation.match(filterCriteria));
        }
        if(!sort.isEmpty()){
            aggOps.add(Aggregation.sort(sort));
        }
        AggregationResults<InventoryPoWarehouseRes> aggResults = mongoTemplate.aggregate(Aggregation.newAggregation(aggOps), InventoryItem.class, InventoryPoWarehouseRes.class);
        return aggResults.getMappedResults();
    }

    @Override
    public List<InventoryItem> findInventoryInStockByPoNumber(String warehouseType, String poNumber, String filter, Sort sort) {
        List<AggregationOperation> aggOps = new ArrayList<>(List.of(
            Aggregation.match(new Criteria().andOperator(
                    Criteria.where("status").is(InventoryItemStatus.IN_STOCK.getId()),
                    Criteria.where("deletedAt").isNull(),
                    Criteria.where("poNumber").is(poNumber)
            )),
            Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
            Aggregation.unwind("warehouse"),
            Aggregation.match(new Criteria().andOperator(
                    Criteria.where("warehouse.deletedAt").isNull(),
                    Criteria.where("warehouse.type").is(warehouseType)
            ))
        ));
        if(filter != null && !filter.isBlank()){
            Criteria filterCriteria = new RSQLParser().parse(filter).accept(new MongoRsqlUtils.MongoRsqlVisitor(Map.of()));
            aggOps.add(Aggregation.match(filterCriteria));
        }
        if(!sort.isEmpty()){
            aggOps.add(Aggregation.sort(sort));
        }
        AggregationResults<InventoryItem> aggResults = mongoTemplate.aggregate(Aggregation.newAggregation(aggOps), InventoryItem.class, InventoryItem.class);
        return aggResults.getMappedResults();
    }

    @Transactional
    @Override
    public void insertAll(Collection<InventoryItem> inventoryItems){
        if(inventoryItems.isEmpty()) return;
        mongoTemplate.insertAll(inventoryItems);
    }

    @Transactional
    @Override
    public void bulkUpdateTransferDeparture(Collection<InventoryItem> inventoryItems){
        if(inventoryItems.isEmpty()) return;
        MongoCollection<Document> coll = mongoTemplate.getCollection(mongoTemplate.getCollectionName(InventoryItem.class));
        List<WriteModel<Document>> writeModels = new ArrayList<>();
        for(var item : inventoryItems){
            Bson filter = Filters.eq("_id", item.getId());
            Bson update = Updates.combine(
                    Updates.set("quantity", item.getQuantity()),
                    Updates.set("warehouseId", item.getWarehouseId().toString()),
                    Updates.set("status", item.getStatus().getId()),
                    Updates.set("logistics.arrivalDate", item.getLogistics().getArrivalDate())
            );
            writeModels.add(new UpdateOneModel<>(filter, update));
        }
        coll.bulkWrite(writeModels);
    }
}
