package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.enums.ServicesStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.operator.OperatorDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateServicesDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ServicesDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ServicesListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.OperatorServiceDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ServicesDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ServicesDTOList;
import com.ClinicaDelCalzado_BackEnd.entity.Operator;
import com.ClinicaDelCalzado_BackEnd.entity.ServicesEntity;
import com.ClinicaDelCalzado_BackEnd.entity.WorkOrder;
import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import com.ClinicaDelCalzado_BackEnd.repository.workOrders.IServicesRepository;
import com.ClinicaDelCalzado_BackEnd.services.IOperatorService;
import com.ClinicaDelCalzado_BackEnd.services.IProductService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    private final IServicesRepository servicesRepository;
    private final IOperatorService operatorService;

    @Autowired
    public ProductServiceImpl(IServicesRepository servicesRepository, IOperatorService operatorService) {
        this.servicesRepository = servicesRepository;
        this.operatorService = operatorService;
    }

    @Override
    public ServicesEntity save(ServicesEntity services) {
        return servicesRepository.save(services);
    }

    @Override
    public List<ServicesDTO> getServicesOrder(String orderNumber) {
        List<ServicesEntity> servicesList = servicesRepository.findByWorkOrder(orderNumber);
        List<OperatorDTO> operator = operatorService.findOperatorAll().getOperators();

        if (servicesList.isEmpty()) {
            throw new NotFoundException(String.format("La orden %s no esta registrada!!", orderNumber));
        }

        return servicesList.stream()
                .map(serv -> ServicesDTO.builder()
                        .id(serv.getIdService())
                        .name(serv.getService())
                        .price(serv.getUnitValue().longValue())
                        .hasPendingPrice(serv.getHasPendingUnitValue())
                        .operator(getOperatorService(operator, serv.getIdOperator()))
                        .serviceStatus(ServicesStatusEnum.getValue(serv.getServiceStatus()))
                        .build())
                .toList();
    }

    @Override
    public Optional<ServicesEntity> findServiceById(Integer idService) {
        return servicesRepository.findById(idService);
    }

    @Override
    public ServicesListDTOResponse findServicesAll() {
        List<ServicesEntity> servicesEntitiesList = servicesRepository.findAll();
        List<OperatorDTO> operator = operatorService.findOperatorAll().getOperators();

        ServicesListDTOResponse servicesListDTOResponse = new ServicesListDTOResponse();
        servicesListDTOResponse.setServices(servicesEntitiesList.stream()
                .map(serv -> ServicesDTOList.builder()
                        .id(serv.getIdService())
                        .idOrder(serv.getIdOrderSer().getOrderNumber())
                        .name(serv.getService())
                        .price(serv.getUnitValue().longValue())
                        .operator(getOperatorService(operator, serv.getIdOperator()))
                        .serviceStatus(ServicesStatusEnum.getValue(serv.getServiceStatus()))
                        .build())
                .toList());

        return servicesListDTOResponse;
    }

    @Override
    public ServicesDTOResponse findServicesById(Integer servicesId) {
        validateIdServices(servicesId);
        ServicesEntity services = validateServiceIdExists(servicesId);

        return servicesDTOResponse("Detalles del servicio recuperado exitosamente.", services);
    }

    @Override
    public List<Boolean> saveServicesWorkOrder(WorkOrderDTORequest workOrderDTORequest, WorkOrder orderNumber) {

        return workOrderDTORequest.getServices().stream()
                .map(serviceDTO -> {
                    ServicesEntity service = ServicesEntity.builder()
                            .idOrderSer(orderNumber)
                            .service(serviceDTO.getName())
                            .unitValue(serviceDTO.getPrice().doubleValue())
                            .serviceStatus(ServicesStatusEnum.RECEIVED.getKeyName())
                            .idOperator(ObjectUtils.isNotEmpty(serviceDTO.getOperator())
                                    && ObjectUtils.isNotEmpty(serviceDTO.getOperator().getIdOperator()) ?
                                    Operator.builder().idOperator(serviceDTO.getOperator().getIdOperator()).build()
                                    : null)
                            .hasPendingUnitValue(!ObjectUtils.isNotEmpty(serviceDTO.getPrice()) || serviceDTO.getPrice() <= 0)
                            .build();
                    save(service);
                    return service.getHasPendingUnitValue();
                }).collect(Collectors.toList());
    }

    @Override
    public ServicesEntity update(Integer serviceId, UpdateServicesDTORequest servicesDTO) {
        ServicesEntity services = matchDifferencesSer(validateServiceIdExists(serviceId), servicesDTO);
        return save(services);
    }

    private void validateIdServices(Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new BadRequestException("El id del servicio es un campo obligatorio, no puede ser vacio");
        }
    }

    private ServicesEntity validateServiceIdExists(Integer id) {
        Optional<ServicesEntity> servicesById = findServiceById(id);

        if (servicesById.isEmpty()) {
            throw new NotFoundException(String.format("El id %d del servicio no esta registrado!!", id));
        }
        return servicesById.get();
    }

    private ServicesDTOResponse servicesDTOResponse(String message, ServicesEntity servicesEntity) {
        List<OperatorDTO> operator = operatorService.findOperatorAll().getOperators();

        return ServicesDTOResponse.builder()
                .message(message)
                .service(ServicesDTOList.builder()
                        .id(servicesEntity.getIdService())
                        .idOrder(servicesEntity.getIdOrderSer().getOrderNumber())
                        .name(servicesEntity.getService())
                        .price(servicesEntity.getUnitValue().longValue())
                        .operator(getOperatorService(operator, servicesEntity.getIdOperator()))
                        .serviceStatus(ServicesStatusEnum.getValue(servicesEntity.getServiceStatus()))
                        .build())
                .build();
    }

    private ServicesEntity buildService(Integer serviceId, String serviceName, String orderNumber, Long operatorId, Long price, String serviceStatus, Boolean hasPendingValue) {

        return ServicesEntity.builder()
                .idService(serviceId)
                .idOrderSer(WorkOrder.builder().orderNumber(orderNumber).build())
                .service(serviceName)
                .idOperator(Operator.builder().idOperator(operatorId).build())
                .unitValue(price.doubleValue())
                .serviceStatus(serviceStatus)
                .hasPendingUnitValue(hasPendingValue)
                .build();
    }

    private ServicesEntity matchDifferencesSer(ServicesEntity currentDataSer, UpdateServicesDTORequest newDataSer) {
        return buildService(
                currentDataSer.getIdService(),
                ObjectUtils.isEmpty(newDataSer.getServiceName()) || Objects.equals(currentDataSer.getService(), newDataSer.getServiceName()) ?
                        currentDataSer.getService() : newDataSer.getServiceName(),
                currentDataSer.getIdOrderSer().getOrderNumber(),
                ObjectUtils.isEmpty(newDataSer.getOperatorId()) || (ObjectUtils.isNotEmpty(currentDataSer.getIdOperator()) && Objects.equals(currentDataSer.getIdOperator().getIdOperator(), newDataSer.getOperatorId())) ?
                        currentDataSer.getIdOperator().getIdOperator() : newDataSer.getOperatorId(),
                ObjectUtils.isEmpty(newDataSer.getPrice()) || Objects.equals(currentDataSer.getUnitValue(), newDataSer.getPrice().doubleValue()) ?
                        currentDataSer.getUnitValue().longValue() : newDataSer.getPrice(),
                ObjectUtils.isEmpty(newDataSer.getServiceStatus()) || Objects.equals(currentDataSer.getServiceStatus(), ServicesStatusEnum.getName(newDataSer.getServiceStatus())) ?
                        currentDataSer.getServiceStatus() : ServicesStatusEnum.getName(newDataSer.getServiceStatus()),
                ObjectUtils.isEmpty(newDataSer.getPrice()) ? currentDataSer.getHasPendingUnitValue() : false
        );

    }

    private OperatorServiceDTO getOperatorService(List<OperatorDTO> operatorDTO, Operator operator) {
        OperatorServiceDTO operatorServiceDTO = new OperatorServiceDTO();

        if (ObjectUtils.isNotEmpty(operator) && ObjectUtils.isNotEmpty(operator.getIdOperator())) {
            Optional<OperatorDTO> optionalOperator = operatorDTO.stream()
                    .filter(ope -> Objects.equals(ope.getIdOperator(), operator.getIdOperator())).findFirst();

            if (optionalOperator.isPresent()) {
                operatorServiceDTO.setIdOperator(optionalOperator.get().getIdOperator());
                operatorServiceDTO.setOperatorName(optionalOperator.get().getOperatorName());
            }
        }
        return operatorServiceDTO;
    }

}
