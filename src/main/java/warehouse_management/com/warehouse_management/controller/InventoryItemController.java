package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryTransferWarehouseReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryPoWarehouseRes;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.service.InventoryItemService;
import java.util.List;

@RestController
@RequestMapping("/inventory-items")
@RequiredArgsConstructor
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;

    //Api Nhập kho
    @PostMapping
    @Operation(
            summary = "API Nhập Kho"
    )
    public ResponseEntity<?> createInventoryItem(@Valid @RequestBody CreateInventoryItemReq req) {
        InventoryItem savedItem = inventoryItemService.createInventoryItem(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedItem));
    }

    @GetMapping("/production/po-numbers")
    @Operation(
            summary = "GET lấy danh sách PO NUMBER theo loại kho.",
            description = "GET lấy danh sách PO NUMBER theo loại kho."
    )
    public ApiResponse<?> getInventoryInStockPoNumbers(
            @Parameter(description = "[PRODUCTION, DEPARTURE, DESTINATION, CONSIGNMENT]")
            @RequestParam String warehouseType,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(required = false) Sort.Direction direction
    ){
        List<InventoryPoWarehouseRes> poNumbers = inventoryItemService.getInventoryInStockPoNumbers(warehouseType, filter, sortBy, direction);
        return ApiResponse.success(poNumbers);
    }

    @GetMapping("/production/po-items")
    @Operation(
            summary = "GET lấy danh sách hàng hóa thuộc PO theo loại kho.",
            description = "GET lấy danh sách hàng hóa thuộc PO theo loại kho."
    )
    public ApiResponse<?> getInventoryInStockPoNumbers(
            @Parameter(description = "[PRODUCTION, DEPARTURE, DESTINATION, CONSIGNMENT]")
            @RequestParam String warehouseType,
            @RequestParam String poNumber,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(required = false) Sort.Direction direction
    ){
        List<InventoryItem> poNumbers = inventoryItemService.getInventoryInStockByPoNumber(warehouseType, poNumber, filter, sortBy, direction);
        return ApiResponse.success(poNumbers);
    }

    @PostMapping("/transfer/production-to-departure")
    @Operation(
            summary = "POST chuyển hàng hóa từ kho chờ sản xuất sang kho đi.",
            description = "POST chuyển hàng hóa từ kho chờ sản xuất sang kho đi."
    )
    public ApiResponse<?> transferItemsProductionToDeparture(
            @RequestBody InventoryTransferWarehouseReq req
    ){
        Warehouse warehouse = inventoryItemService.transferItemsProductionToDeparture(req);
        ApiResponse<?> apiResponse = ApiResponse.success();
        apiResponse.setMessage("Nhập hàng sang kho " + warehouse.getName() + " thành công.");
        return apiResponse;
    }

}
