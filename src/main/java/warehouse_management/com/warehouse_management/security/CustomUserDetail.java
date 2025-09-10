package warehouse_management.com.warehouse_management.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CustomUserDetail implements UserDetails {
    private String id;
    private final String email;
    private final List<String> permisions;
    private final String fullName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permisions == null) return Collections.emptyList();
        return permisions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null; // Không cần password vì authen bên ngoài
    }

    @Override
    public String getUsername() {
        return email; // Dùng email làm username, hoặc đổi thành userId nếu muốn
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getId() {
        return id;
    }


}