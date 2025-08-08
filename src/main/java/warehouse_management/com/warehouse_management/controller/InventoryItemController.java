package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.Inventory.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.service.InventoryItemService;

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

}
