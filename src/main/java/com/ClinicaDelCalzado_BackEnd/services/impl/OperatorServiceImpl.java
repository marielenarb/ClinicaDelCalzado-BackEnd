package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.enums.OperatorStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.operator.OperatorDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.OperatorDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateOperatorDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.OperatorDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.OperatorListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.entity.Operator;
import com.ClinicaDelCalzado_BackEnd.exceptions.AlreadyExistsException;
import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import com.ClinicaDelCalzado_BackEnd.repository.operator.IOperatorRepository;
import com.ClinicaDelCalzado_BackEnd.services.IOperatorService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OperatorServiceImpl implements IOperatorService {

    private final IOperatorRepository operatorRepository;

    @Autowired
    public OperatorServiceImpl(IOperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Override
    public OperatorDTOResponse create(OperatorDTORequest operatorDTORequest) {
        validateInputData(operatorDTORequest, true);

        Long operatorId = operatorDTORequest.getIdOperator();

        if (findOperatorById(operatorId).isPresent()) {
            throw new AlreadyExistsException(
                    String.format("El operador con identificador %s ya existe", operatorId));
        }

        Operator operator = buildOperator(
                operatorId,
                operatorDTORequest.getOperatorName(),
                operatorDTORequest.getOpePhoneNumber(),
                OperatorStatusEnum.ACTIVE.getValue()
        );

        saveOperator(operator);

        return operatorDTOResponse("Operador creado exitosamente.", operator);
    }

    @Override
    public OperatorDTOResponse update(Long operatorId, UpdateOperatorDTORequest operatorDTO) {
        validateInputData(OperatorDTORequest.builder()
                .idOperator(operatorId)
                .build(), false);

        Operator operator = matchDifferencesOpe(validateOperatorIdExists(operatorId), operatorDTO);

        saveOperator(operator);

        return operatorDTOResponse("Operador actualizado exitosamente.", operator);
    }

    @Override
    public OperatorListDTOResponse findOperatorAll() {
        List<Operator> operatorList = operatorRepository.findAll();

        OperatorListDTOResponse operatorListDTOResponse = new OperatorListDTOResponse();
        operatorListDTOResponse.setOperators(operatorList.stream().map(p ->
                new OperatorDTO(p.getIdOperator(),
                        p.getOperatorName(),
                        p.getOpePhoneNumber(),
                        OperatorStatusEnum.getValue(p.getStatusOperator())
                )).toList());

        return operatorListDTOResponse;
    }

    @Override
    public OperatorDTOResponse findOperatorByIdOp(Long operatorId) {
        validateIdentification(operatorId);

        OperatorDTOResponse operatorDTOResponse = new OperatorDTOResponse();
        Optional<Operator> operator = findOperatorById(operatorId);

        if (operator.isEmpty()) {
            throw new NotFoundException(String.format("La identificación %s del operador no existe", operatorId));
        }

        operatorDTOResponse.setMessage("Detalles del operador recuperados exitosamente.");
        operatorDTOResponse.setOperator(operator.map(p ->
                new OperatorDTO(
                        p.getIdOperator(),
                        p.getOperatorName(),
                        p.getOpePhoneNumber(),
                        OperatorStatusEnum.getValue(p.getStatusOperator())
                )).get());

        return operatorDTOResponse;
    }

    @Override
    public Optional<Operator> findOperatorById(Long operatorId) {
        return operatorRepository.findById(operatorId);
    }

    private void saveOperator(Operator operator) {
        operatorRepository.save(operator);
    }

    private void validateIdentification(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new BadRequestException("La identificación es un campo obligatorio, no puede ser vacio");
        }
    }

    private Operator buildOperator(Long operatorId, String operatorName, String opePhoneNumber, String operatorStatus) {
        return Operator.builder()
                .idOperator(operatorId)
                .operatorName(operatorName)
                .opePhoneNumber(opePhoneNumber)
                .statusOperator(OperatorStatusEnum.getName(operatorStatus))
                .build();
    }

    private OperatorDTOResponse operatorDTOResponse(String message, Operator operator) {
        return OperatorDTOResponse.builder()
                .message(message)
                .operator(OperatorDTO.builder()
                        .idOperator(operator.getIdOperator())
                        .operatorName(operator.getOperatorName())
                        .opePhoneNumber(operator.getOpePhoneNumber())
                        .statusOperator(OperatorStatusEnum.getValue(operator.getStatusOperator()))
                        .build())
                .build();
    }

    @Override
    public Operator validateOperatorIdExists(Long id) {
        Optional<Operator> operatorById = findOperatorById(id);

        if (operatorById.isEmpty()) {
            throw new NotFoundException(String.format("El operador con identificación %s no existe", id));
        }
        return operatorById.get();
    }

    private void validateInputData(OperatorDTORequest operatorDTO, Boolean validateAllFields) {

        validateIdentification(operatorDTO.getIdOperator());

        if (ObjectUtils.isEmpty(operatorDTO.getOperatorName()) && validateAllFields) {
            throw new BadRequestException("El nombre es un campo obligatorio, no puede ser vacío");
        }

        if (ObjectUtils.isEmpty(operatorDTO.getOpePhoneNumber()) && validateAllFields) {
            throw new BadRequestException("El número de teléfono es un campo obligatorio, no puede ser vacío");
        }
    }

    private Operator matchDifferencesOpe(Operator currentDataOpe, UpdateOperatorDTORequest newDataOpe) {
        return buildOperator(
                currentDataOpe.getIdOperator(),
                ObjectUtils.isEmpty(newDataOpe.getOperatorName()) || Objects.equals(currentDataOpe.getOperatorName(), newDataOpe.getOperatorName()) ?
                        currentDataOpe.getOperatorName() :
                        newDataOpe.getOperatorName(),
                ObjectUtils.isEmpty(newDataOpe.getOpePhoneNumber()) || Objects.equals(currentDataOpe.getOpePhoneNumber(), newDataOpe.getOpePhoneNumber()) ?
                        currentDataOpe.getOpePhoneNumber() :
                        newDataOpe.getOpePhoneNumber(),
                ObjectUtils.isEmpty(newDataOpe.getOperatorStatus()) || Objects.equals(currentDataOpe.getStatusOperator(), OperatorStatusEnum.getName(newDataOpe.getOperatorStatus())) ?
                        OperatorStatusEnum.getValue(currentDataOpe.getStatusOperator()) :
                        newDataOpe.getOperatorStatus()
        );
    }
}
