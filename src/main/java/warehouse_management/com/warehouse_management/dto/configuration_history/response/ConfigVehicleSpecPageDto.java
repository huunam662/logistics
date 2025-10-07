package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ConfigVehicleSpecPageDto {

    private ObjectId vehicleId;
    private String productCode;
    private String model;
    private String serialNumber;
    private Boolean isFullyComponent;
    private Boolean initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private ComponentVehicle liftingFrame;
    private ComponentVehicle battery;
    private ComponentVehicle charger;
    private ComponentVehicle engine;
    private ComponentVehicle fork;
    private ComponentVehicle valve;
    private ComponentVehicle sideShift;
    private ComponentVehicle wheel;

    @Data
    public static class ComponentVehicle{
        private Object value;
        private String serialNumber;
        private String configStatus;
        private String configType;
        private String configComponentType;
    }

}
