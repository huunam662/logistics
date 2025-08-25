package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeliveryOrderSparePartTicksDto {
    private Integer totalSparePartsDelivery;
    private List<DeliverySparePartTickDto> sparePartTicks = new ArrayList<>();
    private List<BackDeliverySparePartModelDto> backDeliverySparePartModels = new ArrayList<>();

    public Integer getTotalSparePartsDelivery() {
        return sparePartTicks.size() + backDeliverySparePartModels.size();
    }
}
