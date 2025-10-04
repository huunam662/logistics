package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.*;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.ItemCodeModelSerialDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.repair.request.*;
import warehouse_management.com.warehouse_management.dto.repair.response.CheckRepairAssembleDto;
import warehouse_management.com.warehouse_management.dto.repair.response.CheckRepairDisassembleDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.dto.repair.response.VehicleRepairPageDto;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.*;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.repair.RepairRepository;
import warehouse_management.com.warehouse_management.repository.repair_transaction.RepairTransactionRepository;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class RepairService {

    private final RepairRepository repairRepository;
    private final RepairTransactionRepository repairTransactionRepository;
    private final InventoryItemService inventoryItemService;
    private final CustomAuthentication customAuthentication;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;
    private final WarehouseService warehouseService;

    public Repair getToId(ObjectId id){

        Repair repair = repairRepository.findById(id).orElse(null);
        if(repair == null || repair.getDeletedAt() == null)
            throw LogicErrException.of("Phiếu sửa chữa hiện không tồn tại");

        return repair;
    }

    public Repair getToRepairCode(String repairCode){

        Repair repair = repairRepository.findByRepairCode(repairCode).orElse(null);
        if(repair == null || repair.getDeletedAt() == null)
            throw LogicErrException.of("Phiếu sửa chữa hiện không tồn tại");

        return repair;
    }

    @Transactional
    protected Repair buildRepair(InventoryItem vehicle, InventoryItem component, String repairType, LocalDate expectedCompletionDate) {

        RepairType repairTypeEnum = RepairType.fromString(repairType);
        if(repairTypeEnum == null) throw LogicErrException.of("Loại sửa chữa không hợp lệ.");

        ComponentType componentType = ComponentType.fromId(component.getComponentType());
        if(componentType == null) throw LogicErrException.of("Loại bộ phận không hợp lệ.");

        Repair repair = new Repair();
        repair.setDescription(repairTypeEnum.getValue() + " " + componentType.getValue() + " cho " + vehicle.getProductCode());
        repair.setRepairType(repairType);
        repair.setVehicleId(vehicle.getId());
        repair.setComponentId(component.getId());
        repair.setComponentSerialNumber(component.getSerialNumber());
        repair.setComponentType(componentType.getId());
        repair.setExpectedCompletionDate(expectedCompletionDate);

        return repairRepository.save(repair);
    }

    @Transactional
    public Repair pushReasonForRepair(CreateRepairTransactionDto dto){

        Repair repair = getToId(new ObjectId(dto.getRepairId()));

        if(dto.getRepairTransactions() == null) return repair;

        List<ObjectId> changeTransactionIds = dto.getRepairTransactions()
                .stream()
                .filter(e -> e.getRepairTransactionId() != null)
                .map(e -> new ObjectId(e.getRepairTransactionId()))
                .toList();

        List<RepairTransaction> repairTransactionsList = repairTransactionRepository.findAllByRepairIdAndIdIn(repair.getId(), changeTransactionIds);

        Map<ObjectId, RepairTransaction> repairTransactionsMap = repairTransactionsList
                .stream()
                .collect(Collectors.toMap(RepairTransaction::getId, e -> e));

        List<RepairTransaction> repairTransactionsForUpdate = new ArrayList<>();
        List<RepairTransaction> repairTransactionsForCreate = new ArrayList<>();

        for(var repairTrans : dto.getRepairTransactions()){
            if(repairTrans.getRepairTransactionId() == null){

                RepairTransaction repairTransaction = new RepairTransaction();
                repairTransaction.setIsRepaired(false);
                repairTransaction.setReason(repairTrans.getReason());
                repairTransaction.setRepairId(repair.getId());

                repairTransactionsForCreate.add(repairTransaction);
            }
            else{
                RepairTransaction transExists = repairTransactionsMap.getOrDefault(new ObjectId(repairTrans.getRepairTransactionId()), null);

                boolean isChangeTransaction = transExists != null && !transExists.getReason().equals(repairTrans.getReason());

                if(isChangeTransaction) {

                    transExists.setReason(repairTrans.getReason());

                    repairTransactionsForUpdate.add(transExists);
                }
            }
        }

        repairTransactionRepository.bulkInsert(repairTransactionsForCreate);
        repairTransactionRepository.bulkUpdateReasonAndIsRepaired(repairTransactionsForUpdate);

        return repair;
    }

    @Transactional
    public void completedRepairVehicle(ConfigurationCompletedDto dto) {

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dto.getVehicleId()));

        List<Repair> repairList = repairRepository.findAllUnCompletedByVehicleId(vehicle.getId());

        if(!repairList.isEmpty()){

            Repair repair = repairList.getFirst();

            RepairStatus repairStatus = RepairStatus.fromId(repair.getStatus());
            ComponentType componentType = ComponentType.fromId(repair.getComponentType());
            RepairType repairType = RepairType.fromString(repair.getRepairType());

            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " hiện " + repairStatus.getValue() + " để " + repairType.getValue());
        }

        repairList = repairRepository.findAllCompletedAndUnPerformedByVehicleId(vehicle.getId());

        if(!repairList.isEmpty()){

            Repair repair = repairList.getFirst();

            ComponentType componentType = ComponentType.fromId(repair.getComponentType());
            RepairType configurationType = RepairType.fromString(repair.getRepairType());

            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " hiện đã HOÀN TẤT sữa chữa, hãy THỰC HIỆN " + configurationType.getValue());
        }

        ComponentType missingComponentType = getValueMissingComponent(vehicle);
        if(missingComponentType != null)
            throw LogicErrException.of("Bộ phận " + missingComponentType.getValue() + " hiện đang THIẾU, hãy lựa chọn LẮP RÁP mới.");

        inventoryItemRepository.updateStatusByIdIn(List.of(vehicle.getId()), InventoryItemStatus.IN_STOCK.getId());
    }

    protected ComponentType getValueMissingComponent(InventoryItem vehicle){

        List<String> componentTypeList = inventoryItemRepository.findComponentTypeByVehicleId(vehicle.getId());

        Map<String, String> componentTypeMap = componentTypeList.stream()
                .map(ComponentType::fromId)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ComponentType::getId, ComponentType::getValue));

        if(vehicle.getSpecifications().getChassisType() != null && componentTypeMap.getOrDefault(ComponentType.LIFTING_FRAME.getId(), null) == null)
            return ComponentType.LIFTING_FRAME;

        if(vehicle.getSpecifications().getBatteryInfo() != null && componentTypeMap.getOrDefault(ComponentType.BATTERY.getId(), null) == null)
            return ComponentType.BATTERY;

        if(vehicle.getSpecifications().getValveCount() != null && componentTypeMap.getOrDefault(ComponentType.VALVE.getId(), null) == null)
            return ComponentType.VALVE;

        if(vehicle.getSpecifications().getForkDimensions() != null && componentTypeMap.getOrDefault(ComponentType.FORK.getId(), null) == null)
            return ComponentType.FORK;

        if(vehicle.getSpecifications().getWheelInfo() != null && componentTypeMap.getOrDefault(ComponentType.WHEEL.getId(), null) == null)
            return ComponentType.WHEEL;

        if(vehicle.getSpecifications().getEngineType() != null && componentTypeMap.getOrDefault(ComponentType.ENGINE.getId(), null) == null)
            return ComponentType.ENGINE;

        if(vehicle.getSpecifications().getHasSideShift() != null && componentTypeMap.getOrDefault(ComponentType.SIDE_SHIFT.getId(), null) == null)
            return ComponentType.SIDE_SHIFT;

        if(vehicle.getSpecifications().getChargerSpecification() != null && componentTypeMap.getOrDefault(ComponentType.CHARGER.getId(), null) == null)
            return ComponentType.CHARGER;

        return null;
    }

    @Transactional
    public void updateStatusRepair(UpdateStatusRepairDto dto){

        Repair repair = getToId(new ObjectId(dto.getRepairId()));

        if(RepairStatus.COMPLETED.getId().equals(repair.getStatus()))
            throw LogicErrException.of("Đơn sửa chữa đã hoàn tất trước đó.");

        RepairStatus status = RepairStatus.fromId(dto.getStatus());
        if(status == null) throw LogicErrException.of("Trạng thái đơn sửa chữa không hợp lệ.");

        repair.setStatus(status.getId());

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        if(RepairStatus.REPAIRING.getId().equals(status.getId())){
            repair.setConfirmedBy(customUserDetail.getFullName());
            repair.setConfirmedAt(LocalDateTime.now());
        }
        else if(RepairStatus.COMPLETED.getId().equals(status.getId())){
            repair.setCompletedBy(customUserDetail.getFullName());
            repair.setCompletedAt(LocalDateTime.now());

            List<ObjectId> repairTransactionIds = repairTransactionRepository.findAllIdByRepairIdAndIdInAndIsRepaired(repair.getId(), false);
            repairTransactionRepository.bulkUpdateIsRepaired(repairTransactionIds, true);
        }

        repairRepository.updateStatus(repair);
    }

    @Transactional
    public void addVehicleToRepair(AddVehicleToConfigurationDto request){
        List<ObjectId> vehicleIds = request.getVehicleIds().stream().map(ObjectId::new).toList();
        List<InventoryItem> vehicles = inventoryItemRepository.findByIdInAndStatus(vehicleIds, InventoryItemStatus.IN_STOCK.getId());
        List<ObjectId> vehiclesToConfig = vehicles.stream()
                .filter(o -> InventoryType.VEHICLE.getId().equalsIgnoreCase(o.getInventoryType()))
                .map(InventoryItem::getId)
                .toList();
        inventoryItemRepository.updateStatusByIdIn(vehiclesToConfig, InventoryItemStatus.IN_REPAIR.getId());
    }

    public CheckRepairDisassembleDto checkRepairDisassembleOrRepair(ObjectId vehicleId, String componentType, Boolean isRepair){

        Repair repair = null;

        if(isRepair){
            repair = repairRepository.findByVehicleIdAndComponentTypeAndRepairType(vehicleId, componentType, RepairType.REPAIR.getId())
                    .orElse(null);
        }
        else{
            repair = repairRepository.findByVehicleIdAndComponentTypeAndRepairType(vehicleId, componentType, RepairType.DISASSEMBLE.getId())
                        .orElse(null);
        }

        if(repair == null) return null;

        CheckRepairDisassembleDto res = new CheckRepairDisassembleDto();
        res.setRepairCode(repair.getRepairCode());
        res.setStatus(repair.getStatus());

        return res;
    }

    public CheckRepairAssembleDto checkRepairAssemble(ObjectId vehicleId, String componentType){

        Repair repair = repairRepository.findByVehicleIdAndComponentTypeAndRepairType(vehicleId, componentType, RepairType.ASSEMBLE.getId())
                .orElse(null);

        if(repair == null) return null;

        InventoryItem componentReplace = inventoryItemService.getItemToId(repair.getComponentId());

        Warehouse warehouseComponent = warehouseService.getWarehouseToId(componentReplace.getWarehouseId());

        CheckRepairAssembleDto res = new CheckRepairAssembleDto();
        res.setRepairCode(repair.getRepairCode());
        res.setStatus(repair.getStatus());
        res.setComponentId(repair.getComponentId());
        res.setSerialNumber(repair.getComponentSerialNumber());
        res.setWarehouseCode(warehouseComponent.getCode());
        res.setWarehouseName(warehouseComponent.getName());

        ComponentType componentTypeEnum = ComponentType.fromId(componentReplace.getComponentType());
        res.setComponentName(componentTypeEnum == null ? null : componentTypeEnum.getValue());

        return res;
    }

    @Transactional
    public Repair sendAssembleVehicle(SendRepairAssembleDto dto){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dto.getVehicleId()));

        InventoryItem component = inventoryItemService.getItemToId(new ObjectId(dto.getComponentId()));

        ComponentType componentType = ComponentType.fromId(component.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận không hợp lệ");

        boolean isExistsAssembleComponent = repairRepository.existsByVehicleIdAndComponentTypeAndRepairType(vehicle.getId(), componentType.getId(), ConfigurationType.ASSEMBLE.getId());

        if(isExistsAssembleComponent) throw LogicErrException.of("Xe " + vehicle.getProductCode() + " hiện đang sửa chữa lắp ráp " + componentType.getValue());

        if(vehicle.getId().equals(component.getVehicleId())){
            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " đã có sẵn trong xe " + vehicle.getProductCode());
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

        inventoryItemRepository.updateStatusByIdIn(List.of(component.getId()), InventoryItemStatus.IN_REPAIR.getId());

        Repair assembleRequest = buildRepair(vehicle, component, RepairType.ASSEMBLE.getId(), dto.getExpectedCompletionDate());

        assembleRequest.setStatus(RepairStatus.PENDING.getId());

        return repairRepository.save(assembleRequest);
    }

    @Transactional
    public Repair sendDisassembleVehicle(SendRepairDisassembleDto dto){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dto.getVehicleId()));

        ComponentType componentType = ComponentType.fromId(dto.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        boolean isExistsDisassembleComponent = repairRepository.existsByVehicleIdAndComponentTypeAndRepairType(vehicle.getId(), componentType.getId(), RepairType.DISASSEMBLE.getId());

        if(isExistsDisassembleComponent) throw LogicErrException.of("Xe " + vehicle.getProductCode() + " hiện đang tháo rời " + componentType.getValue());

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());

        Repair disassembleRequest = buildRepair(vehicle, component, RepairType.DISASSEMBLE.getId(), dto.getExpectedCompletionDate());

        disassembleRequest.setStatus(ConfigurationStatus.PENDING.getId());

        return repairRepository.save(disassembleRequest);
    }

    @Transactional
    public boolean repairDisassembleComponent(RepairDisassembleComponentDto dropPartRequest) {

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dropPartRequest.getVehicleId()));

        ComponentType componentType = ComponentType.fromId(dropPartRequest.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());

        component.setStatus(InventoryItemStatus.DESTROY.getId());
        component.setVehicleId(null);
        component.setWarehouseId(vehicle.getWarehouseId());

        inventoryItemRepository.save(component);

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        repairRepository.updatePerformed(dropPartRequest.getRepairCode(), customUserDetail.getFullName());

        vehicle.setIsFullyComponent(false);

        return true;
    }

    @Transactional
    public boolean repairAssembleComponent(RepairAssembleComponentDto assemblePart){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(assemblePart.getVehicleId()));

        InventoryItem component = inventoryItemService.getItemToId(new ObjectId(assemblePart.getComponentId()));
        ComponentType componentType = ComponentType.fromId(component.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận không hợp lệ");

        if(vehicle.getId().equals(component.getVehicleId())){
            throw LogicErrException.of("Bộ phận " + componentType.getValue() + " đã có sẵn trong xe " + vehicle.getProductCode());
        }

        component.setVehicleId(vehicle.getId());
        component.setStatus(InventoryItemStatus.IN_VEHICLE.getId());

        component = inventoryItemRepository.save(component);

        assembleSpecifications(vehicle, componentType, component);

        setSerialComponent(vehicle, component, componentType);

        vehicle.setIsFullyComponent();

        inventoryItemRepository.save(vehicle);

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        repairRepository.updatePerformed(assemblePart.getRepairCode(), customUserDetail.getFullName());

        return true;
    }

    protected void setSerialComponent(InventoryItem vehicle, InventoryItem component, ComponentType componentType){

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

    protected void assembleSpecifications(InventoryItem veh, ComponentType componentType, InventoryItem component) {
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

    public List<VehicleComponentTypeDto> getComponentTypeMissingToVehicleId(ObjectId vehicleId){

        InventoryItem vehicle = inventoryItemService.getItemToId(vehicleId);

        List<String> componentTypes = inventoryItemRepository.findComponentTypeByVehicleId(vehicleId);

        List<ComponentType> componentTypesMissing = Arrays.stream(ComponentType.values())
                .filter(o -> !componentTypes.contains(o.getId()))
                .toList();

        return componentTypesMissing.stream()
                .filter(e -> (vehicle.getSpecifications().getChassisType() != null && componentTypesMissing.contains(ComponentType.LIFTING_FRAME))
                        || (vehicle.getSpecifications().getBatteryInfo() != null && componentTypesMissing.contains(ComponentType.BATTERY))
                        || (vehicle.getSpecifications().getValveCount() != null && componentTypesMissing.contains(ComponentType.VALVE))
                        || (vehicle.getSpecifications().getEngineType() != null && componentTypesMissing.contains(ComponentType.ENGINE))
                        || (vehicle.getSpecifications().getChargerSpecification() != null && componentTypesMissing.contains(ComponentType.CHARGER))
                        || (vehicle.getSpecifications().getHasSideShift() != null && componentTypesMissing.contains(ComponentType.SIDE_SHIFT))
                        || (vehicle.getSpecifications().getWheelInfo() != null && componentTypesMissing.contains(ComponentType.WHEEL))
                        || (vehicle.getSpecifications().getForkDimensions() != null && componentTypesMissing.contains(ComponentType.FORK)))
                .map(o -> {
                    VehicleComponentTypeDto res = new VehicleComponentTypeDto();
                    res.setComponentType(o.getId());
                    res.setComponentName(o.getValue());
                    return res;
                })
                .toList();
    }

    public List<VehicleComponentTypeDto> getComponentTypeToVehicleId(ObjectId vehicleId, Boolean isRepair){

        List<String> componentTypes = inventoryItemRepository.findComponentTypeByVehicleId(vehicleId);

        List<String> componentTypesToFilter;

        if(isRepair) componentTypesToFilter = repairRepository.findAllComponentUnRepairAndUnCompletedByVehicleId(vehicleId);
        else componentTypesToFilter = repairRepository.findAllComponentRepairAndUnCompletedByVehicleId(vehicleId);

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

    @Transactional
    public Repair sendRepairComponentVehicle(SendRepairComponentDto dto){

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dto.getVehicleId()));

        ComponentType componentType = ComponentType.fromId(dto.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần sửa chữa không hợp lệ.");

        boolean isExistsRepairComponent = repairRepository.existsByVehicleIdAndComponentTypeAndRepairType(vehicle.getId(), componentType.getId(), RepairType.REPAIR.getId());

        if(isExistsRepairComponent) throw LogicErrException.of("Xe " + vehicle.getProductCode() + " hiện đang sửa chữa " + componentType.getValue());

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());

        inventoryItemRepository.updateStatusByIdIn(List.of(component.getId()), InventoryItemStatus.IN_REPAIR.getId());

        Repair repairRequest = buildRepair(vehicle, component, RepairType.REPAIR.getId(), dto.getExpectedCompletionDate());

        repairRequest.setStatus(ConfigurationStatus.PENDING.getId());

        return repairRepository.save(repairRequest);
    }

    @Transactional
    public boolean repairComponentVehicle(RepairComponentDto dto) {

        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(dto.getVehicleId()));

        ComponentType componentType = ComponentType.fromId(dto.getComponentType());

        if(componentType == null) throw LogicErrException.of("Loại bộ phận cần tháo rời không hợp lệ.");

        InventoryItem component = inventoryItemService.getComponentItemToVehicleIdAndType(vehicle.getId(), componentType, vehicle.getProductCode());

        inventoryItemRepository.updateStatusByIdIn(List.of(component.getId()), InventoryItemStatus.IN_VEHICLE.getId());

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        repairRepository.updatePerformed(dto.getRepairCode(), customUserDetail.getFullName());

        return true;
    }

    public Page<ItemCodeModelSerialDto> getPageVehicleInStock(PageOptionsDto optionsDto){
        return inventoryItemRepository.findPageVehicleInStock(optionsDto);
    }

    public Page<VehicleRepairPageDto> getPageVehicleRepairPage(PageOptionsDto optionsReq) {
        return repairRepository.findPageVehicleRepairPage(optionsReq);
    }

    public Page<RepairVehicleSpecPageDto> getPageRepairVehicleSpec(PageOptionsDto optionsDto){
        return inventoryItemRepository.findPageRepairVehicleSpec(optionsDto);
    }

