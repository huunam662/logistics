package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeliveryOrderProductTicksDto {
    private Integer totalProductsDelivery;
    private List<DeliveryProductTickDto> productTicks = new ArrayList<>();
    private List<BackDeliveryProductModelDto> backDeliveryProductModels = new ArrayList<>();

    public Integer getTotalProductsDelivery() {
        return productTicks.size() + backDeliveryProductModels.size();
    }
}
