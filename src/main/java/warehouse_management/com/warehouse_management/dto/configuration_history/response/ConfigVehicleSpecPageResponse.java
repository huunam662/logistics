package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ConfigVehicleSpecPageResponse {

    private ObjectId vehicleId;
    private String productCode;
    private Boolean isFullyComponent;
    private ComponentVehicle liftingFrame;
    private ComponentVehicle battery;
    private ComponentVehicle charger;
    private ComponentVehicle engine;
    private ComponentVehicle fork;
    private ComponentVehicle valve;
    private ComponentVehicle sideShift;

    public Boolean getFullyComponent() {
        return liftingFrame == null ||
                battery == null ||
                charger == null ||
                engine == null ||
                fork == null ||
                valve == null ||
                sideShift == null;
    }

    @Data
    public static class ComponentVehicle{

        private Object value;
        private String inventoryType;
        private String serialNumber;
    }

}
