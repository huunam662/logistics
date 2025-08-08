package warehouse_management.com.warehouse_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.Inventory.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.InventoryItemRepository;
import warehouse_management.com.warehouse_management.service.InventoryItemService;
import warehouse_management.com.warehouse_management.utils.SimpleMapperUtil;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/inventory-items")
@RequiredArgsConstructor
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;

    //Api Nhập kho
    @PostMapping
    public ResponseEntity<?> createInventory(@Valid @RequestBody CreateInventoryItemReq req) {
        InventoryItem savedItem = inventoryItemService.createInventory(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedItem));
    }

}
