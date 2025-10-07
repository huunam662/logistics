package warehouse_management.com.warehouse_management.integration.office.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.integration.anabase.GenericIntegrationClient;
import warehouse_management.com.warehouse_management.integration.office.dto.request.CreateOfficeFromWarehouseIReq;
import warehouse_management.com.warehouse_management.integration.office.dto.response.CreateOfficeFromWarehouseIRes;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

@Component
@RequiredArgsConstructor
public class OfficeIntegrationClient {

    private final GenericIntegrationClient genericIntegrationClient;

    /**
     * Tạo office từ warehouse
     */
    public CreateOfficeFromWarehouseIRes createOfficeFromWarehouse(String token, CreateOfficeFromWarehouseIReq request) {

        return genericIntegrationClient.post(
                GeneralUtil.CREATE_OFFICE_FROM_WAREHOUSE,
                token,
                request,
                CreateOfficeFromWarehouseIRes.class
        );


    }
}
