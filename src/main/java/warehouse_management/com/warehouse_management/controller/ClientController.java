package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.client.request.CreateClientReq;
import warehouse_management.com.warehouse_management.dto.client.request.UpdateClientReq;
import warehouse_management.com.warehouse_management.dto.client.response.ClientRes;
import warehouse_management.com.warehouse_management.dto.client.response.CreateClientRes;
import warehouse_management.com.warehouse_management.service.ClientService;

import java.util.List;

@RestController
@Tag(name = "Client controller")
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "GET danh sách clients", description = "Lấy toàn bộ danh sách clients")
    public ApiResponse<List<ClientRes>> getAllClients() {
        return (ApiResponse.success(clientService.getAllClients()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "GET client theo ID", description = "Lấy thông tin chi tiết client theo ID")
    public ApiResponse<ClientRes> getClientById(@PathVariable String id) {
        return ApiResponse.success(clientService.getClientById(new ObjectId(id)));

    }

    @PostMapping
    @Operation(summary = "POST tạo client mới", description = "Tạo mới một client")
    public ApiResponse<CreateClientRes> createClient(@Valid @RequestBody CreateClientReq client) {
        return ApiResponse.success(clientService.createClient(client));
    }

    @PutMapping("/{id}")
    @Operation(summary = "PUT cập nhật client", description = "Cập nhật thông tin client theo ID")
    public ApiResponse<CreateClientRes> updateClient(
            @PathVariable("id") String id,
            @RequestBody UpdateClientReq client) {
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
