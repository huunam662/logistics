package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.auth.request.LoginRequestDto;
import warehouse_management.com.warehouse_management.dto.auth.response.LoginResponseDto;
import warehouse_management.com.warehouse_management.service.AuthService;

@RestController
@Tag(name = "Client controller")
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "")
    public ApiResponse<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        LoginResponseDto res = authService.login(loginRequest);
        return ApiResponse.success(res);

    }
}
