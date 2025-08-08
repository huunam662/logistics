package warehouse_management.com.warehouse_management.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "role")
@Getter
public class Role {
    @Id
    private String id;
    private String name;
    private List<Permission> permissions;
}