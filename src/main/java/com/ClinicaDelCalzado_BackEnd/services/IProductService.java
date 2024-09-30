package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateServicesDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ServicesDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.ServicesListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ServicesDTO;
import com.ClinicaDelCalzado_BackEnd.entity.ServicesEntity;
import com.ClinicaDelCalzado_BackEnd.entity.WorkOrder;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    ServicesEntity save(ServicesEntity services);
    ServicesListDTOResponse findServicesAll();
    ServicesDTOResponse findServicesById(Integer servicesId);
    Optional<ServicesEntity> findServiceById(Integer idService);
    List<ServicesDTO> getServicesOrder(String orderNumber);
    List<Boolean> saveServicesWorkOrder(WorkOrderDTORequest workOrderDTORequest, WorkOrder orderNumber);
    ServicesEntity update(Integer serviceId, UpdateServicesDTORequest servicesDTO);
}
