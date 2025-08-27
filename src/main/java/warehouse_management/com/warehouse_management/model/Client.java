package warehouse_management.com.warehouse_management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "client")
public class Client {
    @Id
    private String id;

    private String customerId; // Mã khách hàng do user nhập tay

    private String name; // Tên

    private String address; // Địa chỉ

    private String phone; // Điện thoại

    private String email; // Email

    private int level; // Cấp

    private BigDecimal revenue; // Doanh thu = tổng giá đơn hàng đã đặt
}
