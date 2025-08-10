package warehouse_management.com.warehouse_management.service;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.dto.container.request.CreateContainerDto;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.repository.container.ContainerRepository;
import warehouse_management.com.warehouse_management.utils.MongoRsqlUtils;

import java.util.List;

@Service
public class ContainerService {

    private final ContainerRepository containerRepository;

    public ContainerService(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }

    public Page<ContainerResponseDto> getContainers(PageOptionsReq req) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deletedAt").is(null));

        return MongoRsqlUtils.queryPage(
                Container.class,
                ContainerResponseDto.class,
                query,
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
}
