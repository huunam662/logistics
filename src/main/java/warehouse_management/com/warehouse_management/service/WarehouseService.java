package warehouse_management.com.warehouse_management.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.mapper.warehouse.WarehouseMapper;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.WarehouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;

    public WarehouseService(WarehouseRepository repository, WarehouseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public WarehouseResponseDto createWarehouse(CreateWarehouseDto createDto) {
        Warehouse warehouse = mapper.toEntity(createDto);
        Warehouse savedWarehouse = repository.save(warehouse);
        return mapper.toResponseDto(savedWarehouse);
    }

    public List<WarehouseResponseDto> getAllWarehouses() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public WarehouseResponseDto getWarehouseById(String id) {
        Warehouse warehouse = findWarehouseById(id);
        return mapper.toResponseDto(warehouse);
    }

    public WarehouseResponseDto updateWarehouse(String id, UpdateWarehouseDto updateDto) {
        Warehouse existingWarehouse = findWarehouseById(id);

        mapper.updateFromDto(updateDto, existingWarehouse);

        Warehouse updatedWarehouse = repository.save(existingWarehouse);
        return mapper.toResponseDto(updatedWarehouse);
    }

    public void deleteWarehouse(String id) {

        Warehouse warehouse = findWarehouseById(id);
        repository.delete(warehouse);
    }

    private Warehouse findWarehouseById(String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}
