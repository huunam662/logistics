package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.Inventory.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.InventoryItemRepository;
import warehouse_management.com.warehouse_management.utils.SimpleMapperUtil;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItem createInventory(CreateInventoryItemReq req) {
        InventoryItem item = new InventoryItem();
        SimpleMapperUtil.mapBasicFields(req, item);

        // Map Specifications
        if (req.getSpecifications() != null) {
            InventoryItem.Specifications spec = new InventoryItem.Specifications();
            SimpleMapperUtil.mapBasicFields(req.getSpecifications(), spec);
            item.setSpecifications(spec);
        }

        // Map Pricing
        if (req.getPricing() != null) {
            InventoryItem.Pricing pricing = new InventoryItem.Pricing();
            SimpleMapperUtil.mapBasicFields(req.getPricing(), pricing);
            item.setPricing(pricing);
        }

        // Map Logistics
        if (req.getLogistics() != null) {
            InventoryItem.Logistics logistics = new InventoryItem.Logistics();
            SimpleMapperUtil.mapBasicFields(req.getLogistics(), logistics);
            item.setLogistics(logistics);
        }

        // Lưu DB
        return inventoryItemRepository.save(item);
    }
}
