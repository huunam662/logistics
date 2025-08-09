package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class InventoryPoWarehouseDto {

    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private ObjectId warehouseId;        // _id
    private String warehouseName;       // Tên kho (bắt buộc)
}
