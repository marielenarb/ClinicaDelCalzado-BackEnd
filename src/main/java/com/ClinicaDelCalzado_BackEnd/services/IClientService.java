package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ClientDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ClientListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.entity.Client;

import java.util.Optional;

public interface IClientService {
    Optional<Client> findClientByIdClient(Long idClient);
    Client saveClient(Client client);
    ClientDTOResponse findClientByClientId(Long idClient);
    ClientListDTOResponse findClientAll();
    Boolean validateDifferenceData(Client currentClient, Client client);
    Client findClientWorkOrder(WorkOrderDTORequest workOrderDTORequest);
}

