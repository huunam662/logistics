package warehouse_management.com.warehouse_management.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.client.request.CreateClientReq;
import warehouse_management.com.warehouse_management.dto.client.request.UpdateClientReq;
import warehouse_management.com.warehouse_management.dto.client.response.ClientRes;
import warehouse_management.com.warehouse_management.dto.client.response.CreateClientRes;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.ClientMapper;
import warehouse_management.com.warehouse_management.model.Client;
import warehouse_management.com.warehouse_management.repository.ClientRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public List<ClientRes> getAllClients() {
        return clientRepository.findAllActiveClientRes();
    }

    public CreateClientRes createClient(CreateClientReq client) {
        Client saved = clientRepository.save(clientMapper.toClient(client));
        return buildCreateClientRes(saved.getId().toString());
    }

    public ClientRes getClientById(ObjectId id) {
        return clientRepository.findActiveClientResById(id).orElseThrow(() -> LogicErrException.of("Client không tồn tại hoặc đã bị xóa"));

    }

    public CreateClientRes updateClient(ObjectId id, UpdateClientReq updated) {
        Client existing = clientRepository.findActiveClientById(id).orElseThrow(() -> LogicErrException.of("Client không tồn tại hoặc đã bị xóa"));

        clientMapper.updateClientFromDto(updated, existing);
        Client saved = clientRepository.save(existing);
        return buildCreateClientRes(saved.getId().toString());
    }

    public boolean softDeleteClient(ObjectId id) {
        Client existing = clientRepository.findActiveClientById(id).orElseThrow(() -> LogicErrException.of("Client không tồn tại hoặc đã bị xóa"));
        existing.setDeletedAt(LocalDateTime.now());
        clientRepository.save(existing);
        return true;
    }

    private CreateClientRes buildCreateClientRes(String id) {
        CreateClientRes createClientRes = new CreateClientRes();
        createClientRes.setId(id);
        return createClientRes;
    }

}
