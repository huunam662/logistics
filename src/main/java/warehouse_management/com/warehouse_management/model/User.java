package warehouse_management.com.warehouse_management.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private List<String> roleIds;

    private boolean enabled = true;
    private List<GrantedAuthority> authorities;
}

