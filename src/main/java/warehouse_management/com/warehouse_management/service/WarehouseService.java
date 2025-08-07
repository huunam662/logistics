package warehouse_management.com.warehouse_management.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.mapper.warehouse.WarehouseMapper;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.dto.Inventory.view.InventoryWarehouseContainerView;
import warehouse_management.com.warehouse_management.repository.WarehouseRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j(topic = "WAREHOUSE-SERVICE")
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;

    public WarehouseService(WarehouseRepository repository, WarehouseMapper mapper, WarehouseRepository warehouseRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.warehouseRepository = warehouseRepository;
    }

    public Warehouse getWarehouseToId(ObjectId warehouseId) {
        return warehouseRepository.findById(warehouseId).orElseThrow(
                () -> new NoSuchElementException("Kho không tồn tại.")
        );
    }

    public Page<Warehouse> getPageWarehouse(PageOptionsReq optionsReq) {
//        Aggregation aggQuery = Aggregation.newAggregation(
//                Aggregation.lookup("user", "managedBy", "_id", "userManaged"),
//                Aggregation.unwind("userManaged")
//        );
//        return MongoRsqlUtils.queryAggregatePage(Warehouse.class, WarehouseView.class, aggQuery, optionsReq);
        return MongoRsqlUtils.queryPage(Warehouse.class, optionsReq);
    }

    private Page<InventoryWarehouseContainerView> getPageInventoryProductionAccessories(ObjectId warehouseId, PageOptionsReq optionsReq){
        Criteria isWarehouseId = Criteria.where("warehouseId").is(warehouseId);
        Criteria notDeleted = Criteria.where("isDeleted").is(false);
        Criteria isProduction = Criteria.where("inventoryType").is(InventoryType.PRODUCT_ACCESSORIES.getId());
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(isWarehouseId),
                Aggregation.match(notDeleted),
                Aggregation.match(isProduction),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse")
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainerView.class, aggQuery, optionsReq);
    }

    public Page<InventoryWarehouseContainerView> getPageInventoryProduction(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.PRODUCTION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho chờ sản xuất.");

        return getPageInventoryProductionAccessories(warehouse.getId(), optionsReq);
    }

    public Page<InventoryWarehouseContainerView> getPageInventoryDeparture(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho di.");

        Criteria isWarehouseId = Criteria.where("warehouseId").is(warehouseId);
        Criteria notDeleted = Criteria.where("isDeleted").is(false);
        Criteria isProduction = Criteria.where("inventoryType").is(InventoryType.PRODUCT_ACCESSORIES.getId());
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(isWarehouseId),
                Aggregation.match(notDeleted),
                Aggregation.match(isProduction),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse"),
                Aggregation.lookup("container", "containerId", "_id", "container"),
                Aggregation.unwind("container"),
                Aggregation.lookup("warehouse", "container.toWarehouseId", "_id", "container.toWarehouse"),
                Aggregation.unwind("container.toWarehouse")
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainerView.class, aggQuery, optionsReq);
    }

    public WarehouseResponseDto createWarehouse(CreateWarehouseDto createDto) {
        Warehouse warehouse = mapper.toEntity(createDto);
        Warehouse savedWarehouse = repository.save(warehouse);
        return mapper.toResponseDto(savedWarehouse);
    }

    public Page<InventoryWarehouseContainerView> getPageInventoryDestination(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DESTINATION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đích.");

        return getPageInventoryProductionAccessories(warehouse.getId(), optionsReq);
    }

    public List<WarehouseResponseDto> getAllWarehouses() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Page<InventoryWarehouseContainerView> getPageInventoryConsignment(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.CONSIGNMENT))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho ký gửi.");
        return getPageInventoryProductionAccessories(warehouse.getId(), optionsReq);
    }

    public WarehouseResponseDto getWarehouseById(String id) {
        Warehouse warehouse = findWarehouseById(id);
        return mapper.toResponseDto(warehouse);
    }

    private Page<InventoryWarehouseContainerView> getPageInventorySpareParts(ObjectId warehouseId, PageOptionsReq optionsReq){
        Criteria isWarehouseId = Criteria.where("warehouseId").is(warehouseId);
        Criteria notDeleted = Criteria.where("isDeleted").is(false);
        Criteria isSpareParts = Criteria.where("inventoryType").is(InventoryType.SPARE_PART.getId());
        Aggregation aggQuery = Aggregation.newAggregation(
                Aggregation.match(isWarehouseId),
                Aggregation.match(notDeleted),
                Aggregation.match(isSpareParts),
                Aggregation.lookup("warehouse", "warehouseId", "_id", "warehouse"),
                Aggregation.unwind("warehouse")
        );
        return MongoRsqlUtils.queryAggregatePage(InventoryItem.class, InventoryWarehouseContainerView.class, aggQuery, optionsReq);
    }

    public WarehouseResponseDto updateWarehouse(String id, UpdateWarehouseDto updateDto) {
        Warehouse existingWarehouse = findWarehouseById(id);
        mapper.updateFromDto(updateDto, existingWarehouse);
        Warehouse updatedWarehouse = repository.save(existingWarehouse);
        return mapper.toResponseDto(updatedWarehouse);
    }

    public Page<InventoryWarehouseContainerView> getPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.PRODUCTION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho chờ sản xuất.");

        return getPageInventorySpareParts(warehouse.getId(), optionsReq);
    }

    public Page<InventoryWarehouseContainerView> getPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đi.");


        return getPageInventorySpareParts(warehouse.getId(), optionsReq);
    }

    public void deleteWarehouse(String id) {

        Warehouse warehouse = findWarehouseById(id);
        repository.delete(warehouse);
    }

    public Page<InventoryWarehouseContainerView> getPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DESTINATION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đích.");
        return getPageInventorySpareParts(warehouse.getId(), optionsReq);
    }

    public Page<InventoryWarehouseContainerView> getPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.CONSIGNMENT))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho ký gửi.");
        return getPageInventorySpareParts(warehouse.getId(), optionsReq);
    }

    private Warehouse findWarehouseById(String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}
