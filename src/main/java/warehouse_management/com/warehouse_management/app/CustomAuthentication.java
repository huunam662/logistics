package warehouse_management.com.warehouse_management.app;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;

@Component
public class CustomAuthentication {

    public CustomUserDetail getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // Kiểm tra xem Principal có phải là UserDetails không
        if (authentication.getPrincipal() instanceof CustomUserDetail) {
            return (CustomUserDetail) authentication.getPrincipal();
        }

        // Trường hợp Principal không phải UserDetails (ví dụ: là String "anonymousUser")
        return null;
    }

    public CustomUserDetail getUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem Principal có phải là UserDetails không
        if (
                authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetail customUserDetail
        ) {
            return customUserDetail;
        }

        // Trường hợp Principal không phải UserDetails (ví dụ: là String "anonymousUser")
        throw LogicErrException.of("Hãy đăng nhập để xác thực người dùng.");
    }


}
