package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class InventoryPoWarehouseRes {

    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private InventoryPoWarehouseRes.Warehouse warehouse;

    @Data
    @NoArgsConstructor
    private static class Warehouse{
        private ObjectId id;        // _id
        private String name;       // Tên kho (bắt buộc)
    }
}
