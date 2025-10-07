package warehouse_management.com.warehouse_management.service;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.WarehouseForOrderDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.warehouse.response.GetDepartureWarehouseForContainerDto;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.integration.office.dto.request.CreateOfficeFromWarehouseReq;
import warehouse_management.com.warehouse_management.integration.office.dto.response.CreateOfficeFromWarehouseRes;
import warehouse_management.com.warehouse_management.integration.office.dto.response.OfficeDto;
import warehouse_management.com.warehouse_management.mapper.warehouse.WarehouseMapper;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j(topic = "WAREHOUSE-SERVICE")
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final CustomAuthentication customAuthentication;
    private final OfficeService officeService;

    public WarehouseService(InventoryItemRepository inventoryItemRepository, WarehouseRepository repository, WarehouseMapper mapper, WarehouseRepository warehouseRepository, CustomAuthentication customAuthentication, OfficeService officeService) {
        this.repository = repository;
        this.mapper = mapper;
        this.warehouseRepository = warehouseRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.customAuthentication = customAuthentication;
        this.officeService = officeService;
    }

    public Warehouse getWarehouseToId(ObjectId warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
        if (warehouse == null || warehouse.getDeletedAt() != null)
            throw LogicErrException.of("Kho không tồn tại.");
        return warehouse;
    }

    public Page<WarehouseResponseDto> getPageWarehouse(PageOptionsDto optionsReq) {
        return warehouseRepository.findPageWarehouse(optionsReq);
    }


    public Page<InventoryProductionDto> getPageInventoryProduction(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.PRODUCTION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho chờ sản xuất.");

        return inventoryItemRepository.findPageInventoryProduction(warehouse.getId(), optionsReq);
    }

    public Page<InventoryDepartureDto> getPageInventoryDeparture(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho di.");

        return inventoryItemRepository.findPageInventoryDeparture(warehouse.getId(), optionsReq);
    }

    public WarehouseResponseDto createWarehouse(CreateWarehouseDto createDto) {
        Warehouse warehouse = mapper.toEntity(createDto);
        WarehouseType warehouseType = warehouse.getType();
        OfficeDto res = officeService.createOfficeFromWarehouse(new CreateOfficeFromWarehouseReq(warehouse.getName(), warehouse.getCode(), warehouseType.getOfficeTypeCode()));
        warehouse.setOfficeId(res.getId());
        Warehouse savedWarehouse = repository.save(warehouse);
        return mapper.toResponseDto(savedWarehouse);
    }

    public Page<InventoryDestinationDto> getPageInventoryDestination(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DESTINATION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đích.");

        return inventoryItemRepository.findPageInventoryDestination(warehouse.getId(), optionsReq);
    }

    public List<WarehouseResponseDto> getAllWarehouses() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Page<InventoryConsignmentDto> getPageInventoryConsignment(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.CONSIGNMENT))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho ký gửi.");
        return inventoryItemRepository.findPageInventoryConsignment(warehouse.getId(), optionsReq);
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

    public Page<InventoryProductionSparePartsDto> getPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.PRODUCTION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho chờ sản xuất.");

        return inventoryItemRepository.findPageInventorySparePartsProduction(warehouse.getId(), optionsReq);
    }


    public Page<InventoryDepartureSparePartsDto> getPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đi.");

        return inventoryItemRepository.findPageInventorySparePartsDeparture(warehouse.getId(), optionsReq);
    }

    public boolean deleteWarehouse(String id) {
        boolean success = warehouseRepository.softDeleteById(new ObjectId(id),
                new ObjectId("6529f2e5b3a04a4a2e8b4f1c"), "ACTIVE");

        return success;
    }

    public Page<InventoryDestinationSparePartsDto> getPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.DESTINATION))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho đích.");

        return inventoryItemRepository.findPageInventorySparePartsDestination(warehouse.getId(), optionsReq);
    }

    public Page<InventoryConsignmentSparePartsDto> getPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsDto optionsReq) {
        Warehouse warehouse = getWarehouseToId(warehouseId);
        if (!warehouse.getType().equals(WarehouseType.CONSIGNMENT))
            throw LogicErrException.of("Kết quả cần tìm không phải là kho ký gửi.");

        return inventoryItemRepository.findPageInventorySparePartsConsignment(warehouse.getId(), optionsReq);
    }

    public Page<InventoryCentralWarehouseProductDto> getPageInventoryCentralWarehouse(PageOptionsDto optionsReq){
        return inventoryItemRepository.findPageInventoryCentralWarehouse(optionsReq);
    }

    public Page<InventoryCentralWarehouseProductDto> getPageInventoryCentralWarehouseConsignment(PageOptionsDto optionsReq){
        return inventoryItemRepository.findPageInventoryCentralWarehouseConsignment(optionsReq);
    }

    public Page<InventoryCentralWarehouseSparePartDto> getPageInventoryCentralWarehouseConsignmentSparePart(PageOptionsDto optionsReq){
        return inventoryItemRepository.findPageInventoryCentralWarehouseConsignmentSparePart(optionsReq);
    }

    public Page<InventoryCentralWarehouseSparePartDto> getPageInventoryCentralWarehouseSparePart(PageOptionsDto optionsReq){
        return inventoryItemRepository.findPageInventoryCentralWarehouseSparePart(optionsReq);
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

    public List<GetDepartureWarehouseForContainerDto> getDepartureWarehousesForContainer(String warehouseType) {
        return warehouseRepository.getDepartureWarehousesForContainer(warehouseType);
    }

    public List<WarehouseForOrderDto> getWarehousesForOrder() {
        return warehouseRepository.getWarehousesForOrder();
    }

}
