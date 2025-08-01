package warehouse_management.com.warehouse_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.model.permission.Permission;
import warehouse_management.com.warehouse_management.model.Role;
import warehouse_management.com.warehouse_management.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private RoleRepository roleRepository;

    public boolean hasPermission(String userId, String resource, String httpMethod) {
        List<Role> roles = roleRepository.findRolesByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(perm ->
                        perm.getType() == Permission.PermissionType.API &&
                                perm.getResource().equals(resource) &&
                                perm.getHttpMethod().equalsIgnoreCase(httpMethod)
                );
    }

    public List<Permission> getUIPermissions(String userId) {
        List<Role> roles = roleRepository.findRolesByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .filter(perm -> perm.getType() == Permission.PermissionType.UI)
                .collect(Collectors.toList());
    }
}
