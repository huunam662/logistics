package warehouse_management.com.warehouse_management.module.warehouse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InventoryProductionRes {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm hoặc hàng hóa – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String model;          // Model sản phẩm – Bắt buộc
    private String type;           // Loại sản phẩm (VD: Xe nâng điện) – Bắt buộc
    private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private ObjectId warehouseId;  // _id của warehouse – Có nếu đang ở kho
    private ObjectId containerId;  // _id của container – Có nếu đang trong container
    private InventoryProductionRes.Logistics logistics;
    private InventoryItem.Specifications specifications; // Thông số kỹ thuật – Không bắt buộc
    private InventoryItem.Pricing pricing;               // Giá cả – Không bắt buộc
    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private LocalDateTime createdAt;       // Thời gian tạo – Bắt buộc
    private LocalDateTime updatedAt;       // Thời gian cập nhật cuối – Bắt buộc
    private Warehouse warehouse; // Kho lưu trữ mặt hàng này

    @Data
    @NoArgsConstructor
    public static class Logistics {
        private LocalDateTime orderDate;        // Ngày đặt hàng
        private LocalDateTime departureDate;    // Ngày khởi hành
        private LocalDateTime plannedProductionDate; // Ngày dự kiến sản xuất
        private LocalDateTime estimateCompletionDate; // Ngày dự kiến sản xuất xong
    }
}
