package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.request.AddCommentDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdatePaymentDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateServicesDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.*;
import com.ClinicaDelCalzado_BackEnd.entity.WorkOrder;

public interface IWorkOrderService {

    WorkOrderDTOResponse createWorkOrder(WorkOrderDTORequest workOrderDTORequest, Long userAuth);
    MessageDTOResponse cancelWorkOrder(String orderNumber, Long userAuth);
    ServicesDTOResponse updateServicesWorkOrder(Integer serviceId, UpdateServicesDTORequest servicesDTO, Long auth);
    MessageDTOResponse updatePaymentWorkOrder(String orderNumber, Long userAuth,  UpdatePaymentDTORequest updatePaymentDTORequest);
    MessageDTOResponse addCommentWorkOrder(String orderNumber, Long userAuth, AddCommentDTORequest addCommentDTORequest);
    OrderByIdNumberDTOResponse getWorkOrderByOrderNumber(String orderNumber);
    OrderListDTOResponse getWorkOrderList(String orderStatus, String orderNumber, Long identification, String name, String phone, String attendedBy);
    WorkOrder validateOrderNumber(String orderNumber);
}
