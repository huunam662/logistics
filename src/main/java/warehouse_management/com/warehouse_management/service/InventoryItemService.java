package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.InventoryItemRepository;


@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;


    public InventoryItem createInventoryItem(CreateInventoryItemReq req) {
        InventoryItem item = mapper.toInventoryItemModel(req);
        // Lưu DB
        return inventoryItemRepository.save(item);
//        return null;
    }
}
