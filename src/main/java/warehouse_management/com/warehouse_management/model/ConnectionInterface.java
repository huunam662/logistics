package warehouse_management.com.warehouse_management.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "conn_interface")
@Data
public class ConnectionInterface {
    @Id
    private ObjectId id;

    private String interfaceCode;

    private String interfaceURL;


}
