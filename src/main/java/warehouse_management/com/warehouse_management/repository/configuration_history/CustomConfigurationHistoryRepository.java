package warehouse_management.com.warehouse_management.repository.configuration_history;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.VehicleConfigurationPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;

import java.util.Collection;
import java.util.List;


public interface CustomConfigurationHistoryRepository {
    List<ConfigurationHistory> bulkInsert(Collection<ConfigurationHistory> configurationHistories);

    Page<ConfigurationHistory> findPageCH(PageOptionsDto optionsReq);

    Page<ConfigurationHistory> findPageCHCurrent(PageOptionsDto optionsReq);

    Page<VehicleConfigurationPageDto> findPageVehicleConfigurationPage(PageOptionsDto optionsReq);

    void updatePerformed(String code, String performedBy);

    void updatePerformed(ObjectId id, String performedBy);
}