package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.common.pagination.res.PageInfoRes;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.warehouse.request.BulkDeleteRequestDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.GetDepartureWarehouseForContainerDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.service.WarehouseService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService service) {
        this.warehouseService = service;
    }

    @PostMapping
    public ApiResponse<?> createWarehouse(@Valid @RequestBody CreateWarehouseDto createDto) {
        WarehouseResponseDto createdWarehouse = warehouseService.createWarehouse(createDto);
        return ApiResponse.success(createdWarehouse);
    }

    @GetMapping
    public ApiResponse<?>  getAllWarehouses() {
        List<WarehouseResponseDto> warehouses = warehouseService.getAllWarehouses();
        return ApiResponse.success(warehouses);
    }

    @GetMapping("/{id}")
    public ApiResponse<?>  getWarehouseById(@PathVariable("id") String id) {
        WarehouseResponseDto warehouse = warehouseService.getWarehouseById(id);
        return ApiResponse.success(warehouse);
    }

    @SneakyThrows
    @PutMapping("/{id}")
    public ApiResponse<?>  updateWarehouse(@PathVariable("id") String id, @Valid @RequestBody UpdateWarehouseDto updateDto) {
        WarehouseResponseDto updatedWarehouse = warehouseService.updateWarehouse(id, updateDto);
        return ApiResponse.success(updatedWarehouse);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?>  deleteWarehouse(@PathVariable("id") String id) {
        warehouseService.deleteWarehouse(id);
        return ApiResponse.success();
    }

    @GetMapping("/page")
    @Operation(
            summary = "GET data kho hàng. (phân trang)",
            description = "GET data kho hàng. (phân trang)"
    )
    public ApiResponse<?> getPageWarehouse(
            @ModelAttribute PageOptionsReq optionsReq
    ) {

        Page<WarehouseResponseDto> warehousePage = warehouseService.getPageWarehouse(optionsReq);
        return ApiResponse.success(new PageInfoRes<>(warehousePage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-production")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryProduction(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryProductionDto> inventoryProductionPage = warehouseService.getPageInventoryProduction(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryProductionPage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-departure")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDeparture(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryDepartureDto> inventoryItemPage = warehouseService.getPageInventoryDeparture(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-destination")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đích. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đích. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDestination(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryDestinationDto> inventoryItemPage = warehouseService.getPageInventoryDestination(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-consignment")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryConsignment(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryConsignmentDto> inventoryItemPage = warehouseService.getPageInventoryConsignment(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-consignment")
    @Operation(
            summary = "GET data Phụ tùng của kho ký gửi. (phân trang)",
            description = "GET data Phụ tùng của kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryConsignmentSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryConsignmentSparePartsDto> inventoryItemPage = warehouseService.getPageInventorySparePartsConsignment(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryItemPage));
    }


    @GetMapping("/{warehouseId}/page/spare-parts/inventory-production")
    @Operation(
            summary = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)",
            description = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryProductionSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryProductionSparePartsDto> inventoryWarehouseView = warehouseService.getPageInventorySparePartsProduction(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryWarehouseView));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-departure")
    @Operation(
            summary = "GET data Phụ tùng của kho đi. (phân trang)",
            description = "GET data Phụ tùng của kho đi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDepartureSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryDepartureSparePartsDto> inventoryItemPage = warehouseService.getPageInventorySparePartsDeparture(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-destination")
    @Operation(
            summary = "GET data Phụ tùng của kho đích. (phân trang)",
            description = "GET data Phụ tùng của kho đích. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDestinationSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryDestinationSparePartsDto> inventoryItemPage = warehouseService.getPageInventorySparePartsDestination(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryItemPage));
    }

    @GetMapping("/page/inventory-central-warehouse")
    @Operation(
            summary = "GET data hàng tồn tại các kho đến. (phân trang)",
            description = "GET data hàng tồn tại các kho đến. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryCentralWarehouse(
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryCentralWarehouseDto> inventoryItemPage = warehouseService.getPageInventoryCentralWarehouse(optionsReq);
        return ApiResponse.success(new PageInfoRes<>(inventoryItemPage));
    }

    @PostMapping("/delete-bulk")
    public ApiResponse<Map<String, Object>> bulkDeleteWarehouses(@Valid @RequestBody BulkDeleteRequestDto bulkDeleteRequest) {
        long deletedCount = warehouseService.bulkSoftDeleteWarehouses(bulkDeleteRequest.warehouseIds());

        Map<String, Object> response = Map.of(
                "deletedCount", deletedCount
        );

        return ApiResponse.success(response);
    }

    @GetMapping("/warehouse-type/{warehouseType}")
    public ApiResponse<List<GetDepartureWarehouseForContainerDto>> getWarehouseByTypeForContainer(
            @PathVariable String warehouseType
    ) {
        List<GetDepartureWarehouseForContainerDto> dtos = warehouseService.getDepartureWarehousesForContainer(warehouseType);
        return ApiResponse.success(dtos);
    }

}
