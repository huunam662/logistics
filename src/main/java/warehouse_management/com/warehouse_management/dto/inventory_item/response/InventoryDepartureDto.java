package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryDepartureDto {
    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // PO
    private String status;      // Trạng thái
    private String productCode;    // Mã sản phẩm
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;        // Ngày đặt hàng
    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private String containerStatus;     // Trạng Thái Container
    private String containerCode;       // Mã Cont
    private String containerToWarehouse;    // Kho đến
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime containerDepartureDate;  //  Ngày đi
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime containerArrivalDate;    // Ngày đến
    private String model;          // Model
    private String type;           // Loại
    private String category;       // Chủng loại
    private Boolean hasSideShift;           // SS (Side Shift)
    private String serialNumber;   // Số seri
    private Integer liftingCapacityKg;      // Sức nâng (kg)
    private String chassisType;             // Loại khung nâng
    private Integer liftingHeightMm;        // Độ cao nâng (mm)
    private String engineType;              // Loại động cơ
    private String batteryInfo;             // Bình điện
    private String batterySpecification;    // Thông số bình điện
    private String chargerSpecification;    // Thông số bộ sạc
    private String forkDimensions;          // Thông số càng
    private String warehouseType;           // Loại kho
    private Integer valveCount;             // Số lượng van
    private String otherDetails;            // Chi tiết khác
    private String initialCondition;       // Nguyên trạng
    private String notes;                  // Ghi chú
    private BigDecimal purchasePrice;       // Giá mua
    private BigDecimal salePriceR0;         // Giá R0
    private BigDecimal salePriceR1;         // Giá R1
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalDate;    // Ngày giao hàng
    private BigDecimal actualSalePrice;     // Giá bán thực tế
}
