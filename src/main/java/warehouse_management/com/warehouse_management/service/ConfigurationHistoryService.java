package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.*;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.ItemCodeModelSerialDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.ItemCodePriceDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.VehiclePricingR0R1Dto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.ConfigurationHistoryMapper;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.configuration_history.ConfigurationHistoryRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ConfigurationHistoryService {


    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemService inventoryItemService;
    private final ConfigurationHistoryRepository configurationHistoryRepository;
    private final InventoryItemMapper inventoryItemMapper;
    private final ConfigurationHistoryMapper configurationHistoryMapper;
    private final CustomAuthentication customAuthentication;
    private final WarehouseService warehouseService;

    public ConfigurationHistory getToId(ObjectId id){
        return configurationHistoryRepository.findById(id)
                .orElseThrow(() -> LogicErrException.of("Lịch sử cấu hình không tồn tại."));
    }

    public ConfigurationHistory getToCodeAndVehicleId(String code, ObjectId vehicleId){
        return configurationHistoryRepository.findByCodeAndVehicleId(code, vehicleId)
                .orElseThrow(() -> LogicErrException.of("Lịch sử cấu hình không tồn tại."));
    }

    @Transactional
    public boolean swapItems(VehiclePartSwapDto request) {
        // 1. Lấy vehicle và accessory tương ứng
        InventoryItem leftVeh = inventoryItemService.findByIdOrThrow(request.getLeftVehicleId());

        ComponentType componentType = ComponentType.fromId(request.getComponentType());
        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        InventoryItem leftVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(leftVeh.getId(), componentType, leftVeh.getProductCode());

        ConfigurationHistory configurationHistory = getToCodeAndVehicleId(request.getConfigurationCode(), leftVeh.getId());

        List<InventoryItem> vehicleToUpdateSpecAndPricingList = new ArrayList<>();

        if(!leftVehComponent.getId().equals(configurationHistory.getComponentReplaceId())){

            InventoryItem rightVeh = inventoryItemService.findByIdOrThrow(request.getRightVehicleId());
            InventoryItem rightVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(rightVeh.getId(), componentType, rightVeh.getProductCode());

            inventoryItemRepository.updateVehicleIdById(leftVehComponent.getId(), rightVeh.getId());
            inventoryItemRepository.updateVehicleIdById(rightVehComponent.getId(), leftVeh.getId());

            swapVehicleComponent(leftVeh, rightVeh, componentType);

            setSerialComponent(leftVeh, rightVehComponent, componentType);
            setSerialComponent(rightVeh, leftVehComponent, componentType);

            vehicleToUpdateSpecAndPricingList.add(rightVeh);
        }

        if (request.getLeftPrice() != null) {
            if(leftVeh.getPricing() == null) leftVeh.setPricing(new InventoryItem.Pricing());
            leftVeh.getPricing().setSalePriceR0(request.getLeftPrice().getSalePriceR0());
            leftVeh.getPricing().setSalePriceR1(request.getLeftPrice().getSalePriceR1());
        }

        vehicleToUpdateSpecAndPricingList.add(leftVeh);
        inventoryItemRepository.bulkUpdateSpecAndPricing(vehicleToUpdateSpecAndPricingList);

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        configurationHistoryRepository.updatePerformed(configurationHistory.getId(), customUserDetail.getFullName());

        return true;
    }

// --------------------------- Helpers ---------------------------

    private void swapVehicleComponent(InventoryItem leftVeh, InventoryItem rightVeh, ComponentType componentType) {
        if(leftVeh.getSpecificationsSerial() == null)
            leftVeh.setSpecificationsSerial(new InventoryItem.SpecificationsSerial());

        if(rightVeh.getSpecificationsSerial() == null)
            rightVeh.setSpecificationsSerial(new InventoryItem.SpecificationsSerial());

        switch (componentType) {
            case LIFTING_FRAME -> {
                String tmpHeight = leftVeh.getSpecifications().getLiftingHeightMm();
                leftVeh.getSpecifications().setLiftingHeightMm(rightVeh.getSpecifications().getLiftingHeightMm());
                rightVeh.getSpecifications().setLiftingHeightMm(tmpHeight);

                String tmpCapacity = leftVeh.getSpecifications().getLiftingCapacityKg();
                leftVeh.getSpecifications().setLiftingCapacityKg(rightVeh.getSpecifications().getLiftingCapacityKg());
                rightVeh.getSpecifications().setLiftingCapacityKg(tmpCapacity);

                String tmpChassis = leftVeh.getSpecifications().getChassisType();
                leftVeh.getSpecifications().setChassisType(rightVeh.getSpecifications().getChassisType());
                rightVeh.getSpecifications().setChassisType(tmpChassis);

                String tmpSerial = leftVeh.getSpecificationsSerial().getLiftingFrameSerial();
                leftVeh.getSpecificationsSerial().setLiftingFrameSerial(rightVeh.getSpecificationsSerial().getLiftingFrameSerial());
                rightVeh.getSpecificationsSerial().setLiftingFrameSerial(tmpSerial);
            }
            case BATTERY -> {
                String tmpInfo = leftVeh.getSpecifications().getBatteryInfo();
                leftVeh.getSpecifications().setBatteryInfo(rightVeh.getSpecifications().getBatteryInfo());
                rightVeh.getSpecifications().setBatteryInfo(tmpInfo);

                String tmpSpec = leftVeh.getSpecifications().getBatterySpecification();
                leftVeh.getSpecifications().setBatterySpecification(rightVeh.getSpecifications().getBatterySpecification());
                rightVeh.getSpecifications().setBatterySpecification(tmpSpec);

                String tmpSerial = leftVeh.getSpecificationsSerial().getBatterySerial();
                leftVeh.getSpecificationsSerial().setBatterySerial(rightVeh.getSpecificationsSerial().getBatterySerial());
                rightVeh.getSpecificationsSerial().setBatterySerial(tmpSerial);
            }
            case CHARGER -> {
                String tmpSpec = leftVeh.getSpecifications().getChargerSpecification();
                leftVeh.getSpecifications().setChargerSpecification(rightVeh.getSpecifications().getChargerSpecification());
                rightVeh.getSpecifications().setChargerSpecification(tmpSpec);

                String tmpSerial = leftVeh.getSpecificationsSerial().getChargerSerial();
                leftVeh.getSpecificationsSerial().setChargerSerial(rightVeh.getSpecificationsSerial().getChargerSerial());
                rightVeh.getSpecificationsSerial().setChargerSerial(tmpSerial);
            }
            case ENGINE -> {
                String tmpEngine = leftVeh.getSpecifications().getEngineType();
                leftVeh.getSpecifications().setEngineType(rightVeh.getSpecifications().getEngineType());
                rightVeh.getSpecifications().setEngineType(tmpEngine);

                String tmpSerial = leftVeh.getSpecificationsSerial().getEngineSerial();
                leftVeh.getSpecificationsSerial().setEngineSerial(rightVeh.getSpecificationsSerial().getEngineSerial());
                rightVeh.getSpecificationsSerial().setEngineSerial(tmpSerial);
            }
            case FORK -> {
                String tmpFork = leftVeh.getSpecifications().getForkDimensions();
                leftVeh.getSpecifications().setForkDimensions(rightVeh.getSpecifications().getForkDimensions());
                rightVeh.getSpecifications().setForkDimensions(tmpFork);

                String tmpSerial = leftVeh.getSpecificationsSerial().getForkSerial();
                leftVeh.getSpecificationsSerial().setForkSerial(rightVeh.getSpecificationsSerial().getForkSerial());
                rightVeh.getSpecificationsSerial().setForkSerial(tmpSerial);
            }
            case SIDE_SHIFT -> {
                String tmpSideShift = leftVeh.getSpecifications().getHasSideShift();
                leftVeh.getSpecifications().setHasSideShift(rightVeh.getSpecifications().getHasSideShift());
                rightVeh.getSpecifications().setHasSideShift(tmpSideShift);

                String tmpSerial = leftVeh.getSpecificationsSerial().getSideShiftSerial();
                leftVeh.getSpecificationsSerial().setSideShiftSerial(rightVeh.getSpecificationsSerial().getSideShiftSerial());
                rightVeh.getSpecificationsSerial().setSideShiftSerial(tmpSerial);
            }
            case VALVE -> {
                String tmpValve = leftVeh.getSpecifications().getValveCount();
                leftVeh.getSpecifications().setValveCount(rightVeh.getSpecifications().getValveCount());
                rightVeh.getSpecifications().setValveCount(tmpValve);

                String tmpSerial = leftVeh.getSpecificationsSerial().getValveSerial();
                leftVeh.getSpecificationsSerial().setValveSerial(rightVeh.getSpecificationsSerial().getValveSerial());
                rightVeh.getSpecificationsSerial().setValveSerial(tmpSerial);
            }
            case WHEEL -> {
                String tmpValve = leftVeh.getSpecifications().getWheelInfo();
                leftVeh.getSpecifications().setWheelInfo(rightVeh.getSpecifications().getWheelInfo());
                rightVeh.getSpecifications().setWheelInfo(tmpValve);

                String tmpSerial = leftVeh.getSpecificationsSerial().getWheelSerial();
                leftVeh.getSpecificationsSerial().setWheelSerial(rightVeh.getSpecificationsSerial().getWheelSerial());
                rightVeh.getSpecificationsSerial().setWheelSerial(tmpSerial);
            }
        }
    }

    protected ConfigurationHistory buildSwapHistory(
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
        configHistory.setConfigType(ConfigurationType.SWAP.getId());

        configHistory.setDescription("Hoán đối " + componentType.getValue() + " với Xe " + vehicleRight.getProductCode());

        return configHistory;
    }

    protected ConfigurationHistory buildDisassembleHistory(
            InventoryItem vehicle,
            InventoryItem component,
            ComponentType componentType
    ) {

        ConfigurationHistory configHistory = new ConfigurationHistory();

        configHistory.setVehicleId(vehicle.getId());

        configHistory.setComponentOldId(component.getId());

        if(InventoryType.SPARE_PART.getId().equals(component.getInventoryType()))
            configHistory.setComponentOldSerial(component.getCommodityCode());
        else configHistory.setComponentOldSerial(component.getSerialNumber());

        configHistory.setComponentType(componentType.getId());
        configHistory.setConfigType(ConfigurationType.DISASSEMBLE.getId());

        configHistory.setDescription("Tháo rời " + componentType.getValue() + " ra khỏi Xe " + vehicle.getProductCode());

        return configHistory;
    }

    protected ConfigurationHistory buildAssembleHistory(
            InventoryItem vehicle,
            InventoryItem component,
            ComponentType componentType
    ) {

        ConfigurationHistory configHistory = new ConfigurationHistory();

        configHistory.setVehicleId(vehicle.getId());

        configHistory.setComponentReplaceId(component.getId());

        if(InventoryType.SPARE_PART.getId().equals(component.getInventoryType()))
            configHistory.setComponentReplaceSerial(component.getCommodityCode());
        else configHistory.setComponentReplaceSerial(component.getSerialNumber());

        configHistory.setComponentType(componentType.getId());
        configHistory.setConfigType(ConfigurationType.ASSEMBLE.getId());

        configHistory.setDescription("Lắp ráp " + componentType.getValue() + " vào Xe " + vehicle.getProductCode());

        return configHistory;
    }

    public ConfigurationHistory findByIdOrThrow(String id) {
        return configurationHistoryRepository.findById(new ObjectId(id)).orElseThrow(() -> LogicErrException.of("Không tồn tại"));
    }

    @Transactional
    public boolean dropComponent(DropPartDto dropPartRequest) {

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dropPartRequest.getVehicleId()));

        ComponentType componentType = ComponentType.fromId(dropPartRequest.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());
        InventoryType itemType = ComponentType.itemType(componentType);

        boolean isAccessoryOrSparePartNotExists = false;

        if(itemType.equals(InventoryType.ACCESSORY)) {
            if(component.getProductCode() == null){
                if(dropPartRequest.getProductCode() == null) throw LogicErrException.of("Bắt buộc nhập Mã Sản Phẩm cho bộ phận bị tháo rời");

                if(inventoryItemRepository.existsByProductCode(dropPartRequest.getProductCode()))
                    throw LogicErrException.of("Mã sản phẩm " + dropPartRequest.getProductCode() + " đã tồn tại");

                component.setProductCode(dropPartRequest.getProductCode());
            }

            if(component.getSerialNumber() == null)
                component.setSerialNumber(vehicle.getSerialNumber());

            component.setInitialCondition(false);
            isAccessoryOrSparePartNotExists = true;
        }
        else if(itemType.equals(InventoryType.SPARE_PART)){
            Optional<InventoryItem> ptExists = inventoryItemRepository.findByCommodityCodeAndDescriptionAndWarehouseId(component.getCommodityCode(), component.getDescription(), vehicle.getWarehouseId());
            if(
                    ptExists.isPresent()
                    && !ptExists.get().getId().equals(component.getId())
                    && ptExists.get().getCommodityCode() != null
            ){

                InventoryItem pt = ptExists.get();
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
            component.setStatus(InventoryItemStatus.IN_STOCK.getId());
            component.setVehicleId(null);
            component.setWarehouseId(vehicle.getWarehouseId());

            inventoryItemRepository.save(component);
        }

        disassembleSpecifications(vehicle, componentType);
        vehicle.setInitialCondition(false);
        vehicle.setIsFullyComponent();

        if(vehicle.getPricing() == null) vehicle.setPricing(new InventoryItem.Pricing());
        vehicle.getPricing().setSalePriceR0(dropPartRequest.getVehiclePriceR0());
        vehicle.getPricing().setSalePriceR1(dropPartRequest.getVehiclePriceR1());

        setSerialComponent(vehicle, null, componentType);

        inventoryItemRepository.save(vehicle);

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        configurationHistoryRepository.updatePerformed(dropPartRequest.getConfigurationCode(), customUserDetail.getFullName());

        return true;
    }

    private void setSerialComponent(InventoryItem vehicle, InventoryItem component, ComponentType componentType){

        if(vehicle.getSpecificationsSerial() == null){
            vehicle.setSpecificationsSerial(new InventoryItem.SpecificationsSerial());
        }

        String componentSerial;

        if(component == null) componentSerial = null;
        else componentSerial = InventoryType.SPARE_PART.getId().equals(component.getInventoryType())
                ? component.getCommodityCode() : component.getSerialNumber();

        switch (componentType){
            case ENGINE -> vehicle.getSpecificationsSerial().setEngineSerial(componentSerial);
            case FORK -> vehicle.getSpecificationsSerial().setForkSerial(componentSerial);
            case SIDE_SHIFT -> vehicle.getSpecificationsSerial().setSideShiftSerial(componentSerial);
            case VALVE -> vehicle.getSpecificationsSerial().setValveSerial(componentSerial);
            case BATTERY -> vehicle.getSpecificationsSerial().setBatterySerial(componentSerial);
            case CHARGER -> vehicle.getSpecificationsSerial().setChargerSerial(componentSerial);
            case LIFTING_FRAME -> vehicle.getSpecificationsSerial().setLiftingFrameSerial(componentSerial);
            case WHEEL -> vehicle.getSpecificationsSerial().setWheelSerial(componentSerial);
        }
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

            case SIDE_SHIFT -> veh.getSpecifications().setHasSideShift(null);

            case VALVE -> veh.getSpecifications().setValveCount(null);

            case WHEEL -> veh.getSpecifications().setWheelInfo(null);
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

            case SIDE_SHIFT -> veh.getSpecifications().setHasSideShift("true");

            case WHEEL -> veh.getSpecifications().setWheelInfo(component.getSpecifications().getWheelInfo());

            case VALVE -> veh.getSpecifications().setValveCount(component.getSpecifications().getValveCount());
        }
    }

    @Transactional
    public boolean assembleComponent(AssemblePartDto assemblePart){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(assemblePart.getVehicleId()));

        InventoryItem component = inventoryItemService.getItemToId(new ObjectId(assemblePart.getComponentId()));
        ComponentType componentType = ComponentType.fromId(component.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận không hợp lệ");

        if(vehicle.getId().equals(component.getVehicleId())){
            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " đã có sẵn trong Xe " + vehicle.getProductCode());
        }

        component.setVehicleId(vehicle.getId());
        component.setStatus(InventoryItemStatus.IN_VEHICLE.getId());

        component = inventoryItemRepository.save(component);

        assembleSpecifications(vehicle, componentType, component);

        vehicle.setIsFullyComponent();

        if(vehicle.getPricing() == null) vehicle.setPricing(new InventoryItem.Pricing());
        vehicle.getPricing().setSalePriceR0(assemblePart.getVehiclePriceR0());
        vehicle.getPricing().setSalePriceR1(assemblePart.getVehiclePriceR1());

        setSerialComponent(vehicle, component, componentType);

        inventoryItemRepository.save(vehicle);

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        configurationHistoryRepository.updatePerformed(assemblePart.getConfigurationCode(), customUserDetail.getFullName());

        return true;
    }

    public ConfigVehicleSpecHistoryDto getConfigurationHistoryToVehicleId(ObjectId vehicleId){
        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(vehicleId.toString()));
        if(!InventoryType.VEHICLE.getId().equals(vehicle.getInventoryType()))
            throw LogicErrException.of("Sản phẩm cần Xem lịch sử cấu hình không phải là Xe.");

        List<ConfigurationHistory> configHistories = configurationHistoryRepository.findByVehicleIdOrderByCreatedAtDesc(vehicle.getId());

        ConfigVehicleSpecHistoryDto configVehicleSpecHistory = configurationHistoryMapper.toConfigVehicleSpecHistoryResponse(vehicle);

        configVehicleSpecHistory.setSpecificationsBase(buildSpecificationsBaseResponse(vehicle));

        configVehicleSpecHistory.setConfigHistories(
                configHistories.stream()
                        .map(o -> {
                            ConfigurationHistoryDto res = configurationHistoryMapper.toConfigurationHistoryResponse(o);
                            ComponentType componentType = ComponentType.fromId(o.getComponentType());
                            res.setComponentName(componentType == null ? null : componentType.getValue());
                            return res;
                        })
                        .toList()
        );

        return configVehicleSpecHistory;
    }

    private static ConfigVehicleSpecHistoryDto.Specifications buildSpecificationsBaseResponse(InventoryItem vehicle) {
        ConfigVehicleSpecHistoryDto.Specifications specificationsBase = new ConfigVehicleSpecHistoryDto.Specifications();
        specificationsBase.setLiftingFrame(
                (vehicle.getSpecificationsBase().getChassisType() == null ? "" : vehicle.getSpecificationsBase().getChassisType())
                + " - " + (vehicle.getSpecificationsBase().getLiftingCapacityKg() == null ? "0" : vehicle.getSpecificationsBase().getLiftingCapacityKg())
                + " Kg - " + (vehicle.getSpecificationsBase().getLiftingHeightMm() == null ? "0" : vehicle.getSpecificationsBase().getLiftingHeightMm())
                + " mm"
        );
        specificationsBase.setBattery(
                (vehicle.getSpecificationsBase().getBatteryInfo() == null ? "" : vehicle.getSpecificationsBase().getBatteryInfo())
                + " - " + (vehicle.getSpecificationsBase().getBatterySpecification() == null ? "" : vehicle.getSpecificationsBase().getBatterySpecification())
        );
        specificationsBase.setCharger(vehicle.getSpecificationsBase().getChargerSpecification());
        specificationsBase.setFork(vehicle.getSpecificationsBase().getForkDimensions());
        specificationsBase.setEngine(vehicle.getSpecificationsBase().getEngineType());
        specificationsBase.setValve(vehicle.getSpecificationsBase().getValveCount());
        specificationsBase.setSideShift(vehicle.getSpecificationsBase().getHasSideShift());
        return specificationsBase;
    }

    public Page<ConfigVehicleSpecPageDto> getPageConfigVehicleSpec(PageOptionsDto optionsDto){
        return inventoryItemRepository.findPageConfigVehicleSpec(optionsDto);
    }

    public List<VehicleComponentTypeDto> getComponentTypeToVehicleId(ObjectId vehicleId, Boolean isSwap){

        List<String> componentTypes = inventoryItemRepository.findComponentTypeByVehicleId(vehicleId);

        List<String> componentTypesToFilter;

        if(isSwap) componentTypesToFilter = configurationHistoryRepository.findAllComponentUnSwapAndUnCompletedByVehicleId(vehicleId);
        else componentTypesToFilter = configurationHistoryRepository.findAllComponentSwapAndUnCompletedByVehicleId(vehicleId);

        return componentTypes
                .stream()
                .filter(elm -> !componentTypesToFilter.contains(elm))
                .map(elm -> {
                    ComponentType componentType = ComponentType.fromId(elm);
                    if(componentType == null) return null;
                    VehicleComponentTypeDto res = new VehicleComponentTypeDto();
                    res.setComponentType(componentType.getId());
                    res.setComponentName(componentType.getValue());
                    return res;
                })
                .toList();
    }

    public List<VehicleComponentTypeDto> getComponentTypeMissingToVehicleId(ObjectId vehicleId){

        List<String> componentTypes = inventoryItemRepository.findComponentTypeByVehicleId(vehicleId);

        List<ComponentType> componentTypesMissing = Arrays.stream(ComponentType.values())
                .filter(o -> !componentTypes.contains(o.getId()))
                .toList();

        return componentTypesMissing.stream()
                .map(o -> {
                    VehicleComponentTypeDto res = new VehicleComponentTypeDto();
                    res.setComponentType(o.getId());
                    res.setComponentName(o.getValue());
                    return res;
                })
                .toList();
    }

    public List<ComponentAndWarehouseDto> getWarehouseContainsComponent(String componentType){

        ComponentType type = ComponentType.fromId(componentType);
        if(type == null) throw LogicErrException.of("Loại bộ phận muốn kiểm tra không hợp lệ.");

        List<Map<String, Object>> docRsults = inventoryItemRepository.findWarehouseContainsComponent(type.getId());

        return docRsults.stream()
                .map(doc -> {

                    ComponentAndWarehouseDto res = new ComponentAndWarehouseDto();
                    res.setComponentId((ObjectId) doc.get("componentId"));
                    res.setSerialNumber((String) doc.get("serialNumber"));

                    if(res.getSerialNumber() == null) res.setSerialNumber((String) doc.get("commodityCode"));

                    ComponentType ctype = ComponentType.fromId((String) doc.get("componentType"));

                    res.setComponentName(ctype == null ? null : ctype.getValue());
                    res.setWarehouseName((String) doc.get("warehouseName"));
                    res.setWarehouseCode((String) doc.get("warehouseCode"));
                    return res;
                })
                .toList();
    }

    public List<VehicleComponentTypeDto> getComponentsTypeCommon(ObjectId vehicleLeftId, ObjectId vehicleRightId){

        List<String> componentTypesLeft = inventoryItemRepository.findComponentTypeByVehicleId(vehicleLeftId);

        List<String> componentTypesRight = inventoryItemRepository.findComponentTypeByVehicleId(vehicleRightId);

        Set<String> componentTypesCommon = new HashSet<>(componentTypesLeft);

        componentTypesCommon.retainAll(componentTypesRight);

        return componentTypesCommon.stream().map(elm -> {
            ComponentType componentType = ComponentType.fromId(elm);
            if(componentType == null) return null;
            VehicleComponentTypeDto res = new VehicleComponentTypeDto();
            res.setComponentType(componentType.getId());
            res.setComponentName(componentType.getValue());
            return res;
        }).filter(Objects::nonNull).toList();
    }

    public List<ItemCodeModelSerialDto> getVehicleByComponentTypeAndInConfig(String componentType){

        List<ItemCodeModelSerialDto> items = inventoryItemRepository.findVehicleByComponentTypeAndStatus(componentType, InventoryItemStatus.IN_CONFIG.getId());

        List<ObjectId> vehicleIdsSwapConfig = configurationHistoryRepository.findAllVehicleIdSwapAndUnCompletedByComponentType(componentType);

        return items.stream()
                .filter(o -> !vehicleIdsSwapConfig.contains(o.getVehicleId()))
                .toList();
    }

    @Transactional
    public void addVehicleToConfiguration(AddVehicleToConfigurationDto request){
        List<ObjectId> vehicleIds = request.getVehicleIds().stream().map(ObjectId::new).toList();
        List<InventoryItem> vehicles = inventoryItemRepository.findByIdInAndStatus(vehicleIds, InventoryItemStatus.IN_STOCK.getId());
        List<ObjectId> vehiclesToConfig = vehicles.stream()
                .filter(o -> InventoryType.VEHICLE.getId().equalsIgnoreCase(o.getInventoryType()))
                .map(InventoryItem::getId)
                .toList();
        inventoryItemRepository.updateStatusByIdIn(vehiclesToConfig, InventoryItemStatus.IN_CONFIG.getId());
    }

    public Page<ItemCodeModelSerialDto> getPageVehicleInStock(PageOptionsDto optionsDto){
        return inventoryItemRepository.findPageVehicleInStock(optionsDto);
    }

    public ItemCodePriceDto getCodeAndPriceToVehicleIdAndComponentType(ObjectId vehicleId, String componentType){

        InventoryItem vehicle = inventoryItemService.getItemToId(vehicleId);

        Optional<Map<String, Object>> resultQuery = inventoryItemRepository.findCodeAndPriceByVehicleIdAndComponentType(vehicle.getId(), componentType);

        if(resultQuery.isPresent()){

            Map<String, Object> result = resultQuery.get();

            ItemCodePriceDto res = new ItemCodePriceDto();

            res.setCode((String) result.get("productCode"));
            if(res.getCode() == null) res.setCode((String) result.get("commodityCode"));

            Decimal128 priceR0 = (Decimal128) result.get("salePriceR0");
            Decimal128 priceR1 = (Decimal128) result.get("salePriceR1");
            Decimal128 otherPrice = (Decimal128) result.get("otherPrice");

            res.setSalePriceR0(priceR0 == null ? null : priceR0.bigDecimalValue());
            res.setSalePriceR1(priceR1 == null ? null : priceR1.bigDecimalValue());
            res.setOtherPrice(otherPrice == null ? null : otherPrice.bigDecimalValue());

            return res;
        }

        return new ItemCodePriceDto();
    }

    public VehiclePricingR0R1Dto getVehiclePriceToVehicleId(ObjectId vehicleId){

        InventoryItem vehicle = inventoryItemService.getItemToId(vehicleId);

        VehiclePricingR0R1Dto res = new VehiclePricingR0R1Dto();

        if(vehicle.getPricing() != null){
            res.setVehiclePriceR0(vehicle.getPricing().getSalePriceR0());
            res.setVehiclePriceR1(vehicle.getPricing().getSalePriceR1());
            res.setOtherPrice(vehicle.getPricing().getOtherPrice());
        }

        res.setVehicleId(vehicle.getId());

        return res;
    }

    @Transactional
    public boolean swapMultipleVehicle(List<VehiclePartSwapDto> request){

        List<VehiclePartSwapDto> vehicleToSwapList = new ArrayList<>();

        List<ObjectId> vehicleIdList = new ArrayList<>();

        for(var vehicleSwap : request){
            if(vehicleSwap.getLeftVehicleId() == null || vehicleSwap.getRightVehicleId() == null)
                continue;

            vehicleIdList.add(new ObjectId(vehicleSwap.getLeftVehicleId()));
            vehicleIdList.add(new ObjectId(vehicleSwap.getRightVehicleId()));

            vehicleToSwapList.add(vehicleSwap);
        }

        List<InventoryItem> vehicleList = inventoryItemRepository.findByIdIn(vehicleIdList);
        Map<String, InventoryItem> vehiclesMap = vehicleList.stream()
                .filter(o -> InventoryType.VEHICLE.getId().equals(o.getInventoryType()))
                .collect(Collectors.toMap(vehicleCurrent -> vehicleCurrent.getId().toString(), vehicleCurrent -> vehicleCurrent));

        List<ConfigurationHistory> configurationHistoryList = new ArrayList<>();

        for(var swap : vehicleToSwapList){

            InventoryItem vehicleLeft = vehiclesMap.getOrDefault(swap.getLeftVehicleId(), null);
            InventoryItem vehicleRight = vehiclesMap.getOrDefault(swap.getRightVehicleId(), null);

            if(vehicleLeft == null || vehicleRight == null) continue;

            ComponentType componentType = ComponentType.fromId(swap.getComponentType());
            if(componentType == null) throw LogicErrException.of("Loại bộ phận cần hoán đổi không hợp lệ.");

            InventoryItem leftVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(vehicleLeft.getId(), componentType, vehicleLeft.getProductCode());
            InventoryItem rightVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(vehicleRight.getId(), componentType, vehicleRight.getProductCode());
            inventoryItemRepository.updateVehicleIdById(leftVehComponent.getId(), vehicleRight.getId());
            inventoryItemRepository.updateVehicleIdById(rightVehComponent.getId(), vehicleLeft.getId());

            swapVehicleComponent(vehicleLeft, vehicleRight, componentType);
//            setVehiclePrices(vehicleLeft, vehicleRight, swap);

            ConfigurationHistory historyLeftVeh = buildSwapHistory(vehicleLeft, vehicleRight, leftVehComponent, rightVehComponent, componentType);
            ConfigurationHistory historyRightVeh = buildSwapHistory(vehicleRight, vehicleLeft, rightVehComponent, leftVehComponent, componentType);

            configurationHistoryList.add(historyLeftVeh);
            configurationHistoryList.add(historyRightVeh);
        }

        inventoryItemRepository.bulkUpdateSpecAndPricing(vehiclesMap.values());
        configurationHistoryRepository.bulkInsert(configurationHistoryList);

        return true;
    }

    @Transactional
    public void completedConfigurationVehicle(ConfigurationCompletedDto request){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(request.getVehicleId()));

        List<ConfigurationHistory> configurationHistoryList = configurationHistoryRepository.findAllUnCompletedByVehicleId(vehicle.getId());
        if(!configurationHistoryList.isEmpty()){

            ConfigurationHistory configurationHistory = configurationHistoryList.getFirst();

            ConfigurationStatus configStatus = ConfigurationStatus.fromId(configurationHistory.getStatus());
            ComponentType componentType = ComponentType.fromId(configurationHistory.getComponentType());
            ConfigurationType configurationType = ConfigurationType.fromId(configurationHistory.getConfigType());

            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " hiện " + configStatus.getValue() + " để " + configurationType.getValue());
        }

        configurationHistoryList = configurationHistoryRepository.findAllCompletedAndUnPerformedByVehicleId(vehicle.getId());

        if(!configurationHistoryList.isEmpty()){

            ConfigurationHistory configurationHistory = configurationHistoryList.getFirst();

            ComponentType componentType = ComponentType.fromId(configurationHistory.getComponentType());
            ConfigurationType configurationType = ConfigurationType.fromId(configurationHistory.getConfigType());

            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " hiện đã HOÀN TẤT cấu hình, hãy THỰC HIỆN " + configurationType.getValue());
        }

        inventoryItemRepository.updateStatusByIdIn(List.of(vehicle.getId()), InventoryItemStatus.IN_STOCK.getId());
    }

    public SwapVehiclePricingDto getSwapVehiclePricing(ObjectId vehicleLeftId, ObjectId vehicleRightId){

        InventoryItem vehicleLeft = inventoryItemService.getItemToId(vehicleLeftId);
        InventoryItem vehicleRight = inventoryItemService.getItemToId(vehicleRightId);

        SwapVehiclePricingDto res = new SwapVehiclePricingDto();
        res.setVehicleLeftPricing(new ItemCodePriceDto());
        res.getVehicleLeftPricing().setSalePriceR0(vehicleLeft.getPricing() == null ? null : vehicleLeft.getPricing().getSalePriceR0());
        res.getVehicleLeftPricing().setSalePriceR1(vehicleLeft.getPricing() == null ? null : vehicleLeft.getPricing().getSalePriceR1());
        res.getVehicleLeftPricing().setOtherPrice(vehicleLeft.getPricing() == null ? null : vehicleLeft.getPricing().getOtherPrice());

        res.setVehicleRightPricing(new ItemCodePriceDto());
        res.getVehicleRightPricing().setSalePriceR0(vehicleRight.getPricing() == null ? null : vehicleRight.getPricing().getSalePriceR0());
        res.getVehicleRightPricing().setSalePriceR1(vehicleRight.getPricing() == null ? null : vehicleRight.getPricing().getSalePriceR1());
        res.getVehicleRightPricing().setOtherPrice(vehicleRight.getPricing() == null ? null : vehicleRight.getPricing().getOtherPrice());

        return res;
    }

    @Transactional
    public ConfigurationHistory sendAssembleVehicle(SendConfigAssembleDto dto){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dto.getVehicleId()));

        InventoryItem component = inventoryItemService.getItemToId(new ObjectId(dto.getComponentId()));

        ComponentType componentType = ComponentType.fromId(component.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận không hợp lệ");

        boolean isExistsAssembleComponent = configurationHistoryRepository.existsByVehicleIdAndComponentTypeAndConfigType(vehicle.getId(), componentType.getId(), ConfigurationType.ASSEMBLE.getId());

        if(isExistsAssembleComponent) throw LogicErrException.of("Xe " + vehicle.getProductCode() + " hiện đang lắp ráp " + componentType.getValue());

        if(vehicle.getId().equals(component.getVehicleId())){
            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " đã có sẵn trong Xe " + vehicle.getProductCode());
        }

        if(ComponentType.itemType(componentType).equals(InventoryType.SPARE_PART)){
            if(component.getQuantity() < 1)
                throw LogicErrException.of("Bộ phận phụ tùng " + componentType.getValue() + " không đủ số lượng yêu cầu lắp vào.");
            else if(component.getQuantity() > 1){

                component.setQuantity(component.getQuantity() - 1);
                inventoryItemRepository.bulkUpdateStatusAndQuantity(List.of(component));

                component = inventoryItemMapper.cloneEntity(component);
                component.setQuantity(1);

                component = inventoryItemRepository.save(component);
            }
        }

        inventoryItemRepository.updateStatusByIdIn(List.of(component.getId()), InventoryItemStatus.IN_CONFIG.getId());

        ConfigurationHistory assembleRequest = buildAssembleHistory(vehicle, component, componentType);

        assembleRequest.setStatus(ConfigurationStatus.PENDING.getId());

        return configurationHistoryRepository.save(assembleRequest);
    }

    @Transactional
    public ConfigurationHistory sendDisassembleVehicle(SendConfigDisassembleDto dto){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dto.getVehicleId()));

        ComponentType componentType = ComponentType.fromId(dto.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        boolean isExistsDisassembleComponent = configurationHistoryRepository.existsByVehicleIdAndComponentTypeAndConfigType(vehicle.getId(), componentType.getId(), ConfigurationType.DISASSEMBLE.getId());

        if(isExistsDisassembleComponent) throw LogicErrException.of("Xe " + vehicle.getProductCode() + " hiện đang tháo rời " + componentType.getValue());

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());

        ConfigurationHistory disassembleRequest = buildDisassembleHistory(vehicle, component, componentType);

        disassembleRequest.setStatus(ConfigurationStatus.PENDING.getId());

        return configurationHistoryRepository.save(disassembleRequest);
    }

    @Transactional
    public List<ConfigurationHistory> sendSwapVehicle(SendConfigSwapDto dto){

        // 1. Lấy vehicle và accessory tương ứng
        InventoryItem leftVeh = inventoryItemService.findByIdOrThrow(dto.getLeftVehicleId());
        InventoryItem rightVeh = inventoryItemService.findByIdOrThrow(dto.getRightVehicleId());

        ComponentType componentType = ComponentType.fromId(dto.getComponentType());
        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        boolean isExistsSwapComponentLeft = configurationHistoryRepository.existsByVehicleIdAndComponentTypeAndConfigType(leftVeh.getId(), componentType.getId(), ConfigurationType.SWAP.getId());

        if(isExistsSwapComponentLeft) throw LogicErrException.of("Xe " + leftVeh.getProductCode() + " hiện đang hoán đổi " + componentType.getValue());

        boolean isExistsSwapComponentRight = configurationHistoryRepository.existsByVehicleIdAndComponentTypeAndConfigType(rightVeh.getId(), componentType.getId(), ConfigurationType.SWAP.getId());

        if(isExistsSwapComponentRight) throw LogicErrException.of("Xe " + rightVeh.getProductCode() + " hiện đang hoán đổi " + componentType.getValue());

        InventoryItem leftVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(leftVeh.getId(), componentType, leftVeh.getProductCode());
        InventoryItem rightVehComponent = inventoryItemService.getComponentItemToVehicleIdAndType(rightVeh.getId(), componentType, rightVeh.getProductCode());

        ConfigurationHistory leftSwapRequest = buildSwapHistory(leftVeh, rightVeh, leftVehComponent, rightVehComponent, componentType);
        leftSwapRequest.setStatus(ConfigurationStatus.PENDING.getId());

        ConfigurationHistory rightSwapRequest = buildSwapHistory(rightVeh, leftVeh, rightVehComponent, leftVehComponent, componentType);
        rightSwapRequest.setConfigurationCode(leftSwapRequest.getConfigurationCode());
        rightSwapRequest.setStatus(ConfigurationStatus.PENDING.getId());

        return List.of(
                configurationHistoryRepository.save(leftSwapRequest),
                configurationHistoryRepository.save(rightSwapRequest)
        );
    }

    public Page<VehicleConfigurationPageDto> getPageVehicleConfigurationPage(PageOptionsDto optionsReq) {
        return configurationHistoryRepository.findPageVehicleConfigurationPage(optionsReq);
    }

    public CheckConfigurationDisassembleDto checkConfigurationDisassemble(ObjectId vehicleId, String componentType){

        ConfigurationHistory configurationHistory = configurationHistoryRepository.findByVehicleIdAndComponentTypeAndConfigType(vehicleId, componentType, ConfigurationType.DISASSEMBLE.getId())
                .orElse(null);

        if(configurationHistory == null) return null;

        CheckConfigurationDisassembleDto res = new CheckConfigurationDisassembleDto();
        res.setConfigurationCode(configurationHistory.getConfigurationCode());
        res.setStatus(configurationHistory.getStatus());

        return res;
    }

    public CheckConfigurationAssembleDto checkConfigurationAssemble(ObjectId vehicleId, String componentType){

        ConfigurationHistory configurationHistory = configurationHistoryRepository.findByVehicleIdAndComponentTypeAndConfigType(vehicleId, componentType, ConfigurationType.ASSEMBLE.getId())
                .orElse(null);

        if(configurationHistory == null) return null;

        InventoryItem componentReplace = inventoryItemService.getItemToId(configurationHistory.getComponentReplaceId());

        Warehouse warehouseComponent = warehouseService.getWarehouseToId(componentReplace.getWarehouseId());

        CheckConfigurationAssembleDto res = new CheckConfigurationAssembleDto();
        res.setConfigurationCode(configurationHistory.getConfigurationCode());
        res.setStatus(configurationHistory.getStatus());
        res.setComponentId(configurationHistory.getComponentReplaceId());
        res.setSerialNumber(configurationHistory.getComponentReplaceSerial());
        res.setWarehouseCode(warehouseComponent.getCode());
        res.setWarehouseName(warehouseComponent.getName());

        ComponentType componentTypeEnum = ComponentType.fromId(configurationHistory.getComponentType());
        res.setComponentName(componentTypeEnum == null ? null : componentTypeEnum.getValue());

        return res;
    }

    public CheckConfigurationSwapDto checkConfigurationSwap(ObjectId vehicleId, String componentType){

        ConfigurationHistory configurationHistory = configurationHistoryRepository.findByVehicleIdAndComponentTypeAndConfigType(vehicleId, componentType, ConfigurationType.SWAP.getId())
                .orElse(null);

        if(configurationHistory == null) return null;

        ConfigurationHistory configurationVehicleRight = configurationHistoryRepository.findByConfigurationCodeAndDiffVehicleId(configurationHistory.getConfigurationCode(), configurationHistory.getVehicleId())
                .orElse(null);

        if(configurationVehicleRight == null) return null;

        InventoryItem vehicleRight = inventoryItemService.getItemToId(configurationVehicleRight.getVehicleId());

        CheckConfigurationSwapDto res = new CheckConfigurationSwapDto();
        res.setConfigurationCode(configurationHistory.getConfigurationCode());
        res.setStatus(configurationHistory.getStatus());
        res.setVehicleId(vehicleRight.getId());
        res.setProductCode(vehicleRight.getProductCode());
        res.setSerialNumber(vehicleRight.getSerialNumber());
        res.setModel(vehicleRight.getModel());

        return res;
    }

    @Transactional
    public ConfigurationHistory updateStatusConfiguration(UpdateStatusConfigurationDto dto){

        ConfigurationHistory configurationHistory = getToId(new ObjectId(dto.getConfigurationId()));

        if(ConfigurationStatus.COMPLETED.getId().equals(configurationHistory.getStatus()))
            throw LogicErrException.of("Cấu hình đã được hoàn tất trước đó.");

        ConfigurationStatus status = ConfigurationStatus.fromId(dto.getStatus());
        if(status == null) throw LogicErrException.of("Trạng thái cần thay đổi không hợp lệ.");

        configurationHistory.setStatus(status.getId());

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        if(ConfigurationStatus.CONFIGURING.getId().equals(status.getId())){
            configurationHistory.setConfirmedBy(customUserDetail.getFullName());
            configurationHistory.setConfirmedAt(LocalDateTime.now());
        }
        else if(ConfigurationStatus.COMPLETED.getId().equals(status.getId())){
            configurationHistory.setCompletedBy(customUserDetail.getFullName());
            configurationHistory.setCompletedAt(LocalDateTime.now());
        }

        configurationHistoryRepository.updateStatus(configurationHistory);

        return configurationHistory;
    }
}
