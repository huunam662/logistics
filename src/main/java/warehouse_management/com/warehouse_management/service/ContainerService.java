package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerDetailsProductDto;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerDetailsSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryItemToContainerDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryProductDetailsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventorySparePartDetailsDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.container.request.CreateContainerDto;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.mapper.WarehouseTransactionMapper;
import warehouse_management.com.warehouse_management.model.*;
import warehouse_management.com.warehouse_management.repository.container.ContainerRepository;
import warehouse_management.com.warehouse_management.repository.delivery_order.DeliveryOrderRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse_transaction.WarehouseTransactionRepository;
import warehouse_management.com.warehouse_management.utils.GeneralResource;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContainerService {
    private final ContainerRepository containerRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemService inventoryItemService;
    private final InventoryItemMapper inventoryItemMapper;
    private final DeliveryOrderRepository deliveryOrderRepository;

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
                        ArrayOperators.Reduce.arrayOf("$inventoryItems")
                                .withInitialValue(0)
                                .reduce(
                                        ArithmeticOperators.Add.valueOf("$$value")
                                                .add(
                                                        ArithmeticOperators.Multiply.valueOf("$$this.pricing.purchasePrice")
                                                                .multiplyBy("$$this.quantity")
                                                )

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

    public Map<String, Boolean> checkIsExistsContainerCode(ObjectId containerId, String containerCode){
        boolean exists = containerRepository.existsByContainerCode(containerCode, containerId);
        return Map.of("exists", exists);
    }

    @Transactional
    public Container createContainer(CreateContainerDto createDto) {
        if (containerRepository.existsByContainerCode(createDto.getContainerCode())) {
            throw new DuplicateKeyException("Mã '" + createDto.getContainerCode() + "' đã tồn tại.");
        }

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
        container.setContainerStatus(ContainerStatus.PENDING);

        return containerRepository.save(container);
    }

    @Transactional
    public Container updateContainer(ObjectId containerId, CreateContainerDto createDto){
        Container container = getContainerToId(containerId);
        if (containerRepository.existsByContainerCode(createDto.getContainerCode(), container.getId())) {
            throw new DuplicateKeyException("Mã '" + createDto.getContainerCode() + "' đã tồn tại.");
        }
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

        List<Container> containers = containerRepository.findAllInIds(containerIds);
        for(Container c : containers){
            if(!c.getContainerStatus().equals(ContainerStatus.PENDING))
                throw LogicErrException.of("CONT " + c.getContainerCode() + "chỉ được phép xóa khi đang chờ xác nhận");
            containerCompletedAndRejectedLogic(c, c.getFromWareHouseId());
        }

        long deletedCount = containerRepository.bulkSoftDelete(containerIds, currentUserId);

        return deletedCount > 0;
    }

    @Transactional
    public boolean softDeleteContainers(String containerId){
        if (containerId == null || containerId.isBlank()) {
            return false;
        }
        Container container = getContainerToId(new ObjectId(containerId));
        if(!container.getContainerStatus().equals(ContainerStatus.PENDING))
            throw LogicErrException.of("Chỉ được phép xóa khi đang chờ xác nhận");
        if(container.getInventoryItems() != null){
            containerCompletedAndRejectedLogic(container, container.getFromWareHouseId());
            List<ObjectId> itemIds = container.getInventoryItems().stream().map(Container.InventoryItemContainer::getId).toList();
            inventoryItemRepository.updateStatusAndWarehouseAndUnRefContainer(itemIds, container.getFromWareHouseId(), InventoryItemStatus.IN_STOCK.getId());
        }
        containerRepository.softDeleteById(new ObjectId(containerId), null);
        return true;
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
        if(!container.getContainerStatus().equals(ContainerStatus.PENDING))
            throw LogicErrException.of("Chỉ được phép thêm hàng hóa khi đang chờ xác nhận.");
        try{
            List<InventoryItem> itemsPushToCont = inventoryItemService.transferItems(req.getInventoryItems(), container.getFromWareHouseId(), container, container.getArrivalDate(), null, InventoryItemStatus.IN_TRANSIT);
            container.setContainerStatus(ContainerStatus.PENDING);
            if(container.getInventoryItems() == null) container.setInventoryItems(new ArrayList<>());
            final int itemsContSize = container.getInventoryItems().size();
            for(var itemPush : itemsPushToCont){
                if(itemPush.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                    boolean isExistsInCont = false;
                    for(int i = 0; i < itemsContSize; i++){
                        Container.InventoryItemContainer itemInCont = container.getInventoryItems().get(i);
                        if(!itemInCont.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                            continue;
                        if(itemInCont.getCommodityCode().equals(itemPush.getCommodityCode())){
                            itemInCont.setQuantity(itemPush.getQuantity());
                            isExistsInCont = true;
                            break;
                        }
                    }
                    if(isExistsInCont) continue;
                }
                container.getInventoryItems().add(inventoryItemMapper.toInventoryItemContainer(itemPush));
            }
            containerRepository.save(container);
//            Warehouse wh1 = GeneralResource.getWarehouseById(mongoTemplate, container.getFromWareHouseId());
//            Warehouse wh2 = GeneralResource.getWarehouseById(mongoTemplate, container.getToWarehouseId());
//            warehouseTransferTicketRepository.save(buildDepToDesTran(wh1, wh2, container, itemsPushToCont));
            // TODO: Ghi nhận log giao dịch

            return Map.of("containerId", container.getId());
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Thêm hàng vào container thất bại.");
        }
    }
    
    public ContainerDetailsProductDto getInventoryItemsProductToContainerId(String containerId){
        Container container = getContainerToId(new ObjectId(containerId));
        if (container.getInventoryItems() == null) container.setInventoryItems(List.of());
        List<InventoryProductDetailsDto> dtos = new ArrayList<>();
        List<String> vehicleAccessory = List.of(InventoryType.VEHICLE.getId(), InventoryType.ACCESSORY.getId());
        BigDecimal totalAmounts = BigDecimal.ZERO;
        for(var item : container.getInventoryItems()){
            if(vehicleAccessory.contains(item.getInventoryType())){
                totalAmounts = totalAmounts.add(item.getPricing().getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                dtos.add(inventoryItemMapper.toInventoryProductDetailsDto(item));
            }
        }
        ContainerDetailsProductDto dto = new ContainerDetailsProductDto();
        dto.setTotalAmounts(totalAmounts);
        dto.setInventoryItemsProduct(dtos);
        return dto;
    }

    public ContainerDetailsSparePartDto getInventoryItemsSparePartToContainerId(String containerId){
        Container container = getContainerToId(new ObjectId(containerId));
        if (container.getInventoryItems() == null) container.setInventoryItems(List.of());
        List<InventorySparePartDetailsDto> dtos = new ArrayList<>();
        BigDecimal totalAmounts = BigDecimal.ZERO;
        for(var item : container.getInventoryItems()){
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                totalAmounts = totalAmounts.add(item.getPricing().getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                dtos.add(inventoryItemMapper.toInventorySparePartDetailsDto(item));
            }
        }
        ContainerDetailsSparePartDto dto = new ContainerDetailsSparePartDto();
        dto.setTotalAmounts(totalAmounts);
        dto.setInventoryItemsSparePart(dtos);
        return dto;
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
            if(container.getInventoryItems() == null || container.getInventoryItems().isEmpty())
                throw LogicErrException.of("Chỉ được hoàn tất khi trong CONT " +container.getContainerCode()+" có hàng.");
            containerCompletedAndRejectedLogic(container, container.getToWarehouseId());
            List<ObjectId> itemIds = container.getInventoryItems().stream().map(Container.InventoryItemContainer::getId).toList();
            inventoryItemRepository.updateStatusAndWarehouseAndUnRefContainer(itemIds, container.getToWarehouseId(), InventoryItemStatus.IN_STOCK.getId());
            container.setCompletionDate(LocalDateTime.now());
            updateDeliveryItemsRefDepartureWarehouse(container);
            // TODO: Phiếu xuất/nhập
        }
        container.setContainerStatus(containerStatus);
        containerRepository.save(container);
        return container;
    }

    @Transactional
    public void containerCompletedAndRejectedLogic(Container container, ObjectId warehouseId){
        if(container.getInventoryItems() == null) return;
        // Update items nếu là trạng thái hoàn tất giao hàng
        // Nếu ở kho được chỉ định đã tồn tại phụ tùng với trạng thái đang IN_STOCK thì cập nhập số lượng
        Map<String, Container.InventoryItemContainer> inventoryContainerSparePartMap = container.getInventoryItems().stream()
                .filter(item -> item.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && item.getCommodityCode() != null)
                .collect(Collectors.toMap(Container.InventoryItemContainer::getCommodityCode, item -> item));
        // Lấy ra các phụ tùng với mã sản phẩm đã tồn tại ở kho được chỉ định và trạng thái đang IN_STOCK
        List<InventoryItem> sparePartsInStockDestination = inventoryItemRepository.findSparePartByCommodityCodeIn(inventoryContainerSparePartMap.keySet(), warehouseId, InventoryItemStatus.IN_STOCK.getId());
        // Danh sách lưu trữ các spare part cần xóa mềm thuộc container
        if(!sparePartsInStockDestination.isEmpty()){
            List<ObjectId> sparePartsToDel = new ArrayList<>();
            // Cập nhật lại số lượng phụ tùng có sẵn trong kho được chỉ định (trùng mã hàng hóa)
            for(var sparePart : sparePartsInStockDestination){
                Container.InventoryItemContainer sparePartInContainer = inventoryContainerSparePartMap.get(sparePart.getCommodityCode());
                sparePart.setQuantity(sparePart.getQuantity() + sparePartInContainer.getQuantity());
                sparePartsToDel.add(sparePartInContainer.getId());
            }
            inventoryItemRepository.bulkUpdateTransfer(sparePartsInStockDestination);
            // Xóa cứng các phụ tùng được clone trước đó ở kho nguồn (do trước đó chỉ lấy ra số lượng bé hơn số lượng tồn kho)
            inventoryItemRepository.bulkHardDelete(sparePartsToDel);
        }
    }

    @Transactional
    protected void updateDeliveryItemsRefDepartureWarehouse(Container container){
        List<DeliveryOrder> deliveryOrdersUpdate = new ArrayList<>();
        List<Container.InventoryItemContainer> products = container.getInventoryItems()
                .stream()
                .filter(o -> !o.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .toList();
        for(var p : products){
            DeliveryOrder deliveryOrder = deliveryOrderRepository.findByProductCode(p.getProductCode(), container.getFromWareHouseId());
            if(deliveryOrder == null) continue;
            DeliveryOrder.InventoryItemDelivery productDelivery = deliveryOrder.getInventoryItems().stream()
                    .filter(o -> !o.getInventoryType().equals(InventoryType.SPARE_PART.getId())
                            && o.getProductCode().equals(p.getProductCode())
                            && o.getWarehouseId().equals(container.getFromWareHouseId()))
                    .findFirst()
                    .orElse(null);
            if(productDelivery == null) continue;
            InventoryItem productInHold = inventoryItemRepository.findById(productDelivery.getId()).orElse(null);
            if(productInHold == null) throw LogicErrException.of("Sản phẩm " + p.getProductCode() + " không tồn tại trong kho đến.");
            productDelivery.setWarehouseId(container.getToWarehouseId());
            deliveryOrdersUpdate.add(deliveryOrder);
        }
        List<Container.InventoryItemContainer> spareParts = container.getInventoryItems()
                .stream()
                .filter(o -> o.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .toList();
        for(var p : spareParts){
            InventoryItem sparePartHolding = inventoryItemRepository.findByCommodityCodeAndWarehouseId(p.getCommodityCode(), container.getToWarehouseId(), InventoryItemStatus.HOLD.getId()).orElse(null);
            if(sparePartHolding == null) continue;
            int sparePartQuantity = sparePartHolding.getQuantity();
            List<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findByCommodityCode(p.getCommodityCode(), container.getFromWareHouseId())
                    .stream().sorted(Comparator.comparing(DeliveryOrder::getDeliveryDate, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
            for(var d : deliveryOrders){
                DeliveryOrder.InventoryItemDelivery sparePartDelivery = d.getInventoryItems().stream()
                        .filter(o -> o.getInventoryType().equals(InventoryType.SPARE_PART.getId())
                                && o.getCommodityCode().equals(p.getCommodityCode())
                                && o.getWarehouseId().equals(container.getFromWareHouseId()))
                        .findFirst()
                        .orElse(null);
                if(sparePartDelivery == null || sparePartQuantity < sparePartDelivery.getQuantity())
                    continue;
                sparePartQuantity -= sparePartDelivery.getQuantity();
                sparePartDelivery.setWarehouseId(container.getToWarehouseId());
                deliveryOrdersUpdate.add(d);
            }
        }
        if(!deliveryOrdersUpdate.isEmpty()) deliveryOrderRepository.saveAll(deliveryOrdersUpdate);
    }

}
