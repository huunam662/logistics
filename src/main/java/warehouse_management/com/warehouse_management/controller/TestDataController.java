package warehouse_management.com.warehouse_management.controller;

import com.github.javafaker.Faker;
import com.mongodb.lang.Nullable;
import io.swagger.v3.oas.annotations.Hidden;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.container.ContainerRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;
import warehouse_management.com.warehouse_management.service.InventoryItemService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Hidden
@RestController
@RequestMapping("/dev")
public class TestDataController {
    @Autowired
    private MongoTemplate mongoTemplate;
    private final WarehouseRepository warehouseRepository;
    private final ContainerRepository containerRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemService inventoryItemService;
    private final Faker faker = new Faker(new Locale("vi"));

    public TestDataController(WarehouseRepository warehouseRepository, ContainerRepository containerRepository, InventoryItemRepository inventoryItemRepository, InventoryItemService inventoryItemService) {
        this.warehouseRepository = warehouseRepository;
        this.containerRepository = containerRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemService = inventoryItemService;
    }

    private record InitDataRequest(boolean isDrop) {
    }

    @GetMapping("/test-audit")
    private ResponseEntity<?> testAudit(@RequestParam("val") Integer value ) {
        inventoryItemService.approve(value);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/init-data")
    public ResponseEntity<String> createFakeData(@Nullable @RequestBody InitDataRequest request) {
        // Nếu isDrop == true thì xóa dữ liệu
        if (request != null && request.isDrop) {
            mongoTemplate.dropCollection("warehouse");
            mongoTemplate.dropCollection("container");
            mongoTemplate.dropCollection("inventory_item");
            System.out.println("🗑 Đã xóa toàn bộ dữ liệu trong 3 collections.");
        }
// 1. Check collection 'warehouse'
        if (!mongoTemplate.collectionExists("warehouse")) {
            mongoTemplate.createCollection("warehouse");
            System.out.println("✅ Collection 'warehouse' đã được tạo.");
        } else {
            System.out.println("ℹ️ Collection 'warehouse' đã tồn tại.");
        }

// 2. Check collection 'container'
        if (!mongoTemplate.collectionExists("container")) {
            mongoTemplate.createCollection("container");
            System.out.println("✅ Collection 'container' đã được tạo.");
        } else {
            System.out.println("ℹ️ Collection 'container' đã tồn tại.");
        }

// 3. Check collection 'inventoryItem'
        if (!mongoTemplate.collectionExists("inventory_item")) {
            mongoTemplate.createCollection("inventory_item");
            System.out.println("✅ Collection 'inventoryItem' đã được tạo.");
        } else {
            System.out.println("ℹ️ Collection 'inventoryItem' đã tồn tại.");
        }
        List<Warehouse> warehouses = new ArrayList<>();
// tiếng Việt nếu muốn

        for (int i = 1; i <= 30; i++) {
            String baseCode = String.format("%03d", i);
            String baseName = "Kho " + faker.address().cityName();
            String address = faker.address().fullAddress();
            String note = faker.book().title();

            // Chọn loại kho ngẫu nhiên
            WarehouseType type;
            if(i < 8){
                type = WarehouseType.PRODUCTION;
            }
            else if(i < 15){
                type = WarehouseType.DEPARTURE;
            }
            else if(i < 23){
                type = WarehouseType.DESTINATION;
            }
            else{
                type = WarehouseType.CONSIGNMENT;
            }
            // Tạo kho con -XP
            warehouses.add(Warehouse.builder()
                    .name(baseName)
                    .code(baseCode)
                    .type(type.getId())
                    .status(ActiveStatus.ACTIVE.getId())
                    .address(address)
                    .note(note)
                    .build());

        }

        warehouseRepository.saveAll(warehouses);

        List<Warehouse> warehouseDestinations = warehouses.stream().filter(w -> w.getType().equals(WarehouseType.DESTINATION)).toList();

        // 3. Tạo InventoryItems với warehouse và container
        List<Container> containers = new ArrayList<>();
        List<InventoryItem> items = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for(var warehouse : warehouses){
            if(warehouse.getType().equals(WarehouseType.DEPARTURE)){
                for (int i = 0; i < 5; i++) {
                    Container c = new Container();
                    c.setId(new ObjectId());
                    // FORMAT: CONT-2024-002
                    String code = String.format("CONT-%d-%d", Year.now().getValue(), faker.random().nextInt(99999));
                    c.setContainerCode(code);

                    c.setContainerStatus(ContainerStatus.PENDING);
                    c.setDepartureDate(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 10)));
                    c.setArrivalDate(LocalDateTime.now().plusDays(faker.number().numberBetween(1, 10)));

                    // Ghi chú sát nghĩa hơn
                    String[] notes = {
                            "Đang vận chuyển đến kho đích",
                            "Chờ chất hàng tại kho xuất",
                            "Đã khởi hành, đang trên đường",
                            "Chưa hoàn thành kiểm kê",
                            "Hàng đã tới kho, chờ nhập kho"
                    };
                    c.setNote(notes[faker.random().nextInt(notes.length)]);

                    // Gán from/to warehouse theo đúng loại
                    Warehouse to = warehouseDestinations.get(faker.random().nextInt(warehouseDestinations.size()));
                    c.setFromWareHouseId(warehouse.getId());
                    c.setToWarehouseId(to.getId());
                    containers.add(c);
                }
            }

            String[] descriptionSpareParts = {
                    "Ắc quy xe nâng điện – Nguồn cung cấp năng lượng chính cho toàn bộ hệ thống vận hành",
                    "Bộ sạc ắc quy – Thiết bị nạp điện cho pin xe nâng, đảm bảo hiệu suất hoạt động",
                    "Động cơ điện – Tạo lực di chuyển và nâng hạ cho xe",
                    "Bánh xe PU/Nylon – Bánh chuyên dụng chịu tải nặng, giảm ma sát và êm khi vận hành",
                    "Bộ điều khiển (Controller) – Quản lý dòng điện, tốc độ và hướng di chuyển",
                    "Bơm thủy lực – Tạo áp lực để nâng hạ càng nâng và hàng hóa",
                    "Càng nâng (Fork) – Bộ phận trực tiếp nâng đỡ và di chuyển pallet hàng",
                    "Xích nâng (Lift Chain) – Truyền động giữa xi lanh và khung nâng để nâng hạ",
                    "Ghế ngồi & dây an toàn – Đảm bảo sự thoải mái và an toàn cho người vận hành",
                    "Bộ đèn tín hiệu & còi – Hỗ trợ cảnh báo và an toàn trong quá trình vận hành"
            };

            String poNumber = "";
            String productCode = "";
            for (int j = 0; j < 40; j++) {
                InventoryItem item = new InventoryItem();
                item.setId(new ObjectId());
                if(j % 4 == 0){
                    poNumber = String.format("PO-%s-%d", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), faker.random().nextInt(9999));
                    productCode = String.format("PRC-%d-%d", currentYear, faker.random().nextInt(999999));
                    item.setQuantity(1);
                    item.setPoNumber(poNumber);
                    item.setProductCode(productCode);
                    item.setInventoryType(InventoryType.VEHICLE.getId());
                    item.setSerialNumber("SERIAL_" + faker.number().digits(6));
                    item.setCategory(faker.random().nextInt(5) % 2 == 0 ? "Ngồi lái" : "Đứng lái");
                    item.setManufacturingYear(2020 + faker.random().nextInt(5));
                }
                else{
                    item.setQuantity(faker.number().numberBetween(j + 1, (j + 1) * 10));
                    item.setPoNumber(poNumber);
                    item.setCommodityCode(String.format("CDC-%d-%d", currentYear, faker.random().nextInt(99999999)));
                    item.setDescription(descriptionSpareParts[faker.random().nextInt(descriptionSpareParts.length)]);
                    item.setInventoryType(InventoryType.SPARE_PART.getId());
                }
                item.setModel("Model " + faker.letterify("M-???"));

                item.setStatus(InventoryItemStatus.IN_STOCK);

                // Gán warehouse/container ngẫu nhiên
                item.setWarehouseId(warehouse.getId());

                item.setInitialCondition("Mới");
                String[] ghiChuMau = {
                        "Hàng mới về, chưa kiểm tra",
                        "Cần kiểm tra thông số bình",
                        "Chờ kiểm định chất lượng",
                        "Hàng đã bán, chờ xuất kho",
                        "Đã qua sử dụng, cần bảo dưỡng",
                        "Sản phẩm lỗi nhẹ, cần sửa chữa",
                        "Thiết bị còn nguyên đai kiện",
                        "Bảo quản trong kho mát",
                        "Sắp hết hạn bảo hành",
                        "Phụ tùng đi kèm đầy đủ"
                };

                item.setNotes(ghiChuMau[faker.random().nextInt(ghiChuMau.length)]);
                item.setCreatedAt(LocalDateTime.now());
                item.setUpdatedAt(LocalDateTime.now());

                items.add(item);
                // Specifications
                InventoryItem.Specifications specs = new InventoryItem.Specifications(
                        faker.number().numberBetween(1000, 5000), // liftingCapacityKg
                        "Khung " + faker.letterify("??"),         // chassisType
                        faker.number().numberBetween(2000, 7000), // liftingHeightMm
                        "Động cơ " + faker.letterify("??"),       // engineType
                        "Bình điện " + faker.bothify("??-###"),   // batteryInfo
                        "Thông số bình " + faker.bothify("??-##"),// batterySpecification
                        "Thông số sạc " + faker.bothify("??-##"), // chargerSpecification
                        "",
                        faker.number().numberBetween(2, 6),       // valveCount
                        faker.bool().bool(),                      // hasSideShift
                        faker.lorem().sentence()                  // otherDetails
                );
                item.setSpecifications(specs);

// Pricing
                InventoryItem.Pricing pricing = new InventoryItem.Pricing(
                        BigDecimal.valueOf(faker.number().randomDouble(2, 100, 500)),  // purchasePrice
                        BigDecimal.valueOf(faker.number().randomDouble(2, 550, 650)),  // salePriceR0
                        BigDecimal.valueOf(faker.number().randomDouble(2, 700, 800)),  // salePriceR1
                        BigDecimal.valueOf(faker.number().randomDouble(2, 600, 900)),  // actualSalePrice
                        "Đại lý " + faker.company().name()                             // agent
                );
                item.setPricing(pricing);

// Logistics
                InventoryItem.Logistics logistics = new InventoryItem.Logistics(
                        LocalDateTime.now().minusDays(faker.number().numberBetween(30, 60)), // orderDate
                        LocalDateTime.now().minusDays(faker.number().numberBetween(15, 29)), // departureDate
                        LocalDateTime.now().minusDays(faker.number().numberBetween(1, 14)),  // arrivalDate
                        faker.bool().bool() ? LocalDateTime.now().minusDays(faker.number().numberBetween(1, 10)) : null, // consignmentDate,
                        LocalDateTime.now().minusDays(faker.number().numberBetween(25, 50)), // plannedProductionDate
                        LocalDateTime.now().minusDays(faker.number().numberBetween(16, 28)) // estimateCompletionDate
                );
                item.setLogistics(logistics);
            }


        }

        containerRepository.saveAll(containers);
        inventoryItemRepository.saveAll(items);

        return ResponseEntity.ok("✅ Đã tạo fake data: " +
                warehouses.size() + " warehouses, " +
                containers.size() + " containers, " +
                items.size() + " inventory items.");
    }


}
