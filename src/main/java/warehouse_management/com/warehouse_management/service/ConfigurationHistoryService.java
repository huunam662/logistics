package warehouse_management.com.warehouse_management.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.DropPartRequest;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.VehiclePartSwapRequest;
import warehouse_management.com.warehouse_management.enumerate.AccessoryType;
import warehouse_management.com.warehouse_management.enumerate.SparePartType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.ConfigurationHistoryMapper;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.configuration_history.ConfigurationHistoryRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;


@Service
public class ConfigurationHistoryService {


    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemService inventoryItemService;
    private final ConfigurationHistoryRepository configurationHistoryRepository;
    private final GeneralUtil generalUtil;
    private final ConfigurationHistoryMapper mapper;

    public ConfigurationHistoryService(InventoryItemRepository inventoryItemRepository, InventoryItemService inventoryItemService, ConfigurationHistoryRepository configurationHistoryRepository, GeneralUtil generalUtil, ConfigurationHistoryMapper mapper) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemService = inventoryItemService;
        this.configurationHistoryRepository = configurationHistoryRepository;
        this.generalUtil = generalUtil;
        this.mapper = mapper;
    }

    @Transactional
    public boolean swapItems(VehiclePartSwapRequest request) {
        // 1. Lấy vehicle và accessory tương ứng
        InventoryItem leftVeh = inventoryItemService.findByIdOrThrow(request.getLeftVehicle());
        InventoryItem rightVeh = inventoryItemService.findByIdOrThrow(request.getRightVehicle());

        if (AccessoryType.fromId(request.getPartType()) != null) {
            InventoryItem leftAccessory = inventoryItemRepository
                    .findByVehicleIdAndAccessoryType(new ObjectId(request.getLeftVehicle()), request.getPartType())
                    .orElseThrow(() -> LogicErrException.of("Không tìm thấy accessory của xe trái"));
            InventoryItem rightAccessory = inventoryItemRepository
                    .findByVehicleIdAndAccessoryType(new ObjectId(request.getRightVehicle()), request.getPartType())
                    .orElseThrow(() -> LogicErrException.of("Không tìm thấy accessory của xe phải"));

            AccessoryType accessoryType = AccessoryType.fromId(request.getPartType());

            List<ConfigurationHistory> histsToSave = new ArrayList<>();
            buildHistory(leftVeh, rightAccessory, leftAccessory, accessoryType, histsToSave);
            buildHistory(rightVeh, leftAccessory, rightAccessory, accessoryType, histsToSave);

            // 2. Swap accessory ref
            swapVehicleRef(leftAccessory, rightAccessory, leftVeh, rightVeh);

            // 3. Swap thuộc tính cha nếu cần
            swapVehicleAccessory(leftVeh, rightVeh, accessoryType);

            // 4. Set giá mới từ request
            setVehiclePrices(leftVeh, rightVeh, request);

            // 5. Build history

            // 6. Lưu tất cả
            inventoryItemRepository.saveAll(List.of(leftAccessory, rightAccessory, leftVeh, rightVeh));
            configurationHistoryRepository.saveAll(histsToSave);
        } else if (SparePartType.fromId(request.getPartType()) != null) {


            SparePartType sparePartType = SparePartType.fromId(request.getPartType());
            List<ConfigurationHistory> histsToSave = new ArrayList<>();
            buildHistory(leftVeh, rightVeh, sparePartType, histsToSave);
            buildHistory(rightVeh, leftVeh, sparePartType, histsToSave);

            // 2. Swap accessory ref
//            swapVehicleRef(leftVeh, rightVeh, leftVeh, rightVeh);

            // 3. Swap thuộc tính cha nếu cần
            swapVehicleSparePart(leftVeh, rightVeh, sparePartType);

            // 4. Set giá mới từ request
            setVehiclePrices(leftVeh, rightVeh, request);

            // 5. Build history

            // 6. Lưu tất cả
            inventoryItemRepository.saveAll(List.of(leftVeh, rightVeh));
            configurationHistoryRepository.saveAll(histsToSave);
        }


        return true;
    }

