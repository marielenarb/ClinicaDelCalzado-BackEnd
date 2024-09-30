package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.request.OperatorDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateOperatorDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.OperatorDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.OperatorListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.entity.Operator;

import java.util.Optional;

public interface IOperatorService {

    OperatorDTOResponse create(OperatorDTORequest operatorDTORequest);

    OperatorDTOResponse update(Long operatorId, UpdateOperatorDTORequest operatorDTORequest);

    OperatorListDTOResponse findOperatorAll();

    OperatorDTOResponse findOperatorByIdOp(Long operatorId);

    Optional<Operator> findOperatorById(Long operatorId);

    Operator validateOperatorIdExists(Long id);
}