//    public ConfigVehicleSpecHistoryDto getConfigurationHistoryToVehicleId(ObjectId vehicleId){
//        InventoryItem vehicle = inventoryItemService.getItemToId(new ObjectId(vehicleId.toString()));
//        if(!InventoryType.VEHICLE.getId().equals(vehicle.getInventoryType()))
//            throw LogicErrException.of("Sản phẩm cần xem lịch sử cấu hình không phải là xe.");
//
//        List<ConfigurationHistory> configHistories = configurationHistoryRepository.findByVehicleIdOrderByCreatedAtDesc(vehicle.getId());
//
//        ConfigVehicleSpecHistoryDto configVehicleSpecHistory = configurationHistoryMapper.toConfigVehicleSpecHistoryResponse(vehicle);
//
//        configVehicleSpecHistory.setSpecificationsBase(buildSpecificationsBaseResponse(vehicle));
//
//        configVehicleSpecHistory.setConfigHistories(
//                configHistories.stream()
//                        .map(o -> {
//                            ConfigurationHistoryDto res = configurationHistoryMapper.toConfigurationHistoryResponse(o);
//                            ComponentType componentType = ComponentType.fromId(o.getComponentType());
//                            res.setComponentName(componentType == null ? null : componentType.getValue());
//                            return res;
//                        })
//                        .toList()
//        );
//
//        return configVehicleSpecHistory;
//    }
}
