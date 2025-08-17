package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryCentralWarehouseDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm hoặc hàng hóa – Bắt buộc
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc
    private String model;          // Model sản phẩm – Bắt buộc
    private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
    private String type;           // Loại sản phẩm (VD: Xe nâng điện) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private String warehouseType;   // Loại kho
    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String notes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalDate;      // Ngày giao hàng
    private Integer liftingCapacityKg;      // Sức nâng (kg)
    private String chassisType;             // Loại khung nâng
    private Integer liftingHeightMm;        // Độ cao nâng (mm)
    private String engineType;              // Loại động cơ
    private String batteryInfo;             // Thông tin bình điện
    private String batterySpecification;    // Thông số bình điện
    private String forkDimensions;          // Thông số càng
    private String chargerSpecification;    // Thông số bộ sạc
    private Integer valveCount;             // Số lượng van
    private Boolean hasSideShift;           // Có side shift không
    private String otherDetails;            // Chi tiết khác
    private BigDecimal purchasePrice;       // Giá mua vào
    private BigDecimal salePriceR0;         // Giá bán đề xuất R0
    private BigDecimal salePriceR1;         // Giá bán đề xuất R1
    private BigDecimal actualSalePrice;     // Giá bán thực tế
    private String agent;                   // Đại lý (nếu có)
    private String warehouseName;           // Tên kho
    private String warehouseCode;           // Mã kho

}
