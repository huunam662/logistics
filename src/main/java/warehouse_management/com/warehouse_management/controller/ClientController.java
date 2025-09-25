package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.client.request.CreateClientDto;
import warehouse_management.com.warehouse_management.dto.client.request.UpdateClientDto;
import warehouse_management.com.warehouse_management.dto.client.response.ClientDto;
import warehouse_management.com.warehouse_management.dto.client.response.ClientIdDto;
import warehouse_management.com.warehouse_management.service.ClientService;

import java.util.List;

@RestController
@Tag(name = "Client")
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "GET danh sách clients", description = "Lấy toàn bộ danh sách clients")
    public ApiResponse<List<ClientDto>> getAllClients(
            @RequestParam(value = "name", required = false, defaultValue = "")
            String name,
            @RequestParam(value = "email", required = false, defaultValue = "")
            String email
    ) {
        return (ApiResponse.success(clientService.getAllClients(name, email)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "GET client theo ID", description = "Lấy thông tin chi tiết client theo ID")
    public ApiResponse<ClientDto> getClientById(@PathVariable String id) {
        return ApiResponse.success(clientService.getClientById(new ObjectId(id)));

    }

    @PostMapping
    @Operation(summary = "POST tạo client mới", description = "Tạo mới một client")
    public ApiResponse<ClientIdDto> createClient(@Valid @RequestBody CreateClientDto client) {
        return ApiResponse.success(clientService.createClient(client));
    }

    @PutMapping("/{id}")
    @Operation(summary = "PUT cập nhật client", description = "Cập nhật thông tin client theo ID")
    public ApiResponse<ClientIdDto> updateClient(
            @PathVariable("id") String id,
            @RequestBody UpdateClientDto client) {
        return ApiResponse.success(
                clientService.updateClient(new ObjectId(id), client)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "DELETE client", description = "Xóa client theo ID")
    public ApiResponse<Boolean> deleteClient(@PathVariable String id) {
        return ApiResponse.success(
                clientService.softDeleteClient(new ObjectId(id))
        );
    }
}
