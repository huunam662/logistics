package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;

@Data
public class DeliveryItemProductDetails {
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String model;          // Model sản phẩm – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
    // Tải trọng (kg)
    // Độ cao nâng (mm)
    // Động cơ
    // Bình điện
    // Giá trị
    // Giá bán R0
    // Giá bán R1
    // Giá bán thực tế
    // Đại lý
    // Ghi chú
}
