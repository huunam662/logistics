package warehouse_management.com.warehouse_management.dto.quotation_form.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class QuotationCustomerDto {

    private ObjectId id;
    private String name;
    private Integer level;
    private String code; // Mã khách hàng do user nhập tay
    private String address; // Địa chỉ
    private String phone; // Điện thoại
    private String email; // Email


}
