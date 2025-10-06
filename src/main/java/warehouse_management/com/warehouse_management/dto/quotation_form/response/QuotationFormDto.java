package warehouse_management.com.warehouse_management.dto.quotation_form.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class QuotationFormDto {

    private ObjectId id;
    private String quotationCode;
    private ObjectId customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerEmail;
    private String customerLevel;

    private String createdBy;
    private LocalDateTime createdAt;
}
