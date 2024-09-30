package com.ClinicaDelCalzado_BackEnd.controller;

import com.ClinicaDelCalzado_BackEnd.dtos.response.ClientDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ClientListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.services.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {

    @Autowired
    private IClientService clientService;

    @GetMapping("/list")
    public ResponseEntity<ClientListDTOResponse> getAllClient(Authentication authentication) {

        ClientListDTOResponse clientListDTO = clientService.findClientAll();
        return new ResponseEntity<>(clientListDTO, HttpStatus.OK);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDTOResponse> getClientById(@PathVariable Long clientId, Authentication authentication) {

        ClientDTOResponse clientResponseDTO = clientService.findClientByClientId(clientId);
        return new ResponseEntity<>(clientResponseDTO, HttpStatus.OK);
    }
}
