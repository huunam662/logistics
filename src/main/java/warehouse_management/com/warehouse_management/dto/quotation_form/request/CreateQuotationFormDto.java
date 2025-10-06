package warehouse_management.com.warehouse_management.dto.quotation_form.request;

import lombok.Data;

@Data
public class CreateQuotationFormDto {

    private String quotationCode;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerEmail;
    private String customerLevel;

}
