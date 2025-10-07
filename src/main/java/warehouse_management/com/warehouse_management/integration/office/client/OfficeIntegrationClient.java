package warehouse_management.com.warehouse_management.integration.office.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.integration.IntegrationException;
import warehouse_management.com.warehouse_management.integration.anabase.GenericIntegrationClient;
import warehouse_management.com.warehouse_management.integration.office.dto.request.CreateOfficeFromWarehouseReq;
import warehouse_management.com.warehouse_management.integration.office.dto.response.CreateOfficeFromWarehouseRes;
import warehouse_management.com.warehouse_management.integration.office.dto.response.OfficeDto;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

@Component
@RequiredArgsConstructor
public class OfficeIntegrationClient {

    private final GenericIntegrationClient genericIntegrationClient;

    /**
     * Tạo office từ warehouse
     */
    public CreateOfficeFromWarehouseRes createOfficeFromWarehouse(String token, CreateOfficeFromWarehouseReq request) {

        return genericIntegrationClient.post(
                GeneralUtil.CREATE_OFFICE_FROM_WAREHOUSE,
                token,
                request,
                CreateOfficeFromWarehouseRes.class
        );


    }
}
