package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Embedded user information for notifications
 * Based on CustomUserDetail structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    
    private String id;
    
    private String email;
    
    private String fullName;
    
    private List<String> permissions;
}
