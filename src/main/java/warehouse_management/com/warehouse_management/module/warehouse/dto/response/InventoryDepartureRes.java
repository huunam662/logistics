package warehouse_management.com.warehouse_management.module.warehouse.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InventoryDepartureRes {
    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm hoặc hàng hóa – Bắt buộc
    private LocalDateTime orderDate;        // Ngày đặt hàng
    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private String model;          // Model sản phẩm – Bắt buộc
    private String type;           // Loại sản phẩm (VD: Xe nâng điện) – Bắt buộc
    private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private InventoryItem.Specifications specifications; // Thông số kỹ thuật – Không bắt buộc
    private InventoryDepartureRes.Pricing pricing; // Giá cả – Không bắt buộc
    private InventoryDepartureRes.Logistics logistics; // Thông tin vận chuyển – Không bắt buộc
    private Warehouse warehouse; // Kho lưu trữ mặt hàng này
    private InventoryDepartureRes.Container container; // Container được vận chuyển có chứa mặt hàng này


    @Data
    @NoArgsConstructor
    public static class Container{
        private ObjectId id;  // _id – Khóa chính
        private String containerStatus;     // EMPTY, LOADING, IN_TRANSIT, COMPLETED
        private LocalDateTime departureDate;  // Ngày khởi hành
        private LocalDateTime arrivalDate;    // Ngày đến nơi
        private Warehouse toWarehouse;
    }

    @Data
    @NoArgsConstructor
    public static class Pricing{
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
    }

    @Data
    @NoArgsConstructor
    public static class Logistics{
        private LocalDateTime orderDate;        // Ngày đặt hàng
        private LocalDateTime departureDate;    // Ngày khởi hành
        private LocalDateTime arrivalDate;      // Ngày đến
    }
}
