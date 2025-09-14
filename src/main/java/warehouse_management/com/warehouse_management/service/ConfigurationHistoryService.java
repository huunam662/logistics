package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.AssemblePartRequest;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.DropPartRequest;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.VehiclePartSwapRequest;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecHistoryResponse;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecPageResponse;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigurationHistoryResponse;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.ChangeConfigurationType;
import warehouse_management.com.warehouse_management.enumerate.ComponentType;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.ConfigurationHistoryMapper;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.configuration_history.ConfigurationHistoryRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ConfigurationHistoryService {


    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemService inventoryItemService;
    private final ConfigurationHistoryRepository configurationHistoryRepository;
    private final WarehouseService warehouseService;
    private final InventoryItemMapper inventoryItemMapper;
    private final ConfigurationHistoryMapper configurationVehicleMapper;


    @Transactional
    public boolean swapItems(VehiclePartSwapRequest request) {
        // 1. Lấy vehicle và accessory tương ứng
        InventoryItem leftVeh = inventoryItemService.findByIdOrThrow(request.getLeftVehicleId());
        InventoryItem rightVeh = inventoryItemService.findByIdOrThrow(request.getRightVehicleId());

        ComponentType componentType = ComponentType.fromId(request.getComponentType());
        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        InventoryItem leftVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(leftVeh.getId(), componentType, leftVeh.getProductCode());
        InventoryItem rightVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(rightVeh.getId(), componentType, rightVeh.getProductCode());
        inventoryItemRepository.updateVehicleIdById(leftVehComponent.getId(), rightVeh.getId());
        inventoryItemRepository.updateVehicleIdById(rightVehComponent.getId(), leftVeh.getId());

        swapVehicleComponent(leftVeh, rightVeh, componentType);
        setVehiclePrices(leftVeh, rightVeh, request);
        buildSwapHistory(leftVeh, rightVeh, leftVehComponent, rightVehComponent, componentType);
        buildSwapHistory(rightVeh, leftVeh, rightVehComponent, leftVehComponent, componentType);

        return true;
    }

// --------------------------- Helpers ---------------------------

    private void swapVehicleComponent(InventoryItem leftVeh, InventoryItem rightVeh, ComponentType componentType) {
        switch (componentType) {
            case LIFTING_FRAME -> {
                Integer tmpHeight = leftVeh.getSpecifications().getLiftingHeightMm();
                leftVeh.getSpecifications().setLiftingHeightMm(rightVeh.getSpecifications().getLiftingHeightMm());
                rightVeh.getSpecifications().setLiftingHeightMm(tmpHeight);

                Integer tmpCapacity = leftVeh.getSpecifications().getLiftingCapacityKg();
                leftVeh.getSpecifications().setLiftingCapacityKg(rightVeh.getSpecifications().getLiftingCapacityKg());
                rightVeh.getSpecifications().setLiftingCapacityKg(tmpCapacity);

                String tmpChassis = leftVeh.getSpecifications().getChassisType();
                leftVeh.getSpecifications().setChassisType(rightVeh.getSpecifications().getChassisType());
                rightVeh.getSpecifications().setChassisType(tmpChassis);
            }
            case BATTERY -> {
                String tmpInfo = leftVeh.getSpecifications().getBatteryInfo();
                leftVeh.getSpecifications().setBatteryInfo(rightVeh.getSpecifications().getBatteryInfo());
                rightVeh.getSpecifications().setBatteryInfo(tmpInfo);

                String tmpSpec = leftVeh.getSpecifications().getBatterySpecification();
                leftVeh.getSpecifications().setBatterySpecification(rightVeh.getSpecifications().getBatterySpecification());
                rightVeh.getSpecifications().setBatterySpecification(tmpSpec);
            }
            case CHARGER -> {
                String tmpSpec = leftVeh.getSpecifications().getChargerSpecification();
                leftVeh.getSpecifications().setChargerSpecification(rightVeh.getSpecifications().getChargerSpecification());
                rightVeh.getSpecifications().setChargerSpecification(tmpSpec);
            }
            case ENGINE -> {
                String tmpEngine = leftVeh.getSpecifications().getEngineType();
                leftVeh.getSpecifications().setEngineType(rightVeh.getSpecifications().getEngineType());
                rightVeh.getSpecifications().setEngineType(tmpEngine);
            }
            case FORK -> {
                String tmpFork = leftVeh.getSpecifications().getForkDimensions();
                leftVeh.getSpecifications().setForkDimensions(rightVeh.getSpecifications().getForkDimensions());
                rightVeh.getSpecifications().setForkDimensions(tmpFork);
            }
            case SIDE_SHIFT -> {
                Boolean tmpSideShift = leftVeh.getSpecifications().getHasSideShift();
                leftVeh.getSpecifications().setHasSideShift(rightVeh.getSpecifications().getHasSideShift());
                rightVeh.getSpecifications().setHasSideShift(tmpSideShift);
            }
            case VALVE -> {
                Integer tmpValve = leftVeh.getSpecifications().getValveCount();
                leftVeh.getSpecifications().setValveCount(rightVeh.getSpecifications().getValveCount());
                rightVeh.getSpecifications().setValveCount(tmpValve);
            }
        }
    }

    private void setVehiclePrices(InventoryItem leftVeh, InventoryItem rightVeh, VehiclePartSwapRequest request) {
        if (request.getLeftPrice() != null) {
            leftVeh.getPricing().setActualSalePrice(request.getLeftPrice().getActualSalePrice());
            leftVeh.getPricing().setSalePriceR0(request.getLeftPrice().getSalePriceR0());
            leftVeh.getPricing().setSalePriceR1(request.getLeftPrice().getSalePriceR1());
        }
        if (request.getRightPrice() != null) {
            rightVeh.getPricing().setActualSalePrice(request.getRightPrice().getActualSalePrice());
            rightVeh.getPricing().setSalePriceR0(request.getRightPrice().getSalePriceR0());
            rightVeh.getPricing().setSalePriceR1(request.getRightPrice().getSalePriceR1());
        }
    }

    private ConfigurationHistory buildSwapHistory(
            InventoryItem vehicleLeft,
            InventoryItem vehicleRight,
            InventoryItem componentOld,
            InventoryItem componentReplace,
            ComponentType componentType
    ) {

        ConfigurationHistory configHistory = new ConfigurationHistory();

        configHistory.setVehicleId(vehicleLeft.getId());

        configHistory.setComponentOldId(componentOld.getId());

        if(InventoryType.SPARE_PART.getId().equals(componentOld.getInventoryType()))
            configHistory.setComponentOldSerial(componentOld.getCommodityCode());
        else configHistory.setComponentOldSerial(componentOld.getSerialNumber());

        configHistory.setComponentReplaceId(componentReplace.getId());
        if(InventoryType.SPARE_PART.getId().equals(componentReplace.getInventoryType()))
            configHistory.setComponentReplaceSerial(componentReplace.getCommodityCode());
        else configHistory.setComponentReplaceSerial(componentReplace.getSerialNumber());

        configHistory.setComponentType(componentType.getId());
        configHistory.setConfigType(ChangeConfigurationType.SWAP.getId());

        configHistory.setDescription("Hoán đối " + componentType.getValue() + " với xe " + vehicleRight.getProductCode());

        return configurationHistoryRepository.save(configHistory);
    }

    private ConfigurationHistory buildDisassembleHistory(
            InventoryItem vehicle,
            InventoryItem component,
            ComponentType componentType
    ) {

        ConfigurationHistory configHistory = new ConfigurationHistory();

        configHistory.setVehicleId(vehicle.getId());

        configHistory.setComponentOldId(component.getId());
        configHistory.setComponentOldSerial(component.getSerialNumber());;

        configHistory.setComponentType(componentType.getId());
        configHistory.setConfigType(ChangeConfigurationType.DISASSEMBLE.getId());

        configHistory.setDescription("Tháo rời " + componentType.getValue() + " ra khỏi xe " + vehicle.getProductCode());

        return configurationHistoryRepository.save(configHistory);
    }

    private ConfigurationHistory buildAssembleHistory(
            InventoryItem vehicle,
            InventoryItem component,
            ComponentType componentType
    ) {

        ConfigurationHistory configHistory = new ConfigurationHistory();

        configHistory.setVehicleId(vehicle.getId());

        configHistory.setComponentReplaceId(component.getId());
        configHistory.setComponentReplaceSerial(component.getSerialNumber());;

        configHistory.setComponentType(componentType.getId());
        configHistory.setConfigType(ChangeConfigurationType.ASSEMBLE.getId());

        configHistory.setDescription("Lắp ráp " + componentType.getValue() + " vào xe " + vehicle.getProductCode());

        return configurationHistoryRepository.save(configHistory);
    }

    public ConfigurationHistory findByIdOrThrow(String id) {
        return configurationHistoryRepository.findById(new ObjectId(id)).orElseThrow(() -> LogicErrException.of("Không tồn tại"));
    }

    @Transactional
    public boolean dropComponent(DropPartRequest dropPartRequest) {

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dropPartRequest.getVehicleId()));

        ComponentType componentType = ComponentType.fromId(dropPartRequest.getComponentType());
        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());
        InventoryType itemType = ComponentType.itemType(componentType);

        boolean isAccessoryOrSparePartNotExists = false;

        if(component.getProductCode() == null && itemType.equals(InventoryType.ACCESSORY)) {
            component.setProductCode(dropPartRequest.getProductCode());
            isAccessoryOrSparePartNotExists = true;
        }
        else if(itemType.equals(InventoryType.SPARE_PART)){
            Optional<InventoryItem> componentExistsCodeAndDescription = inventoryItemRepository.findByCommodityCodeAndDescription(component.getCommodityCode(), component.getDescription());
            if(componentExistsCodeAndDescription.isPresent()){
                InventoryItem pt = componentExistsCodeAndDescription.get();
                pt.setQuantity(pt.getQuantity() + component.getQuantity());
                inventoryItemRepository.bulkUpdateStatusAndQuantity(List.of(pt));
                inventoryItemRepository.deleteById(component.getId());
            }
            else isAccessoryOrSparePartNotExists = true;
        }

        if(isAccessoryOrSparePartNotExists){
            if(component.getPricing() == null) component.setPricing(new InventoryItem.Pricing());
            component.getPricing().setSalePriceR0(dropPartRequest.getPriceR0());
            component.getPricing().setSalePriceR1(dropPartRequest.getPriceR1());
            component.getPricing().setActualSalePrice(dropPartRequest.getActualPrice());
            component.setStatus(InventoryItemStatus.IN_STOCK.getId());

            inventoryItemRepository.save(component);
        }

        disassembleSpecifications(vehicle, componentType);
        inventoryItemRepository.save(vehicle);

        buildDisassembleHistory(vehicle, component, componentType);
        return true;
    }


    private void disassembleSpecifications(InventoryItem veh, ComponentType componentType) {
        switch (componentType) {
            case LIFTING_FRAME -> {
                veh.getSpecifications().setLiftingHeightMm(null);
                veh.getSpecifications().setLiftingCapacityKg(null);
                veh.getSpecifications().setChassisType(null);
            }
            case BATTERY -> {
                veh.getSpecifications().setBatteryInfo(null);
                veh.getSpecifications().setBatterySpecification(null);
            }
            case CHARGER -> veh.getSpecifications().setChargerSpecification(null);

            case ENGINE -> veh.getSpecifications().setEngineType(null);

            case FORK -> veh.getSpecifications().setForkDimensions(null);

            case SIDE_SHIFT -> veh.getSpecifications().setHasSideShift(false);

            case VALVE -> veh.getSpecifications().setValveCount(0);
        }
    }

    private void assembleSpecifications(InventoryItem veh, ComponentType componentType, InventoryItem component) {
        switch (componentType) {
            case LIFTING_FRAME -> {
                veh.getSpecifications().setLiftingHeightMm(component.getSpecifications().getLiftingHeightMm());
                veh.getSpecifications().setLiftingCapacityKg(component.getSpecifications().getLiftingCapacityKg());
                veh.getSpecifications().setChassisType(component.getSpecifications().getChassisType());
            }
            case BATTERY -> {
                veh.getSpecifications().setBatteryInfo(component.getSpecifications().getBatteryInfo());
                veh.getSpecifications().setBatterySpecification(component.getSpecifications().getBatterySpecification());
            }
            case CHARGER -> veh.getSpecifications().setChargerSpecification(component.getSpecifications().getChargerSpecification());

            case ENGINE -> veh.getSpecifications().setEngineType(component.getSpecifications().getEngineType());

            case FORK -> veh.getSpecifications().setForkDimensions(component.getSpecifications().getForkDimensions());

            case SIDE_SHIFT -> {
                if(component.getComponentType().equals(ComponentType.SIDE_SHIFT.getId())){
                    veh.getSpecifications().setHasSideShift(true);
                }
            }

            case VALVE -> veh.getSpecifications().setValveCount(component.getQuantity());
        }
    }

    public boolean assembleComponent(AssemblePartRequest assemblePart){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(assemblePart.getVehicleId()));
        ComponentType componentType = ComponentType.fromId(assemblePart.getComponentType());
        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());
        if(vehicle.getId().equals(component.getVehicleId()))
            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " đã có sẵn trong xe " + vehicle.getProductCode());

        Warehouse warehouse = warehouseService.getWarehouseToId(new ObjectId(assemblePart.getWarehouseId()));

        component = inventoryItemRepository.findByComponentTypeAndWarehouseId(componentType.getId(), warehouse.getId())
                .orElseThrow(() -> LogicErrException.of("Bộ phận " + componentType.getValue() + " không tồn tại ở kho " + warehouse.getName()));

        if(ComponentType.itemType(componentType).equals(InventoryType.SPARE_PART)){
            if(component.getQuantity() < assemblePart.getComponentQuantity())
                throw LogicErrException.of("Bộ phận phụ tùng " + componentType.getValue() + " không đủ số lượng yêu cầu lắp vào.");
            else if(component.getQuantity() > assemblePart.getComponentQuantity()){

                component.setQuantity(component.getQuantity() - assemblePart.getComponentQuantity());
                inventoryItemRepository.bulkUpdateStatusAndQuantity(List.of(component));

                component = inventoryItemMapper.cloneEntity(component);
                component.setQuantity(assemblePart.getComponentQuantity());
            }
        }

        component.setVehicleId(vehicle.getId());
        component.setStatus(InventoryItemStatus.OTHER.getId());

        component = inventoryItemRepository.save(component);

        assembleSpecifications(vehicle, componentType, component);

        inventoryItemRepository.save(vehicle);

        buildAssembleHistory(vehicle, component, componentType);

        return true;
    }

    public ConfigVehicleSpecHistoryResponse getConfigurationHistoryToVehicleId(ObjectId vehicleId){
        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(vehicleId.toString()));
        if(!InventoryType.VEHICLE.getId().equals(vehicle.getInventoryType()))
            throw LogicErrException.of("Sản phẩm cần xem lịch sử cấu hình không phải là xe.");

        List<ConfigurationHistory> configHistories = configurationHistoryRepository.findByVehicleIdOrderByCreatedAtDesc(vehicle.getId());

        ConfigVehicleSpecHistoryResponse configVehicleSpecHistory = configurationVehicleMapper.toConfigVehicleSpecHistoryResponse(vehicle);

        configVehicleSpecHistory.setConfigHistories(
                configHistories.stream()
                        .map(configurationVehicleMapper::toConfigurationHistoryResponse)
                        .toList()
        );

        return configVehicleSpecHistory;
    }

    public Page<ConfigVehicleSpecPageResponse> getPageConfigVehicleSpec(PageOptionsDto optionsDto){
        return inventoryItemRepository.findPageConfigVehicleSpec(optionsDto);
    }
}
