package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ClientDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ClientListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.userClient.ClientDTO;
import com.ClinicaDelCalzado_BackEnd.entity.Client;
import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import com.ClinicaDelCalzado_BackEnd.repository.workOrders.IClientRepository;
import com.ClinicaDelCalzado_BackEnd.services.IClientService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Optional<Client> findClientByIdClient(Long idClient) {
        return clientRepository.findByIdClient(idClient);
    }

    @Override
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public ClientDTOResponse findClientByClientId(Long idClient) {
        validateIdentification(idClient);

        ClientDTOResponse clientDTOResponse = new ClientDTOResponse();
        Optional<Client> client = findClientByIdClient(idClient);

        if (client.isEmpty()) {
            throw new NotFoundException(String.format("La identificación %s del administrador no existe", idClient));
        }

        clientDTOResponse.setMessage("Detalles del cliente recuperados exitosamente.");
        clientDTOResponse.setClient(client.map(p ->
                new ClientDTO(p.getIdClient(), p.getClientName(), p.getCliPhoneNumber())).get());

        return clientDTOResponse;

    }

    @Override
    public ClientListDTOResponse findClientAll() {

        List<Client> clientList = clientRepository.findAll();

        ClientListDTOResponse clientListDTOResponse = new ClientListDTOResponse();
        clientListDTOResponse.setClients(clientList.stream().map(p ->
                new ClientDTO(p.getIdClient(), p.getClientName(), p.getCliPhoneNumber())).toList());

        return clientListDTOResponse;
    }

    @Override
    public Boolean validateDifferenceData(Client currentClient, Client client){
        return currentClient.getClientName().equals(client.getClientName()) &&
                currentClient.getCliPhoneNumber().equals(client.getCliPhoneNumber());
    }

    @Override
    public Client findClientWorkOrder(WorkOrderDTORequest workOrderDTORequest) {

        Optional<Client> clientByIdClient = findClientByIdClient(workOrderDTORequest.getClient().getIdentification());
        Client client = Client.builder()
                .idClient(workOrderDTORequest.getClient().getIdentification())
                .clientName(workOrderDTORequest.getClient().getName())
                .cliPhoneNumber(workOrderDTORequest.getClient().getCellphone())
                .build();

        if (clientByIdClient.isEmpty() || !validateDifferenceData(clientByIdClient.get(), client)) {
            return saveClient(client);
        }

        return clientByIdClient.get();
    }

    private void validateIdentification(Long idClient) {
        if (ObjectUtils.isEmpty(idClient)) {
            throw new BadRequestException("La identificación es un campo obligatorio, no puede ser vacio");
        }
    }
}
