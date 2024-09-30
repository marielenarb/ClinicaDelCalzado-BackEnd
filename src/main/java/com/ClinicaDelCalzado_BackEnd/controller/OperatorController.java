package com.ClinicaDelCalzado_BackEnd.controller;

import com.ClinicaDelCalzado_BackEnd.dtos.request.OperatorDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateOperatorDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.OperatorDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.OperatorListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.services.IOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operator")
public class OperatorController {
    @Autowired
    private IOperatorService operatorService;

    public OperatorController(IOperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @PostMapping("/created")
    public ResponseEntity<OperatorDTOResponse> createOperator(@RequestBody OperatorDTORequest operatorDTORequest, Authentication authentication) {

        OperatorDTOResponse responseDTO = operatorService.create(operatorDTORequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/updated/{operatorId}")
    public ResponseEntity<OperatorDTOResponse> updateOperator(@PathVariable Long operatorId,
                                                              @RequestBody UpdateOperatorDTORequest operatorDTORequest, Authentication authentication) {

        OperatorDTOResponse responseDTO = operatorService.update(operatorId, operatorDTORequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<OperatorListDTOResponse> getAllOperators(Authentication authentication) {

        OperatorListDTOResponse responseDTO = operatorService.findOperatorAll();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{operatorId}")
    public ResponseEntity<OperatorDTOResponse> getOperatorById(@PathVariable Long operatorId, Authentication authentication) {

        OperatorDTOResponse responseDTO = operatorService.findOperatorByIdOp(operatorId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
