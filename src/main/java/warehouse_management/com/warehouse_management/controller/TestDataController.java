package warehouse_management.com.warehouse_management.controller;

import com.github.javafaker.Faker;
import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.ContainerRepository;
import warehouse_management.com.warehouse_management.repository.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dev")
public class TestDataController {
    @Autowired
    private MongoTemplate mongoTemplate;
    private final WarehouseRepository warehouseRepository;
    private final ContainerRepository containerRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final Faker faker = new Faker(new Locale("vi"));

    public TestDataController(WarehouseRepository warehouseRepository, ContainerRepository containerRepository, InventoryItemRepository inventoryItemRepository) {
        this.warehouseRepository = warehouseRepository;
        this.containerRepository = containerRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    private record InitDataRequest(boolean isDrop) {
    }

    @PostMapping("/init-data")
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

        for (int i = 1; i <= 5; i++) {
            String baseCode = String.format("%03d", i);
            String baseName = "Kho " + faker.address().cityName();
            String address = faker.address().fullAddress();
            String note = faker.book().title();

            // Chọn loại kho ngẫu nhiên
            WarehouseType type = i % 2 == 0 ? WarehouseType.DESTINATION : WarehouseType.DEPARTURE;

            // Tạo kho con -XP
            warehouses.add(Warehouse.builder()
                    .name(baseName + " – Xe & Phụ kiện")
                    .code(baseCode + "-XP")
                    .type(type.getId())
                    .status(ActiveStatus.ACTIVE.getId())
                    .address(address)
                    .note(note)
                    .build());

            // Tạo kho con -PT
            warehouses.add(Warehouse.builder()
                    .name(baseName + " – Phụ tùng")
                    .code(baseCode + "-PT")
                    .type(type.getId())
                    .status(ActiveStatus.ACTIVE.getId())
                    .address(address)
                    .note(note)
                    .build());
        }

        warehouseRepository.saveAll(warehouses);


        // Lọc kho theo loại
        List<Warehouse> departureWarehouses = warehouses.stream()
                .filter(w -> WarehouseType.DEPARTURE.equals(w.getType()))
                .collect(Collectors.toList());



        List<Warehouse> destinationWarehouses = warehouses.stream()
                .filter(w -> WarehouseType.DESTINATION.equals(w.getType()))
                .collect(Collectors.toList());

// Kiểm tra đủ kho để tạo container không
        if (departureWarehouses.isEmpty() || destinationWarehouses.isEmpty()) {
            throw new IllegalStateException("Không đủ warehouse loại DEPARTURE hoặc DESTINATION để tạo Container.");
        }

        List<Container> containers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Container c = new Container();
            c.setId(new ObjectId());

            // FORMAT: CONT-2024-002
            String code = String.format("CONT-%d-%03d", Year.now().getValue(), i + 1);
            c.setContainerCode(code);

            c.setContainerStatus(ContainerStatus.LOADING);
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
            Warehouse from = departureWarehouses.get(faker.random().nextInt(departureWarehouses.size()));
            Warehouse to = destinationWarehouses.get(faker.random().nextInt(destinationWarehouses.size()));
            c.setFromWareHouseId(from.getId());
            c.setToWarehouseId(to.getId());

            containers.add(c);
        }

        containerRepository.saveAll(containers);

        // 3. Tạo InventoryItems với warehouse và container
        List<InventoryItem> items = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 20; i++) {
            InventoryItem item = new InventoryItem();
            item.setId(new ObjectId());
            String poNumber = String.format("PO-%s-%03d", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), i + 1);
            String productCode = String.format("PRC-%d-%03d", currentYear, i + 1);
            item.setPoNumber(poNumber);
            item.setProductCode(productCode);
            item.setInventoryType(InventoryType.PRODUCT_ACCESSORIES.getId());
            item.setQuantity(faker.number().numberBetween(i + 1, (i + 1) * 10));
            item.setSerialNumber("SERIAL_" + faker.number().digits(6));
            item.setModel("Model " + faker.letterify("M-???"));
            item.setType("Xe nâng điện");
            item.setCategory("Ngồi lái");
            item.setManufacturingYear(2020 + faker.random().nextInt(5));
            item.setStatus(InventoryItemStatus.IN_STOCK);

            // Gán warehouse/container ngẫu nhiên
            item.setWarehouseId(warehouses.get(faker.random().nextInt(warehouses.size())).getId());
            item.setContainerId(containers.get(faker.random().nextInt(containers.size())).getId());

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
            item.setIsDeleted(false);
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
        inventoryItemRepository.saveAll(items);

        return ResponseEntity.ok("✅ Đã tạo fake data: " +
                warehouses.size() + " warehouses, " +
                containers.size() + " containers, " +
                items.size() + " inventory items.");
    }


}
