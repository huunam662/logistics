package warehouse_management.com.warehouse_management.dto.quotation_form.request;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class UpdateQuotationFormDto {

    private String id;
    private String quotationCode;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerEmail;
    private String customerLevel;
}
