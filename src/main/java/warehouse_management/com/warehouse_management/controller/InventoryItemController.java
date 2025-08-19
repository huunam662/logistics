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
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportDestinationProductDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportDestinationSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionProductDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
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

    @GetMapping("/{id}/product")
    @Operation(
            summary = "GET Lấy thông tin của một Xe hoặc Phụ kiện cụ thể.",
            description = "GET Lấy thông tin của một Xe hoặc Phụ kiện cụ thể."
    )
    public ResponseEntity<?> getInventoryProduct(@PathVariable("id") String id){
        InventoryProductDetailsDto dto = inventoryItemService.getInventoryProductDetails(new ObjectId(id));
        return ResponseEntity.ok().body(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/spare-part")
    @Operation(
            summary = "GET Lấy thông tin của một Phụ tùng cụ thể.",
            description = "GET Lấy thông tin của một Phụ tùng cụ thể."
    )
    public ResponseEntity<?> getInventorySparePart(@PathVariable("id") String id){
        InventorySparePartDetailsDto dto = inventoryItemService.getInventorySparePartDetails(new ObjectId(id));
        return ResponseEntity.ok().body(ApiResponse.success(dto));
    }

    // Api Nhập kho
    @PostMapping("/product")
    @Operation(
            summary = "API Nhập Xe hoặc Phụ kiện vào Kho.",
            description = "API Nhập Xe hoặc Phụ kiện vào Kho."
    )
    public ResponseEntity<?> createInventoryItem(@Valid @RequestBody CreateInventoryProductDto req) {
        InventoryItem savedItem = inventoryItemService.createInventoryProduct(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("inventoryItemId", savedItem.getId())));
    }


    @PutMapping("/{id}/product")
    @Operation(
            summary = "PUT Cập nhật một hàng hóa cụ thể.",
            description = "PUT Cập nhật một hàng hóa cụ thể."
    )
    public ResponseEntity<ApiResponse<?>> updateInventoryItem(
            @PathVariable("id") String id,
            @Valid @RequestBody CreateInventoryProductDto dto
    ){
        InventoryItem item = inventoryItemService.updateInventoryProduct(id, dto);
        ApiResponse<?> apiResponse = ApiResponse.success(Map.of("inventoryId", item.getId()));
        apiResponse.setMessage("Cập nhật mặt hàng "+item.getProductCode() + " thành công.");
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/spare-part")
    @Operation(
            summary = "POST Nhập phụ tùng vào kho.",
            description = "POST Nhập phụ tùng vào kho."
    )
    public ResponseEntity<?> createInventorySparePart(@Valid @RequestBody CreateInventorySparePartDto req){
        InventoryItem savedItem = inventoryItemService.createInventorySparePart(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("inventoryItemId", savedItem.getId())));
    }

    @PutMapping("/{id}/spare-part")
    @Operation(
            summary = "PUT Cập nhật phụ tùng vào kho.",
            description = "PUT Cập nhật phụ tùng vào kho."
    )
    public ResponseEntity<?> updateInventorySparePart(
            @PathVariable("id") String id,
            @Valid @RequestBody CreateInventorySparePartDto req
    ){
        InventoryItem savedItem = inventoryItemService.updateInventorySparePart(id, req);
        return ResponseEntity.ok(ApiResponse.success(Map.of("inventoryItemId", savedItem.getId())));
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
            @RequestParam("inventoryType") List<String> inventoryTypes,
            @RequestParam(value = "poNumber", required = false, defaultValue = "") String poNumber,
            @RequestParam(value = "warehouseId", required = false) String warehouseId
    ){
        List<InventoryPoWarehouseDto> poNumbers = inventoryItemService.getInventoryInStockPoNumbers(warehouseType, inventoryTypes, poNumber, warehouseId);
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

    // Bulk insert kho production -  xe phụ kiện (import Excel)
// Bulk insert kho production - xe phụ kiện (import Excel)
    @PostMapping("/production/products/import")
    public ResponseEntity<ApiResponse<?>> bulkCreateProductionProducts(
            @RequestBody List<ExcelImportProductionProductDto> dtos) {
        List<InventoryItem> created = inventoryItemService.bulkCreateProductionProductItems(dtos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("createdCount", created.size())));
    }

    //     Bulk insert kho production - phụ tùng (import Excel)
    @PostMapping("/production/spare-parts/import")
    public ResponseEntity<ApiResponse<?>> bulkCreateProductionSpareParts(
            @Valid @RequestBody List<ExcelImportProductionSparePartDto> dtos) {
        List<InventoryItem> created = inventoryItemService.bulkCreateProductionSparePartItems(dtos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("createdCount", created.size())));
    }

    // Bulk insert kho destination - xe phụ kiện (import Excel)
    @PostMapping("/destination/products/import")
    public ResponseEntity<ApiResponse<?>> bulkCreateDestinationProducts(
            @Valid @RequestBody List<ExcelImportDestinationProductDto> dtos) {
        List<InventoryItem> created = inventoryItemService.bulkCreateDestinationProductItems(dtos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("createdCount", created.size())));
    }

    // Bulk insert kho destination - phụ tùng (import Excel)
    @PostMapping("/destination/spare-parts/import")
    public ResponseEntity<ApiResponse<?>> bulkCreateDestinationSpareParts(
            @Valid @RequestBody List<ExcelImportDestinationSparePartDto> dtos) {
        List<InventoryItem> created = inventoryItemService.bulkCreateDestinationSparePartItems(dtos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of("createdCount", created.size())));
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

}
