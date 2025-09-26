package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryProductionDto {
    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // PO      //*
    
    private LocalDateTime orderDate;        // Ngày đặt hàng
    private String productCode;    // Mã sản phẩm       //*
    private String model;          // Model                 //*
    private String category;       // Chủng loại            //*
    private String hasSideShift;           // SS (Side Shift)
    private String serialNumber;   // Số seri           //*
    private String inventoryType;   // Loại         //*
    private String liftingCapacityKg;      // Sức nâng (kg)        //*
    private String chassisType;             // Loại khung nâng
    private String liftingHeightMm;        // Độ cao nâng (mm)     //*
    private String engineType;              // Loại động cơ     //*
    private String batteryInfo;             // Bình điện        //*
    private String batterySpecification;    // Thông số bình điện       //*
    private String chargerSpecification;    // Thông số bộ sạc
    private String forkDimensions;          // Thông số càng
    private String warehouseType;           // Loại kho
    private String valveCount;             // Số lượng van
    private String otherDetails;            // Chi tiết khác
    private Boolean initialCondition;       // Nguyên trạng
    private String notes;                  // Ghi chú
    private BigDecimal purchasePrice;       // Giá mua
    private BigDecimal salePriceR0;         // Giá R0
    private BigDecimal salePriceR1;         // Giá R1
    private BigDecimal actualSalePrice;     // Giá bán thực tế
    private String agent;                   // Đại lý (nếu có)
}
