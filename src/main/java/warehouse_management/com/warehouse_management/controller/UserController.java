package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserIDto;
import warehouse_management.com.warehouse_management.service.UserService;

import java.util.List;

@RestController
@Tag(name = "User")
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/by-role/{roleCode}")
    @Operation(
            summary = "GET users by role name",
            description = "Lấy danh sách users theo role name từ .NET API. " +
                    "Sử dụng intercode GET_USERS_BY_ROLE."
    )
    public ApiResponse<List<UserIDto>> getUsersByRole(
            @PathVariable("roleCode") String roleCode
    ) {
        // Get users by role từ Service (đã có for loop test bên trong)
        List<UserIDto> users = userService.getUsersByRole(roleCode);
        
        return ApiResponse.success(users);
    }
}
