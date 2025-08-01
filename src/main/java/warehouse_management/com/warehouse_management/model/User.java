package warehouse_management.com.warehouse_management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private List<String> roleIds;

    private boolean enabled = true;
    private List<GrantedAuthority> authorities;
}

