package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserListRes;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserDto;
import warehouse_management.com.warehouse_management.service.UserService;

import java.util.List;

@RestController
@Tag(name = "User")
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/by-role/{roleName}")
    @Operation(
            summary = "GET users by role name",
            description = "Lấy danh sách users theo role name từ .NET API. " +
                    "Sử dụng intercode GET_USERS_BY_ROLE."
    )
    public ApiResponse<List<UserDto>> getUsersByRole(
            @PathVariable("roleName") String roleName
    ) {
        // Get users by role từ .NET API
        UserListRes userListRes = userService.getUsersByRole(roleName);
        List<UserDto> users = userListRes.getData().getCollection();
        
        // Test loop - sẽ hoạt động vì UserDto đã được deserialize đúng cách
        for(UserDto u : users) {
            System.out.println(u.getEmail());
        }
        
        return ApiResponse.success(users);
    }
}
