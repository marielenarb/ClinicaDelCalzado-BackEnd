package com.ClinicaDelCalzado_BackEnd.controller;

import com.ClinicaDelCalzado_BackEnd.dtos.enums.OrderStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.request.AddCommentDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdatePaymentDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateServicesDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.*;
import com.ClinicaDelCalzado_BackEnd.exceptions.UnauthorizedException;
import com.ClinicaDelCalzado_BackEnd.services.IWorkOrderService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/work-orders")
public class OrderController {

    @Autowired
    private IWorkOrderService workOrderService;

    @PostMapping("/created")
    public ResponseEntity<WorkOrderDTOResponse> createWorkOrder(@RequestBody WorkOrderDTORequest workOrderDTO, Authentication authentication) {
        String userAuth = getUserAuth(authentication);

        WorkOrderDTOResponse responseDTO = workOrderService.createWorkOrder(workOrderDTO, Long.valueOf(userAuth));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/cancel/{orderNumber}")
    public ResponseEntity<MessageDTOResponse> cancelWorkOrder(@PathVariable String orderNumber, Authentication authentication) {
        String userAuth = getUserAuth(authentication);

        MessageDTOResponse messageDTOResponse = workOrderService.cancelWorkOrder(orderNumber, Long.valueOf(userAuth));
        return new ResponseEntity<>(messageDTOResponse, HttpStatus.OK);
    }

    @PutMapping("/payment/{orderNumber}")
    public ResponseEntity<MessageDTOResponse> paymentWorkOrder(@PathVariable String orderNumber,
                                                               @RequestBody UpdatePaymentDTORequest updatePaymentDTORequest, Authentication authentication) {
        String userAuth = getUserAuth(authentication);

        MessageDTOResponse messageDTOResponse = workOrderService.updatePaymentWorkOrder(orderNumber, Long.valueOf(userAuth), updatePaymentDTORequest);
        return new ResponseEntity<>(messageDTOResponse, HttpStatus.OK);
    }

    @PutMapping({"/comment/{orderNumber}", "/comment"})
    public ResponseEntity<MessageDTOResponse> addCommentWorkOrder(@PathVariable(value = "orderNumber", required = false) String orderNumber,
                                                                  @RequestBody AddCommentDTORequest addCommentDTORequest, Authentication authentication) {
        String userAuth = getUserAuth(authentication);

        MessageDTOResponse messageDTOResponse = workOrderService.addCommentWorkOrder(orderNumber, Long.valueOf(userAuth), addCommentDTORequest);
        return new ResponseEntity<>(messageDTOResponse, HttpStatus.OK);
    }

    @PutMapping("/updated/service/{serviceId}")
    public ResponseEntity<ServicesDTOResponse> updateServiceWorkOrder(@PathVariable Integer serviceId,
                                                                      @RequestBody UpdateServicesDTORequest servicesDTORequest, Authentication authentication) {
        String userAuth = getUserAuth(authentication);

        ServicesDTOResponse responseDTO = workOrderService.updateServicesWorkOrder(serviceId, servicesDTORequest, Long.valueOf(userAuth));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderByIdNumberDTOResponse> getWorkOrderByOrderNumber(@PathVariable(value = "orderNumber", required = false) String orderNumber, Authentication authentication) {
        OrderByIdNumberDTOResponse orderByIdNumberDTOResponse = workOrderService.getWorkOrderByOrderNumber(orderNumber);
        return new ResponseEntity<>(orderByIdNumberDTOResponse, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<OrderListDTOResponse> getWorkOrderList(@RequestParam(value = "order_status", required = false) String orderStatus,
                                                                 @RequestParam(value = "order_number", required = false) String orderNumber,
                                                                 @RequestParam(value = "identification", required = false) Long identification,
                                                                 @RequestParam(value = "name", required = false) String name,
                                                                 @RequestParam(value = "phone", required = false) String phone,
                                                                 @RequestParam(value = "attended_by", required = false) String attendedBy,
                                                                 Authentication authentication) {

        orderStatus = ObjectUtils.isEmpty(orderStatus) ? OrderStatusEnum.VALID.getKeyName() : OrderStatusEnum.getName(orderStatus);

        OrderListDTOResponse orderListDTOResponse = workOrderService.getWorkOrderList(orderStatus, orderNumber, identification, name, phone, attendedBy);
        return new ResponseEntity<>(orderListDTOResponse, HttpStatus.OK);
    }

    private String getUserAuth(Authentication authentication) {
        if (ObjectUtils.isEmpty(authentication.getPrincipal())) {
            throw new UnauthorizedException("La sesión ha caducado o no esta autorizado para realizar esta acción");
        }
        return ((User) authentication.getPrincipal()).getUsername();
    }
}
