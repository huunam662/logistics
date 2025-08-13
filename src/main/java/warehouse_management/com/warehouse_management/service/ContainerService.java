package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryItemToContainerDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryTransferWarehouseDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;


    public Page<ContainerResponseDto> getContainers(PageOptionsDto req) {
        MatchOperation matchStage = Aggregation.match(Criteria.where("deletedAt").is(null));
        LookupOperation lookupFromWarehouse = Aggregation.lookup("warehouse", "fromWareHouseId", "_id", "fromWarehouseInfo");
        UnwindOperation unwindFromWarehouse = Aggregation.unwind("$fromWarehouseInfo", true);

        LookupOperation lookupToWarehouse = Aggregation.lookup("warehouse", "toWarehouseId", "_id", "toWarehouseInfo");
        UnwindOperation unwindToWarehouse = Aggregation.unwind("$toWarehouseInfo", true);

        ProjectionOperation projectStage = Aggregation.project()
                .and("_id").as("id")
                .and("containerCode").as("containerCode")
                .and("containerStatus").as("containerStatus")
                .and("departureDate").as("departureDate")
                .and("arrivalDate").as("arrivalDate")
                .and("status").as("status")
                .and("note").as("note")
                .and("fromWarehouseInfo").as("fromWarehouse")
                .and("toWarehouseInfo").as("toWarehouse");

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
        Container container = getContainerToId(new ObjectId(req.getContainerId()));
        try{
            // Hệ thống bắt đầu một giao dịch (transaction)
            Map<String, Integer> itemIdQualityMap = req.getInventoryItems().stream().collect(
                    Collectors.toMap(InventoryItemToContainerDto.InventoryItemTransfer::getId, InventoryItemToContainerDto.InventoryItemTransfer::getQuantity)
            );
            List<ObjectId> itemIdToTransfer = itemIdQualityMap.keySet().stream().map(ObjectId::new).toList();
            // Lấy toàn bộ sản phẩm (theo mã sản phẩm) trong Kho chờ sản xuất có PO được chọn
            List<InventoryItem> itemsToTransfer = inventoryItemRepository.findByIdIn(itemIdToTransfer);
            List<InventoryItem> itemsSparePartToNew = new ArrayList<>();
            for(var item : itemsToTransfer){
                if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                    int quantityToTransfer = itemIdQualityMap.get(item.getId().toString());
                    if(item.getQuantity() == 0)
                        throw LogicErrException.of("Hàng phụ tùng " + item.getProductCode() + " hiện hết hàng.");
                    else if(item.getQuantity() < quantityToTransfer)
                        throw LogicErrException.of("Số lượng phụ tùng " + item.getProductCode() + " cần nhập vượt quá số lượng trong kho.");
                    else if(item.getQuantity() > quantityToTransfer){
                        InventoryItem sparePartToDeparture = inventoryItemMapper.cloneEntity(item);
                        sparePartToDeparture.setId(null);
                        sparePartToDeparture.setContainerId(container.getId());
                        sparePartToDeparture.setQuantity(quantityToTransfer);
                        // Kho hiện tại → “Kho đi (TQ)”
                        sparePartToDeparture.setWarehouseId(container.getToWarehouseId());
                        sparePartToDeparture.setStatus(InventoryItemStatus.IN_TRANSIT);
                        // Ngày giao hàng = ngày đã chọn theo PO
                        sparePartToDeparture.getLogistics().setArrivalDate(container.getArrivalDate());
                        itemsSparePartToNew.add(sparePartToDeparture);
                        item.setQuantity(item.getQuantity() - quantityToTransfer);
                        continue;
                    }
                }

                // Kho hiện tại → “Kho đi (TQ)”
                item.setWarehouseId(container.getToWarehouseId());
                item.setContainerId(container.getId());
                item.setStatus(InventoryItemStatus.IN_TRANSIT);
                // Ngày giao hàng = ngày đã chọn theo PO
                item.getLogistics().setArrivalDate(container.getArrivalDate());
            }
            inventoryItemRepository.insertAll(itemsSparePartToNew);
            inventoryItemRepository.bulkUpdateTransferDeparture(itemsToTransfer);

            // TODO: Ghi nhận log giao dịch

            return Map.of(
                    "containerId", container.getId()
            );
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Thêm hàng vào container thất bại.");
        }
    }

    public List<InventoryItem> getInventoryItemsToId(String containerId){
        Container container = getContainerToId(new ObjectId(containerId));
        return inventoryItemRepository.findByContainerId(container.getId());
    }

    @Transactional
    public Container updateContainerStatus(String containerId, String status){
        Container container = getContainerToId(new ObjectId(containerId));
        if(container.getContainerStatus().equals(ContainerStatus.COMPLETED))
            throw LogicErrException.of("Cont hàng đã được hoàn tất trước đó.");
        ContainerStatus containerStatus = ContainerStatus.fromId(status);
        if(containerStatus == null) throw LogicErrException.of("Trạng thái không hợp lệ.");
        container.setContainerStatus(containerStatus);
        containerRepository.save(container);
        if(containerStatus.equals(ContainerStatus.COMPLETED)){
            inventoryItemRepository.updateStatusByContainerId(container.getId(), InventoryItemStatus.IN_STOCK.getId());
        }
        return container;
    }
}