// --------------------------- Helpers ---------------------------

    private void swapVehicleRef(InventoryItem leftAccessory, InventoryItem rightAccessory, InventoryItem
            leftVeh, InventoryItem rightVeh) {
        leftAccessory.setVehicleId(rightVeh.getId());
        rightAccessory.setVehicleId(leftVeh.getId());
    }

    private void swapVehicleAccessory(InventoryItem leftVeh, InventoryItem rightVeh, AccessoryType accessoryType) {
        switch (accessoryType) {
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
        }
    }

    private void swapVehicleSparePart(InventoryItem leftVeh, InventoryItem rightVeh, SparePartType sparePartType) {
        switch (sparePartType) {
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
            case SIDESHIFT -> {
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

    private void buildHistory(
            InventoryItem vehicle,
            InventoryItem accessory,
            InventoryItem prevAccessory,
            AccessoryType accessoryType,
            List<ConfigurationHistory> histsToSave
    ) {
        ConfigurationHistory latestHistOpt =
                configurationHistoryRepository.findFirstByVehicleIdAndIsLatestTrue(vehicle.getId()).orElseThrow(() -> LogicErrException.of("K tìm thấy bản mới nhất "));
        latestHistOpt.setLatest(false);
        histsToSave.add(latestHistOpt);

        ConfigurationHistory newHist = mapper.clone(latestHistOpt);

        newHist.setLatest(true);


        // Set giá mới vào history


        // Set accessory hiện tại & trước
        switch (accessoryType) {
            case LIFTING_FRAME -> {
                if (accessory != null) {
                    newHist.setLiftingFrameId(accessory.getId());
                    newHist.setLiftingFrameLabel(generalUtil.buildLiftingFrameLabel(accessory));
                } else {
                    newHist.setLiftingFrameId(null);
                    newHist.setLiftingFrameLabel(null);
                }
                if (prevAccessory != null) {
                    newHist.setPrevLiftingFrameId(prevAccessory.getId());
                    newHist.setPrevLiftingFrameLabel(generalUtil.buildLiftingFrameLabel(prevAccessory));
                }
            }
            case BATTERY -> {
                if (accessory != null) {
                    newHist.setBatteryId(accessory.getId());
                    newHist.setBatteryLabel(generalUtil.buildBatteryLabel(accessory));
                } else {
                    newHist.setBatteryId(null);
                    newHist.setBatteryLabel(null);
                }
                if (prevAccessory != null) {
                    newHist.setPrevBatteryId(prevAccessory.getId());
                    newHist.setPrevBatteryLabel(generalUtil.buildBatteryLabel(prevAccessory));
                }
            }
            case CHARGER -> {
                if (accessory != null) {
                    newHist.setChargerId(accessory.getId());
                    newHist.setChargerLabel(generalUtil.buildChargerLabel(accessory));
                } else {
                    newHist.setChargerId(null);
                    newHist.setChargerLabel(null);
                }
                if (prevAccessory != null) {
                    newHist.setPrevChargerId(prevAccessory.getId());
                    newHist.setPrevChargerLabel(generalUtil.buildChargerLabel(prevAccessory));
                }
            }
        }

        newHist.setNote("TEST1");
        histsToSave.add(newHist);
    }

    private void buildHistory(
            InventoryItem leftVeh,
            InventoryItem rightVeh,
            SparePartType sparePartType,
            List<ConfigurationHistory> histsToSave
    ) {
        ConfigurationHistory latestHistOpt =
                configurationHistoryRepository.findFirstByVehicleIdAndIsLatestTrue(leftVeh.getId()).orElseThrow(() -> LogicErrException.of("K tìm thấy bản mới nhất "));
        latestHistOpt.setLatest(false);
        histsToSave.add(latestHistOpt);

        ConfigurationHistory newHist = mapper.clone(latestHistOpt);

        newHist.setLatest(true);


        // Set accessory hiện tại & trước
        switch (sparePartType) {
            case FORK -> {
                newHist.setForkDimensions(rightVeh.getSpecifications().getForkDimensions());
                newHist.setPrevForkDimensions(leftVeh.getSpecifications().getForkDimensions());
            }
            case ENGINE -> {
                newHist.setEngineType(rightVeh.getSpecifications().getEngineType());
                newHist.setPrevEngineType(leftVeh.getSpecifications().getEngineType());

            }
            case VALVE -> {
                newHist.setValveCount(rightVeh.getSpecifications().getValveCount());
                newHist.setPrevValveCount(leftVeh.getSpecifications().getValveCount());

            }
            case SIDESHIFT -> {
                newHist.setHasSideShift(rightVeh.getSpecifications().getHasSideShift());
                newHist.setPrevHasSideShift(leftVeh.getSpecifications().getHasSideShift());
            }
        }


        newHist.setNote("TEST2");
        histsToSave.add(newHist);
    }

    private void buildHistory(
            InventoryItem veh,
            SparePartType sparePartType,
            List<ConfigurationHistory> histsToSave
    ) {
        ConfigurationHistory latestHistOpt =
                configurationHistoryRepository.findFirstByVehicleIdAndIsLatestTrue(veh.getId()).orElseThrow(() -> LogicErrException.of("K tìm thấy bản mới nhất "));
        latestHistOpt.setLatest(false);
        histsToSave.add(latestHistOpt);

        ConfigurationHistory newHist = mapper.clone(latestHistOpt);

        newHist.setLatest(true);


        // Set accessory hiện tại & trước
        switch (sparePartType) {
            case FORK -> {
                newHist.setPrevForkDimensions(newHist.getForkDimensions());
                newHist.setForkDimensions(null);
            }
            case ENGINE -> {
                newHist.setPrevEngineType(newHist.getEngineType());
                newHist.setEngineType(null);
            }
            case VALVE -> {
                newHist.setPrevValveCount(newHist.getValveCount());
                newHist.setValveCount(null);
            }
            case SIDESHIFT -> {
                newHist.setPrevHasSideShift(newHist.getHasSideShift());
                newHist.setHasSideShift(null);
            }
        }

        newHist.setNote("TEST3");
        histsToSave.add(newHist);
    }

    public ConfigurationHistory findByIdOrThrow(String id) {
        return configurationHistoryRepository.findById(new ObjectId(id)).orElseThrow(() -> LogicErrException.of("Không tồn tại"));
    }

    @Transactional
    public boolean dropPart(DropPartRequest dropPartRequest) {
        String vehId = dropPartRequest.getVehicleId();
        InventoryItem veh = inventoryItemService.findByIdOrThrow(vehId);
        String partType = dropPartRequest.getPartType();
        String newCode = dropPartRequest.getPartCode();
        if (AccessoryType.fromId(partType) != null) {
            InventoryItem pk = inventoryItemRepository
                    .findByVehicleIdAndAccessoryType(new ObjectId(dropPartRequest.getVehicleId()), dropPartRequest.getPartType())
                    .orElseThrow(() -> LogicErrException.of("Không tìm thấy accessory của xe trái"));
            AccessoryType accessoryType = AccessoryType.fromId(partType);
            List<ConfigurationHistory> histsToSave = new ArrayList<>();
//            drop ref
            pk.setProductCode(newCode);
            pk.setVehicleId(null);

            inventoryItemRepository.save(pk);
            buildHistory(veh, null, pk, accessoryType, histsToSave);
            dropAccessory(veh, accessoryType);

            inventoryItemRepository.save(veh);
            configurationHistoryRepository.saveAll(histsToSave);
        } else if (SparePartType.fromId(partType) != null) {
            SparePartType sparePartType = SparePartType.fromId(partType);
            List<ConfigurationHistory> histsToSave = new ArrayList<>();

            buildHistory(veh, sparePartType, histsToSave);
            dropSparePart(veh, sparePartType);

            inventoryItemRepository.save(veh);
            configurationHistoryRepository.saveAll(histsToSave);
            InventoryItem pt = inventoryItemRepository.findById(new ObjectId(newCode)).orElseThrow(() -> LogicErrException.of("PT not found"));
            pt.setQuantity(pt.getQuantity() + 1);
            inventoryItemRepository.save(pt);
        }

        return true;
    }

    private void dropSparePart(InventoryItem veh,
                               SparePartType sparePartType) {
        switch (sparePartType) {
            case ENGINE -> {
                veh.getSpecifications().setEngineType(null);
            }
            case FORK -> {
                veh.getSpecifications().setForkDimensions(null);
            }
            case SIDESHIFT -> {
                veh.getSpecifications().setHasSideShift(null);
            }
            case VALVE -> {
                veh.getSpecifications().setValveCount(null);
            }
        }

    }

    private void dropAccessory(InventoryItem veh,
                               AccessoryType accessoryType) {
        switch (accessoryType) {
            case LIFTING_FRAME -> {
                veh.getSpecifications().setLiftingHeightMm(null);
                veh.getSpecifications().setLiftingCapacityKg(null);
                veh.getSpecifications().setChassisType(null);

            }
            case BATTERY -> {
                veh.getSpecifications().setBatteryInfo(null);
                veh.getSpecifications().setBatterySpecification(null);
            }
            case CHARGER -> {
                veh.getSpecifications().setChargerSpecification(null);

            }
        }

    }

}
