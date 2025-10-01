package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.WarehouseForOrderDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.warehouse.request.BulkDeleteRequestDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.GetDepartureWarehouseForContainerDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.service.InventoryItemService;
import warehouse_management.com.warehouse_management.service.WarehouseService;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Warehouse")
@RequestMapping("/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final InventoryItemService inventoryItemService;

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
            @ModelAttribute PageOptionsDto optionsReq
    ) {

        Page<WarehouseResponseDto> warehousePage = warehouseService.getPageWarehouse(optionsReq);
        return ApiResponse.success(new PageInfoDto<>(warehousePage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-production")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryProduction(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryProductionDto> inventoryProductionPage = warehouseService.getPageInventoryProduction(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryProductionPage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-departure")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDeparture(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryDepartureDto> inventoryItemPage = warehouseService.getPageInventoryDeparture(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-destination")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đích. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đích. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDestination(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryDestinationDto> inventoryItemPage = warehouseService.getPageInventoryDestination(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-consignment")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryConsignment(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryConsignmentDto> inventoryItemPage = warehouseService.getPageInventoryConsignment(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-consignment")
    @Operation(
            summary = "GET data Phụ tùng của kho ký gửi. (phân trang)",
            description = "GET data Phụ tùng của kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryConsignmentSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryConsignmentSparePartsDto> inventoryItemPage = warehouseService.getPageInventorySparePartsConsignment(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }


    @GetMapping("/{warehouseId}/page/spare-parts/inventory-production")
    @Operation(
            summary = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)",
            description = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryProductionSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryProductionSparePartsDto> inventoryWarehouseView = warehouseService.getPageInventorySparePartsProduction(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryWarehouseView));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-departure")
    @Operation(
            summary = "GET data Phụ tùng của kho đi. (phân trang)",
            description = "GET data Phụ tùng của kho đi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDepartureSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryDepartureSparePartsDto> inventoryItemPage = warehouseService.getPageInventorySparePartsDeparture(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-destination")
    @Operation(
            summary = "GET data Phụ tùng của kho đích. (phân trang)",
            description = "GET data Phụ tùng của kho đích. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDestinationSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryDestinationSparePartsDto> inventoryItemPage = warehouseService.getPageInventorySparePartsDestination(new ObjectId(warehouseId), optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/page/inventory-central-warehouse/product")
    @Operation(
            summary = "GET data sản phẩm tồn tại các kho đến. (phân trang)",
            description = "GET data sản phẩm tồn tại các kho đến. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryCentralWarehouse(
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryCentralWarehouseProductDto> inventoryItemPage = warehouseService.getPageInventoryCentralWarehouse(optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/page/inventory-central-warehouse/spare-part")
    @Operation(
            summary = "GET data hàng hóa tồn tại các kho ký gửi. (phân trang)",
            description = "GET data hàng hóa tồn tại các kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryCentralWarehouseSparePart(
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryCentralWarehouseSparePartDto> inventoryItemPage = warehouseService.getPageInventoryCentralWarehouseSparePart(optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/page/inventory-central-consignment/product")
    @Operation(
            summary = "GET data sản phẩm tồn tại các kho ký gửi. (phân trang)",
            description = "GET data sản phẩm tồn tại các kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryCentralWarehouseConsignment(
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryCentralWarehouseProductDto> inventoryItemPage = warehouseService.getPageInventoryCentralWarehouseConsignment(optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
    }

    @GetMapping("/page/inventory-central-consignment/spare-part")
    @Operation(
            summary = "GET data hàng hóa tồn tại các kho đến. (phân trang)",
            description = "GET data hàng hóa tồn tại các kho đến. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryCentralWarehouseConsignmentSparePart(
            @ModelAttribute PageOptionsDto optionsReq
    ) {
        Page<InventoryCentralWarehouseSparePartDto> inventoryItemPage = warehouseService.getPageInventoryCentralWarehouseConsignmentSparePart(optionsReq);
        return ApiResponse.success(new PageInfoDto<>(inventoryItemPage));
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
            @PathVariable("warehouseType") String warehouseType
    ) {
        List<GetDepartureWarehouseForContainerDto> dtos = warehouseService.getDepartureWarehousesForContainer(warehouseType);
        return ApiResponse.success(dtos);
    }

    @GetMapping("/warehouse-for-order")
    public ApiResponse<List<WarehouseForOrderDto>> getWarehousesForOrder() {
        List<WarehouseForOrderDto> dtos = warehouseService.getWarehousesForOrder();
        return ApiResponse.success(dtos);
    }

    @GetMapping("/{id}/products")
    @Operation(
            summary = "GET danh sách Sản Phẩm thuộc kho.",
            description = "GET danh sách Sản Phẩm thuộc kho."
    )
    public ResponseEntity<?> getProductsByWarehouseId(
            @PathVariable("id") String warehouseId,
            @RequestParam(value = "filter", required = false) String filter
    ){
        List<InventoryProductDetailsDto> productDetails = inventoryItemService.getProductsByWarehouseId(warehouseId, filter);
        return ResponseEntity.ok(ApiResponse.success(productDetails));
    }

    @GetMapping("/{id}/spare-part")
    @Operation(
            summary = "GET danh sách Phụ Tùng thuộc kho.",
            description = "GET danh sách Phụ Tùng thuộc kho."
    )
    public ResponseEntity<?> getSparePartByWarehouseId(
            @PathVariable("id") String warehouseId,
            @RequestParam(value = "filter", required = false) String filter
    ){
        List<InventorySparePartDetailsDto> sparePartDetailsDtos = inventoryItemService.getSparePartByWarehouseId(warehouseId, filter);
        return ResponseEntity.ok(ApiResponse.success(sparePartDetailsDtos));
    }


    @GetMapping("/products")
    @Operation(
            summary = "GET danh sách Sản Phẩm thuộc nhiều kho.",
            description = "GET danh sách Sản Phẩm thuộc nhiều kho."
    )
    public ResponseEntity<?> getProductsByWarehouseIdIn(
            @RequestParam("warehouseIds") List<String> warehouseIds,
            @RequestParam(value = "filter", required = false) String filter
    ){
        List<InventoryProductDetailsDto> productDetails = inventoryItemService.getProductsByWarehouseIdList(warehouseIds, filter);
        return ResponseEntity.ok(ApiResponse.success(productDetails));
    }

    @GetMapping("/spare-part")
    @Operation(
            summary = "GET danh sách Phụ Tùng thuộc nhiều kho.",
            description = "GET danh sách Phụ Tùng thuộc nhiều kho."
    )
    public ResponseEntity<?> getSparePartByWarehouseIdIn(
            @RequestParam("warehouseIds") List<String> warehouseIds,
            @RequestParam(value = "filter", required = false) String filter
    ){
        List<InventorySparePartDetailsDto> sparePartDetailsDtos = inventoryItemService.getSparePartByWarehouseIdList(warehouseIds, filter);
        return ResponseEntity.ok(ApiResponse.success(sparePartDetailsDtos));
    }

}
