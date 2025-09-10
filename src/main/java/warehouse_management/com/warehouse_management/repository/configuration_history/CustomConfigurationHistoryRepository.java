package warehouse_management.com.warehouse_management.repository.configuration_history;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryDepartureDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface CustomConfigurationHistoryRepository  {
    List<ConfigurationHistory> bulkInsert(Collection<ConfigurationHistory> configurationHistories);

    Page<ConfigurationHistory> findPageCH(PageOptionsDto optionsReq);

    Page<ConfigurationHistory> findPageCHCurrent(PageOptionsDto optionsReq);


}