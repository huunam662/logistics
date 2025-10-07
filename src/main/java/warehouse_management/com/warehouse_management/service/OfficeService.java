package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.integration.office.client.OfficeIntegrationClient;
import warehouse_management.com.warehouse_management.integration.office.dto.request.CreateOfficeFromWarehouseIReq;
import warehouse_management.com.warehouse_management.integration.office.dto.response.CreateOfficeFromWarehouseIRes;
import warehouse_management.com.warehouse_management.integration.office.dto.response.OfficeIDto;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

@Service
@RequiredArgsConstructor
public class OfficeService {

    private final OfficeIntegrationClient officeIntegrationClient;
    private final CustomAuthentication customAuthentication;

    /**
     * Tạo office từ warehouse
     */
    public OfficeIDto createOfficeFromWarehouse(CreateOfficeFromWarehouseIReq request) {
        CreateOfficeFromWarehouseIRes response = officeIntegrationClient.createOfficeFromWarehouse(
                customAuthentication.getUser().getAnatk(), 
                request
        );
        
        // Check success ở tầng service
        if (!response.getSuccess()) {
            throw LogicErrException.of("Lỗi tạo office từ warehouse: API trả về success = false");
        }
        
        return response.getData();
    }
}
