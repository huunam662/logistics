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

    @Data
    public static class ComponentVehicle{
        private Object value;
        private Object serialNumber;
    }


    public Boolean getIsFullyComponent() {
        return liftingFrame != null && liftingFrame.getValue() != null && liftingFrame.getSerialNumber() != null &&
                battery != null && battery.getValue() != null && battery.getSerialNumber() != null &&
                charger != null && charger.getValue() != null && charger.getSerialNumber() != null &&
                engine != null && engine.getValue() != null && engine.getSerialNumber() != null &&
                fork != null && fork.getValue() != null && fork.getSerialNumber() != null &&
                valve != null && valve.getValue() != null && valve.getSerialNumber() != null &&
                sideShift != null && sideShift.getValue() != null && sideShift.getSerialNumber() != null;
    }
}
