package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryItemToContainerDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryProductDetailsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventorySparePartDetailsDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.container.request.CreateContainerDto;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.enumerate.ContainerStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.container.ContainerRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemService inventoryItemService;
    private final InventoryItemMapper inventoryItemMapper;

    public Page<ContainerResponseDto> getContainers(PageOptionsDto req) {
        MatchOperation matchStage = Aggregation.match(new Criteria().andOperator(
                Criteria.where("deletedAt").is(null)
        ));
        LookupOperation lookupFromWarehouse = Aggregation.lookup("warehouse", "fromWareHouseId", "_id", "fromWarehouseInfo");
        UnwindOperation unwindFromWarehouse = Aggregation.unwind("fromWarehouseInfo", true);

        LookupOperation lookupToWarehouse = Aggregation.lookup("warehouse", "toWarehouseId", "_id", "toWarehouseInfo");
        UnwindOperation unwindToWarehouse = Aggregation.unwind("toWarehouseInfo", true);

        ProjectionOperation projectStage = Aggregation.project()
                .and("_id").as("id")
                .and("containerCode").as("containerCode")
                .and("containerStatus").as("containerStatus")
                .and("departureDate").as("departureDate")
                .and("arrivalDate").as("arrivalDate")
                .and("completionDate").as("completionDate")
                .and("note").as("note")
                .and("fromWarehouseInfo").as("fromWarehouse")
                .and("toWarehouseInfo").as("toWarehouse")
                .and(
                        ArrayOperators.Reduce.arrayOf("inventoryItems")
                                .withInitialValue(0)
                                .reduce(
                                        ArithmeticOperators.Add.valueOf("$$value")
                                                .add("$$this.pricing.purchasePrice")
                                )
                ).as("totalAmounts");

        Aggregation aggregation = Aggregation.newAggregation(
                matchStage,
                lookupFromWarehouse,
                unwindFromWarehouse,
                lookupToWarehouse,
                unwindToWarehouse,
                projectStage
        );

        return MongoRsqlUtils.queryAggregatePage(
                Container.class,
                ContainerResponseDto.class,
                aggregation,
                req
        );
    }

    @Transactional
    public Container createContainer(CreateContainerDto createDto) {
//        if (containerRepository.existsByContainerCode(createDto.getContainerCode())) {
//            throw new DuplicateResourceException("Container với mã '" + createDto.getContainerCode() + "' đã tồn tại.");
//        }

        Container container = new Container();
        container.setContainerCode(createDto.getContainerCode());

        if (createDto.getFromWareHouseId() != null && !createDto.getFromWareHouseId().isBlank()) {
            container.setFromWareHouseId(new ObjectId(createDto.getFromWareHouseId()));
        }
        if (createDto.getToWarehouseId() != null && !createDto.getToWarehouseId().isBlank()) {
            container.setToWarehouseId(new ObjectId(createDto.getToWarehouseId()));
        }

        container.setDepartureDate(createDto.getDepartureDate());
        container.setArrivalDate(createDto.getArrivalDate());
        container.setNote(createDto.getNote());

        return containerRepository.save(container);
    }

    @Transactional
    public boolean softDeleteContainer(ObjectId containerId) {
        ObjectId currentUserId = new ObjectId("6898d9a81faac9cf6f106d64");
        boolean success = containerRepository.softDeleteById(containerId, currentUserId);
        return success;
    }

    @Transactional
    public boolean bulkSoftDeleteContainers(List<String> idStrings) {
        if (idStrings == null || idStrings.isEmpty()) {
            return false;
        }

        ObjectId currentUserId = new ObjectId("6898d9a81faac9cf6f106d64");

        List<ObjectId> containerIds = idStrings.stream()
                .map(ObjectId::new)
                .toList();

        long deletedCount = containerRepository.bulkSoftDelete(containerIds, currentUserId);

        return deletedCount > 0;
    }

    public Container getContainerToId(ObjectId id){
        Container container = containerRepository.findById(id).orElse(null);
        if(container == null || container.getDeletedAt() != null)
            throw LogicErrException.of("Container không tồn tại.");
        return container;
    }

    public Map<String, ObjectId> pushItems(@RequestBody InventoryItemToContainerDto req){
        if(req.getInventoryItems().isEmpty())
            throw LogicErrException.of("Hàng hóa cần nhập sang kho khác hiện đang rỗng.");

        Container container = getContainerToId(new ObjectId(req.getContainerId()));
        try{
            List<InventoryItem> itemsInContainer = inventoryItemService.transferItems(req.getInventoryItems(), container.getToWarehouseId(), container, container.getArrivalDate(), null, InventoryItemStatus.IN_TRANSIT);
            container.setContainerStatus(ContainerStatus.PENDING);
            container.setInventoryItems(itemsInContainer.stream().map(inventoryItemMapper::toInventoryItemContainer).toList());
            containerRepository.save(container);
            // TODO: Ghi nhận log giao dịch

            return Map.of("containerId", container.getId());
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Thêm hàng vào container thất bại.");
        }
    }
    
    public List<InventoryProductDetailsDto> getInventoryItemsProductToContainerId(String containerId){
        Container container = getContainerToId(new ObjectId(containerId));
        List<InventoryProductDetailsDto> dtos = new ArrayList<>();
        if (container.getInventoryItems() == null) return dtos;
        List<String> vehicleAccessory = List.of(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId());
        for(var item : container.getInventoryItems()){
            if(vehicleAccessory.contains(item.getInventoryType())){
                dtos.add(inventoryItemMapper.toInventoryProductDetailsDto(item));
            }
        }
        return dtos;
    }

    public List<InventorySparePartDetailsDto> getInventoryItemsSparePartToContainerId(String containerId){
        Container container = getContainerToId(new ObjectId(containerId));
        List<InventorySparePartDetailsDto> dtos = new ArrayList<>();
        if (container.getInventoryItems() == null) return dtos;
        for(var item : container.getInventoryItems()){
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                dtos.add(inventoryItemMapper.toInventorySparePartDetailsDto(item));
            }
        }
        return dtos;
    }

    @Transactional
    public Container updateContainerStatus(String containerId, String status){
        Container container = getContainerToId(new ObjectId(containerId));
        if(container.getContainerStatus().equals(ContainerStatus.COMPLETED))
            throw LogicErrException.of("Cont hàng đã được hoàn tất trước đó.");
        ContainerStatus containerStatus = ContainerStatus.fromId(status);
        if(containerStatus == null) throw LogicErrException.of("Trạng thái không hợp lệ.");
        // update container
        if(containerStatus.equals(ContainerStatus.COMPLETED)){
            // Update items nếu là trạng thái hoàn tất giao hàng
            inventoryItemRepository.updateStatusAndUnRefContainer(container.getId(), InventoryItemStatus.IN_STOCK.getId());
            container.setCompletionDate(LocalDateTime.now());
        }
        container.setContainerStatus(containerStatus);
        containerRepository.save(container);
        return container;
    }
}
