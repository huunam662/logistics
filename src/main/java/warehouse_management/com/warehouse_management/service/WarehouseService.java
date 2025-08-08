package warehouse_management.com.warehouse_management.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.InventoryWarehouseContainer;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryPoWarehouseRes;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.mapper.warehouse.WarehouseMapper;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j(topic = "WAREHOUSE-SERVICE")
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;

    public WarehouseService(InventoryItemRepository inventoryItemRepository, WarehouseRepository repository, WarehouseMapper mapper, WarehouseRepository warehouseRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.warehouseRepository = warehouseRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public Warehouse getWarehouseToId(ObjectId warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
        if (warehouse == null || warehouse.getDeletedAt() != null)
            throw LogicErrException.of("Kho không tồn tại.");
        return warehouse;
    }

    public Page<Warehouse> getPageWarehouse(PageOptionsReq optionsReq) {
        return MongoRsqlUtils.queryPage(Warehouse.class, optionsReq);
    }


    public Page<InventoryWarehouseContainer> getPageInventoryProduction(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.PRODUCTION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho chờ sản xuất.");

        return inventoryItemRepository.findPageInventoryProduction(warehouseId, optionsReq);
    }

    public Page<InventoryWarehouseContainer> getPageInventoryDeparture(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho di.");

        return inventoryItemRepository.findPageInventoryDeparture(warehouseId, optionsReq);
    }

    public WarehouseResponseDto createWarehouse(CreateWarehouseDto createDto) {
        Warehouse warehouse = mapper.toEntity(createDto);
        Warehouse savedWarehouse = repository.save(warehouse);
        return mapper.toResponseDto(savedWarehouse);
    }

    public Page<InventoryWarehouseContainer> getPageInventoryDestination(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DESTINATION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đích.");

        return inventoryItemRepository.findPageInventoryDestination(warehouseId, optionsReq);
    }

    public List<WarehouseResponseDto> getAllWarehouses() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Page<InventoryWarehouseContainer> getPageInventoryConsignment(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.CONSIGNMENT))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho ký gửi.");

        return inventoryItemRepository.findPageInventoryConsignment(warehouseId, optionsReq);
    }

    public WarehouseResponseDto getWarehouseById(String id) {
        Warehouse warehouse = getWarehouseToId(new ObjectId(id));
        return mapper.toResponseDto(warehouse);
    }

    public WarehouseResponseDto updateWarehouse(String id, UpdateWarehouseDto updateDto) throws Exception {
        Optional<Warehouse> existingWarehouse = repository.findById(new ObjectId(id));

        Warehouse warehouse = existingWarehouse.orElseThrow(
                () -> new Exception("Not found warehouse")
        );

        mapper.updateFromDto(updateDto, warehouse);
        Warehouse updatedWarehouse = repository.save(warehouse);
        return mapper.toResponseDto(updatedWarehouse);
    }

    public Page<InventoryWarehouseContainer> getPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.PRODUCTION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho chờ sản xuất.");

        return inventoryItemRepository.findPageInventorySparePartsProduction(warehouseId, optionsReq);
    }


    public Page<InventoryWarehouseContainer> getPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đi.");

        return inventoryItemRepository.findPageInventorySparePartsDeparture(warehouseId, optionsReq);
    }

    public boolean deleteWarehouse(String id) {
        Optional<Warehouse> warehouse = repository.findById(new ObjectId(id));
        if (!warehouse.isPresent()) {
            warehouse.get().setDeletedAt(LocalDateTime.now());
        } else
            return false;
        return true;
    }

    public Page<InventoryWarehouseContainer> getPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DESTINATION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đích.");

        return inventoryItemRepository.findPageInventorySparePartsDestination(warehouseId, optionsReq);
    }

    public Page<InventoryWarehouseContainer> getPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsReq optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.CONSIGNMENT))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho ký gửi.");

        return inventoryItemRepository.findPageInventorySparePartsConsignment(warehouseId, optionsReq);
    }

    public Page<InventoryWarehouseContainer> getPageInventoryCentralWarehouse(PageOptionsReq optionsReq){
        return inventoryItemRepository.findPageInventoryCentralWarehouse(optionsReq);
    }

    @Transactional
    public long bulkSoftDeleteWarehouses(List<String> warehouseIdStrings) {
        // Lấy ID người dùng hiện tại
//        ObjectId currentUserId = getCurrentUserId(); // Placeholder

        List<ObjectId> warehouseIds = warehouseIdStrings.stream()
                .map(ObjectId::new)
                .toList();

        if (warehouseIds.isEmpty()) {
            return 0;
        }

        return warehouseRepository.bulkSoftDelete(warehouseIds, new ObjectId("6529f2e5b3a04a4a2e8b4f1c"));
    }

}
