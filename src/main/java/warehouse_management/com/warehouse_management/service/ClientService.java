package warehouse_management.com.warehouse_management.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.client.request.CreateClientDto;
import warehouse_management.com.warehouse_management.dto.client.request.UpdateClientDto;
import warehouse_management.com.warehouse_management.dto.client.response.ClientDto;
import warehouse_management.com.warehouse_management.dto.client.response.ClientIdDto;
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

    public List<ClientDto> getAllClients(String name, String email) {
        return clientRepository.findAllActiveClientRes(name, email);
    }

    public ClientIdDto createClient(CreateClientDto client) {
        Client saved = clientRepository.save(clientMapper.toClient(client));
        return buildCreateClientRes(saved.getId().toString());
    }

    public ClientDto getClientById(ObjectId id) {
        return clientRepository.findActiveClientResById(id).orElseThrow(() -> LogicErrException.of("Client không tồn tại hoặc đã bị xóa"));
    }

    public Client getClientToId(ObjectId id) {
        return clientRepository.findById(id).orElseThrow(() -> LogicErrException.of("Client không tồn tại hoặc đã bị xóa"));
    }

    public ClientIdDto updateClient(ObjectId id, UpdateClientDto updated) {
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

    private ClientIdDto buildCreateClientRes(String id) {
        ClientIdDto createClientRes = new ClientIdDto();
        createClientRes.setId(id);
        return createClientRes;
    }

}
