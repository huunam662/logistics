package warehouse_management.com.warehouse_management.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "client")
@Data
public class Client implements Persistable<ObjectId> {
    @Id
    private ObjectId id;

    private String customerCode; // Mã khách hàng do user nhập tay

    private String name; // Tên
    @CreatedBy
    private String createdBy;

    private String address; // Địa chỉ
     @LastModifiedBy
    private String updatedBy;

    private String phone; // Điện thoại
    private ObjectId deletedBy;

    private String email; // Email
    @CreatedDate
    private LocalDateTime createdAt;

    private String level; // Cấp
    private LocalDateTime deletedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private BigDecimal revenue; // Doanh thu = tổng giá đơn hàng đã đặt

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
