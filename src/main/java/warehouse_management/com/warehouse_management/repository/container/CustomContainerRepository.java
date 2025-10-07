package warehouse_management.com.warehouse_management.repository.container;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;

import java.util.List;

public interface CustomContainerRepository {
    boolean softDeleteById(ObjectId containerId, ObjectId deletedBy);
    long bulkSoftDelete(List<ObjectId> containerIds, ObjectId deletedBy);
    Page<ContainerResponseDto> getPageContainers(PageOptionsDto req);
}