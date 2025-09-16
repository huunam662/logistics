package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ConfigVehicleSpecPageDto {

    private ObjectId vehicleId;
    private String productCode;
    private String serialNumber;
    private Boolean isFullyComponent;
    private ComponentVehicle liftingFrame;
    private ComponentVehicle battery;
    private ComponentVehicle charger;
    private ComponentVehicle engine;
    private ComponentVehicle fork;
    private ComponentVehicle valve;
    private ComponentVehicle sideShift;

    @Data
    public static class ComponentVehicle{
        private Object value;
        private String serialNumber;
    }


    public Boolean getIsFullyComponent() {
        return liftingFrame.getSerialNumber() != null && battery.getSerialNumber() != null &&
                charger.getSerialNumber() != null && engine.getSerialNumber() != null &&
                fork.getSerialNumber() != null && valve.getSerialNumber() != null &&
                sideShift.getSerialNumber() != null;
    }
}
