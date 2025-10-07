package warehouse_management.com.warehouse_management.dto.quotation_form.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FillSparePartForQuotationDto {

    private String quotationFormId;
    private List<QuotationSparePart> quotationSpareParts = new ArrayList<>();
    private List<QuotationSparePartManual> quotationSparePartManuals = new ArrayList<>();


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotationSparePart{
        private String id;
        private Integer quantity;
        private BigDecimal salePrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotationSparePartManual{
        private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
        private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
        private Integer quantity;   // Số lượng hàng hóa
        private String model;
        private String description;
        private String notes;                  // Ghi chú chung – Không bắt buộc
        private String contractNumber; // Số hợp đồng
        private BigDecimal salePrice;
    }
}
