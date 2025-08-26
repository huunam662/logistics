package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Document(collection = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    private ObjectId id;        // _id
    private String name;       // Tên kho (bắt buộc)
    private String address;


    @CreatedBy
    private ObjectId createdBy;

    @LastModifiedBy
    private ObjectId updatedBy;

    private ObjectId deletedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
