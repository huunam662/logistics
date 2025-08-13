package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemPoNumberDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryPoWarehouseDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemProductionVehicleTypeDto;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.service.InventoryItemService;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Inventory Item")
@RequestMapping("/v1/inventory-items")
@RequiredArgsConstructor
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;
    private final InventoryItemMapper mapper;

    // Api Nhập kho
    @PostMapping
    @Operation(
            summary = "API Nhập Kho"
    )
    public ResponseEntity<?> createInventoryItem(@Valid @RequestBody CreateInventoryItemDto req) {
        InventoryItem savedItem = inventoryItemService.createInventoryItem(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedItem));
    }

    @GetMapping("/po-numbers")
    @Operation(
            summary = "GET lấy danh sách PO NUMBER theo loại kho.",
            description = "GET lấy danh sách PO NUMBER theo loại kho."
    )
    public ApiResponse<?> getInventoryInStockPoNumbers(
            @Parameter(description = "[PRODUCTION, DEPARTURE, DESTINATION, CONSIGNMENT]")
            @RequestParam("warehouseType") String warehouseType,
            @Parameter(description = "[VEHICLE, ACCESSORY, SPARE_PART]")
            @RequestParam("inventoryType") String inventoryType
    ){
        List<InventoryPoWarehouseDto> poNumbers = inventoryItemService.getInventoryInStockPoNumbers(warehouseType, inventoryType);
        return ApiResponse.success(poNumbers);
    }

    @GetMapping("/po-items")
    @Operation(
            summary = "GET lấy danh sách hàng hóa thuộc PO theo loại kho.",
            description = "GET lấy danh sách hàng hóa thuộc PO theo loại kho."
    )
    public ApiResponse<?> getInventoryInStockPoNumbers(
            @Parameter(description = "[PRODUCTION, DEPARTURE, DESTINATION, CONSIGNMENT]")
            @RequestParam("warehouseType") String warehouseType,
            @RequestParam("poNumber") String poNumber,
            @RequestParam(value = "filter", required = false) String filter
    ){
        List<InventoryItemPoNumberDto> poNumbers = inventoryItemService.getInventoryInStockByPoNumber(warehouseType, poNumber, filter);
        return ApiResponse.success(poNumbers);
    }

    @PostMapping("/transfer/production-to-departure")
    @Operation(
            summary = "POST chuyển hàng hóa từ kho chờ sản xuất sang kho đi.",
            description = "POST chuyển hàng hóa từ kho chờ sản xuất sang kho đi."
    )
    public ApiResponse<?> transferItemsProductionToDeparture(
            @RequestBody InventoryTransferProductionDepartureDto req
    ){
        Warehouse warehouse = inventoryItemService.transferItemsProductionToDeparture(req);
        ApiResponse<?> apiResponse = ApiResponse.success();
        apiResponse.setMessage("Nhập hàng sang kho " + warehouse.getName() + " thành công.");
        return apiResponse;
    }

    @PostMapping("/transfer/destination-to-consignment")
    @Operation(
            summary = "POST chuyển hàng hóa từ kho đến sang kho kí gửi.",
            description = "POST chuyển hàng hóa từ kho đến sang kho kí gửi."
    )
    public ApiResponse<?> transferItemsDestinationToConsignment(@RequestBody InventoryTransferDestinationConsignmentDto dto){
        Warehouse warehouse = inventoryItemService.transferItemsDestinationToConsignment(dto);
        ApiResponse<?> apiResponse = ApiResponse.success();
        apiResponse.setMessage("Nhập hàng sang kho " + warehouse.getName() + " thành công.");
        return apiResponse;
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<PageInfoDto<InventoryItemProductionVehicleTypeDto>> searchItemsInWarehouse(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsDto optionsReq) {

        PageInfoDto<InventoryItemProductionVehicleTypeDto> itemPage = inventoryItemService.getItemsFromVehicleWarehouse(warehouseId, optionsReq);
        return ResponseEntity.ok(itemPage);
    }

    @PostMapping("/bulk-insert")
    public ResponseEntity<ApiResponse<?>> bulkCreateItems(
            @Valid @RequestBody List<InventoryItemCreateDto> createDtos) {

        List<InventoryItem> createdItems = inventoryItemService.bulkCreateInventoryItems(createDtos);

        Map<String, Object> result = Map.of("createdCount", createdItems.size());

        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.CREATED);
    }

    @PostMapping("/warehouse/stock-transfer")
    @Operation(
            summary = "POST Điều chuyển nội bộ.",
            description = "POST Điều chuyển nội bộ."
    )
    public ResponseEntity<ApiResponse<?>> stockTransfer(
            @RequestBody InventoryStockTransferDto req
    ){
        Map<String, Object> results = inventoryItemService.stockTransfer(req);
        Warehouse originWarehouse = (Warehouse) results.get("originWarehouse");
        Warehouse destinationWarehouse = (Warehouse) results.get("destinationWarehouse");
        Map<String, ObjectId> dataResponse = Map.of("ticketId", (ObjectId) results.get("ticketId"));
        ApiResponse<?> apiResponse = ApiResponse.success(dataResponse);
        apiResponse.setMessage("Tạo phiên chuyển hàng từ kho "+originWarehouse.getName()+" sang "+destinationWarehouse.getName()+", đang trong quá trình chờ duyệt.");
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "GET Lấy hàng hóa thông qua id.",
            description = "GET Lấy hàng hóa thông qua id."
    )
    public ResponseEntity<ApiResponse<?>> getSingleInventoryItemById(@PathVariable("id") String id){
        InventoryItem item = inventoryItemService.getItemToId(new ObjectId(id));
        InventoryItemPoNumberDto dto = mapper.toInventoryItemPoNumberDto(item);
        return ResponseEntity.ok().body(ApiResponse.success(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "DELETE Xóa một hàng hóa cụ thể.",
            description = "DELETE Xóa một hàng hóa cụ thể."
    )
    public ResponseEntity<ApiResponse<?>> deleteSingleInventoryItem(@PathVariable("id") String id){
        long deletedCount = inventoryItemService.deleteToId(id);
        Map<String, Long> response = Map.of(
                "deletedCount", deletedCount
        );
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @DeleteMapping("/delete-bulk")
    @Operation(
            summary = "DELETE Xóa nhóm hàng hóa cụ thể.",
            description = "DELETE Xóa nhóm hàng hóa cụ thể."
    )
    public ResponseEntity<ApiResponse<?>> deleteSingleInventoryItem(@Valid @RequestBody DeleteBulkInventoryItemDto dto){
        long deletedCount = inventoryItemService.deleteBulk(dto.inventoryItemIds());
        Map<String, Long> response = Map.of(
                "deletedCount", deletedCount
        );
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @PutMapping
    @Operation(
            summary = "PUT Cập nhật một hàng hóa cụ thể.",
            description = "PUT Cập nhật một hàng hóa cụ thể."
    )
    public ResponseEntity<ApiResponse<?>> updateInventoryItem(@Valid @RequestBody UpdateInventoryItemDto dto){
        InventoryItem item = inventoryItemService.updateInventoryItem(dto);
        ApiResponse<?> apiResponse = ApiResponse.success(Map.of("inventoryId", item.getId()));
        apiResponse.setMessage("Cập nhật mặt hàng "+item.getProductCode() + " thành công.");
        return ResponseEntity.ok().body(apiResponse);
    }
}
