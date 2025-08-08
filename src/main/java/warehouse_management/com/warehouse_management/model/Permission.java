package warehouse_management.com.warehouse_management.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "permission")
@Getter
public class Permission {
    @Id
    private String id;
    private String name;
    private String resource;  // "users", "products"
    private List<String> actions; // ["create", "read", "update", "delete"]
    private PermissionType type; // UI or API
    private String httpMethod; // For API: GET, POST, etc.
    private String uiComponent; // For UI: "delete_button"

    public enum PermissionType { UI, API }
}