package warehouse_management.com.warehouse_management.dto.quotation_form.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteQuotationDto {

    private List<String> quotationIds;

}
