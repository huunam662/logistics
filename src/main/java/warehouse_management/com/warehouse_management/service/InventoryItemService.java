package warehouse_management.com.warehouse_management.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.Inventory.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.mapper.warehouse.WarehouseMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.InventoryItemRepository;
import warehouse_management.com.warehouse_management.utils.SimpleMapperUtil;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItem createInventory(CreateInventoryItemReq req) {
        InventoryItem item = mapper.toInventoryItemModel(req);
        // Lưu DB
        return inventoryItemRepository.save(item);
//        return null;
    }
}
