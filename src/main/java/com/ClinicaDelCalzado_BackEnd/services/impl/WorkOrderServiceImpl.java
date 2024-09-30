package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.enums.OrderStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.enums.PaymentStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.enums.ServicesStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.request.AddCommentDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdatePaymentDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateServicesDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.*;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.*;
import com.ClinicaDelCalzado_BackEnd.entity.*;
import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import com.ClinicaDelCalzado_BackEnd.repository.workOrders.IWorkOrderRepository;
import com.ClinicaDelCalzado_BackEnd.services.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.ClinicaDelCalzado_BackEnd.util.Constants.*;

@Service
public class WorkOrderServiceImpl implements IWorkOrderService {

    private final IWorkOrderRepository workOrderRepository;

    private final ICompanyService companyService;

    private final IClientService clientService;

    private final IProductService productService;

    private final IAdminService adminService;

    private final ICommentService commentService;

    private final IOperatorService operatorService;

    @Autowired
    public WorkOrderServiceImpl(IWorkOrderRepository workOrderRepository, ICompanyService companyService,
                                IClientService clientService, IProductService productService,
                                IAdminService adminService, ICommentService commentService, IOperatorService operatorService) {
        this.workOrderRepository = workOrderRepository;
        this.companyService = companyService;
        this.clientService = clientService;
        this.productService = productService;
        this.adminService = adminService;
        this.commentService = commentService;
        this.operatorService = operatorService;
    }

    @Override
    public WorkOrderDTOResponse createWorkOrder(WorkOrderDTORequest workOrderDTORequest, Long userAuth) {

        validateRequest(workOrderDTORequest);

        Company company = companyService.findCompanyWorkOrder(workOrderDTORequest);
        Client client = clientService.findClientWorkOrder(workOrderDTORequest);

        Administrator attendedBy = adminService.findAdministratorById(userAuth)
                .orElseThrow(() -> new NotFoundException(String.format("Administrator %s not found", workOrderDTORequest.getAttendedById())));

        double totalPriceOrder = totalPrice(workOrderDTORequest.getServices());
        double newBalance = totalPriceOrder - workOrderDTORequest.getDownPayment();

        WorkOrder workOrder = saveWorkOrder(
                WorkOrder.builder()
                        .orderNumber(String.format("%s-%s-%d", ORDER_ABR, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), generateRandomValueOrder()))
                        .idCompany(company)
                        .creationDate(LocalDateTime.now())
                        .deliveryDate(LocalDate.parse(workOrderDTORequest.getDeliveryDate().toString()))
                        .orderStatus(OrderStatusEnum.VALID.getKeyName())
                        .paymentStatus(newBalance == 0 ? PaymentStatusEnum.PAID.getKeyName() : PaymentStatusEnum.PENDING.getKeyName())
                        .attendedBy(attendedBy)
                        .idClient(client)
                        .deposit(workOrderDTORequest.getDownPayment())
                        .totalValue(totalPriceOrder)
                        .balance(newBalance)
                        .build());

        if (ObjectUtils.isNotEmpty(workOrderDTORequest.getGeneralComment())) {
            commentService.saveCommentOrder(workOrderDTORequest.getGeneralComment(), workOrder.getOrderNumber(), userAuth);
        }

        List<Boolean> servicesDTOS = productService.saveServicesWorkOrder(workOrderDTORequest, workOrder);
        if (servicesDTOS.stream().anyMatch(p -> p.equals(Boolean.TRUE))) {
            workOrder.setPaymentStatus(PaymentStatusEnum.PENDING.getKeyName());
            saveWorkOrder(workOrder);
        }

        return new WorkOrderDTOResponse("Orden de trabajo creada exitosamente!", workOrder.getOrderNumber());
    }

    @Override
    public MessageDTOResponse cancelWorkOrder(String orderNumber, Long userAuth) {

        WorkOrder workOrder = validateOrderNumber(orderNumber);
        if (workOrder.getPaymentStatus().equals(PaymentStatusEnum.PAID.getKeyName())) {
            throw new BadRequestException(String.format("La orden de trabajo no puede ser anulada porque esta en estado %s!!",
                    PaymentStatusEnum.getValue(workOrder.getPaymentStatus())));
        }

        workOrder.setOrderStatus(OrderStatusEnum.CANCELED.getKeyName());
        workOrder.setModificationDate(LocalDateTime.now());
        workOrder.setLastModificationBy(userAuth);

        saveWorkOrder(workOrder);
        commentService.saveCommentOrder("Orden de trabajo cancelada", workOrder.getOrderNumber(), userAuth);

        return MessageDTOResponse.builder().message("Orden de trabajo cancelada con éxito.").build();
    }

    @Override
    public ServicesDTOResponse updateServicesWorkOrder(Integer serviceId, UpdateServicesDTORequest servicesDTO, Long auth) {

        ServicesEntity services = productService.update(serviceId, servicesDTO);
        String orderNumber = services.getIdOrderSer().getOrderNumber();
        WorkOrder workOrder = validateOrderNumber(orderNumber);
        List<ServicesDTO> servicesList = productService.getServicesOrder(orderNumber);

        if (ObjectUtils.isNotEmpty(servicesDTO.getPrice()) && servicesList.stream().noneMatch(ServicesDTO::getHasPendingPrice)) {
            updateValuesWorkOrder(servicesList, workOrder);
        }

        if (ObjectUtils.isNotEmpty(servicesDTO.getServiceStatus()) &&
                servicesList.stream().anyMatch(p -> p.getServiceStatus().equals(ServicesStatusEnum.DISPATCHED.getKeyName()))) {
            updateStatusWorkOrder(workOrder, auth);
        }
        return ServicesDTOResponse.builder()
                .message("Servicios actualizado exitosamente.")
                .service(ServicesDTOList.builder()
                        .id(services.getIdService())
                        .idOrder(services.getIdOrderSer().getOrderNumber())
                        .name(services.getService())
                        .price(services.getUnitValue().longValue())
                        .operator(servicesList.stream()
                                .filter(p-> p.getId().equals(services.getIdService()))
                                .map(ServicesDTO::getOperator)
                                .findFirst().orElse(OperatorServiceDTO.builder().build()))
                        .serviceStatus(services.getServiceStatus())
                        .build())
                .build();
    }

    @Override
    public MessageDTOResponse updatePaymentWorkOrder(String orderNumber, Long userAuth, UpdatePaymentDTORequest updatePaymentDTORequest) {

        if (ObjectUtils.isEmpty(updatePaymentDTORequest.getPaymentAmount())) {
            throw new BadRequestException("El abono no puede estar vacio!!");
        }

        double payment = updatePaymentDTORequest.getPaymentAmount();
        WorkOrder workOrder = validateOrderNumber(orderNumber);

        if (payment == 0){
            throw new BadRequestException("El abono no puede ser cero!!");
        }

        if (workOrder.getPaymentStatus().equals(PaymentStatusEnum.PAID.getKeyName())){
            throw new BadRequestException("La orden de trabajo ya esta paga!!");
        }

        if (!workOrder.getOrderStatus().equals(OrderStatusEnum.VALID.getKeyName())){
            throw new BadRequestException("La orden de trabajo no se encuentra en un estado vigente!!");
        }

        double totalPriceOrder = workOrder.getTotalValue();
        double deposit = workOrder.getDeposit();
        double newDeposit = deposit + payment;
        double newBalance = totalPriceOrder - newDeposit;

        if (newDeposit > totalPriceOrder) {
            throw new BadRequestException("El abono supera el saldo total!!");
        }

        String commentPayment = String.format("Abono de $%.0f realizado exitosamente y cuenta con un saldo pendiente de $%.0f", payment, newBalance);
        commentService.saveCommentOrder(commentPayment, workOrder.getOrderNumber(), userAuth);

        if (newBalance == 0) {
            workOrder.setPaymentStatus(PaymentStatusEnum.PAID.getKeyName());
            commentService.saveCommentOrder("Orden de trabajo pagada", workOrder.getOrderNumber(), userAuth);
        }

        workOrder.setDeposit(newDeposit);
        workOrder.setBalance(newBalance);
        saveWorkOrder(workOrder);

        return MessageDTOResponse.builder().message(commentPayment).build();
    }

    @Override
    public MessageDTOResponse addCommentWorkOrder(String orderNumber, Long userAuth, AddCommentDTORequest addCommentDTORequest) {

        WorkOrder workOrder = validateOrderNumber(orderNumber);
        if (ObjectUtils.isEmpty(addCommentDTORequest.getComment())) {
            throw new BadRequestException("El comentario no puede ser vacio!!");
        }

        commentService.saveCommentOrder(addCommentDTORequest.getComment(), workOrder.getOrderNumber(), userAuth);

        return MessageDTOResponse.builder().message("Comentario añadido exitosamente a la orden de trabajo.").build();
    }

    @Override
    public OrderByIdNumberDTOResponse getWorkOrderByOrderNumber(String orderNumber) {

        WorkOrder workOrder = getOrder(orderNumber);
        Optional<Company> company = companyService.findCompanyByNit(workOrder.getIdCompany().getNit());
        Optional<Client> client = clientService.findClientByIdClient(workOrder.getIdClient().getIdClient());
        Optional<Administrator> attendedBy = adminService.findAdministratorById(workOrder.getAttendedBy().getIdAdministrator());
        List<ServicesDTO> servicesDTOList = productService.getServicesOrder(orderNumber);
        List<CommentDTO> commentDTOList = commentService.getCommentOrder(orderNumber);

        CompanyDTO companyDTO = company.map(value -> CompanyDTO
                .builder()
                .name(value.getName())
                .nit(value.getNit())
                .address(value.getAddress())
                .phones(Collections.singletonList(value.getPhones()))
                .build()).orElse(null);

        ClientDTO clientDTO = client.map(value -> ClientDTO
                .builder()
                .identification(value.getIdClient())
                .name(value.getClientName())
                .cellphone(value.getCliPhoneNumber())
                .build()).orElse(null);

        return OrderByIdNumberDTOResponse
                .builder()
                .orderNumber(orderNumber)
                .company(companyDTO)
                .attendedBy(attendedBy.map(Administrator::getAdminName).orElse(""))
                .createDate(workOrder.getCreationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm")))
                .orderStatus(OrderStatusEnum.getValue(workOrder.getOrderStatus()))
                .deliveryDate(workOrder.getDeliveryDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .client(clientDTO)
                .services(servicesDTOList)
                .comments(commentDTOList)
                .downPayment(workOrder.getDeposit().longValue())
                .totalValue(workOrder.getTotalValue().longValue())
                .balance(workOrder.getBalance().longValue())
                .paymentStatus(PaymentStatusEnum.getValue(workOrder.getPaymentStatus()))
                .build();
    }

    @Override
    public OrderListDTOResponse getWorkOrderList(String orderStatus, String orderNumber, Long identification, String name, String phone, String attendedBy) {

        List<Object[]> workOrderList;

        if (!ObjectUtils.isEmpty(orderNumber) || !ObjectUtils.isEmpty(identification) || !ObjectUtils.isEmpty(name)
                || !ObjectUtils.isEmpty(phone) || !ObjectUtils.isEmpty(attendedBy)) {
            workOrderList = workOrderRepository.findFilteredWorkOrders(orderStatus, orderNumber, identification, name, phone, attendedBy);
        } else {
            workOrderList = workOrderRepository.findOrdersWithServicesByStatus(orderStatus);
        }

        return getOrderList(workOrderList);
    }

    @Override
    public WorkOrder validateOrderNumber(String orderNumber) {
        if (ObjectUtils.isEmpty(orderNumber)) {
            throw new BadRequestException(String.format("La orden %s no esta registrada!!", orderNumber));
        }

        return workOrderRepository.findById(orderNumber)
                .orElseThrow(() -> new NotFoundException(String.format("La orden de trabajo %s no encontrada", orderNumber)));
    }

    private void updateValuesWorkOrder(List<ServicesDTO> servicesDTO, WorkOrder workOrder) {
        double totalPriceOrder = totalPrice(servicesDTO);
        double newBalance = totalPriceOrder - workOrder.getDeposit();

        workOrder.setTotalValue(totalPriceOrder);
        workOrder.setBalance(newBalance);

        if (newBalance == 0) {
            workOrder.setPaymentStatus(PaymentStatusEnum.PAID.getKeyName());
            commentService.saveCommentOrder("Orden de trabajo pagada", workOrder.getOrderNumber(), workOrder.getAttendedBy().getIdAdministrator());
        }
        saveWorkOrder(workOrder);
    }

    private void updateStatusWorkOrder(WorkOrder workOrder, Long userAuth) {

        workOrder.setOrderStatus(OrderStatusEnum.FINISHED.getKeyName());
        workOrder.setModificationDate(LocalDateTime.now());
        workOrder.setLastModificationBy(userAuth);

        saveWorkOrder(workOrder);
        commentService.saveCommentOrder("Orden de trabajo finalizada", workOrder.getOrderNumber(), userAuth);

    }

    private WorkOrder getOrder(String orderNumber) {
        Optional<WorkOrder> workOrder = workOrderRepository.findById(orderNumber);

        if (workOrder.isEmpty()) {
            throw new NotFoundException(String.format("La orden %s no esta registrada!!", orderNumber));
        }

        return workOrder.get();
    }

    private OrderListDTOResponse getOrderList(List<Object[]> workOrderList) {

        // Mapear los resultados de Object[] a una estructura de datos más manejable
        Map<String, List<Object[]>> ordersGroupedByOrderNumber = workOrderList.stream()
                .collect(Collectors.groupingBy(row -> (String) row[0]));  // Agrupa por orderNumber (posición 0)

        // Crear la respuesta
        OrderListDTOResponse orderListDTOResponse = new OrderListDTOResponse();

        // Convertir a OrderDTOResponse con conteo de servicios
        orderListDTOResponse.setOrders(
                ordersGroupedByOrderNumber.entrySet().stream()
                        .map(entry -> {
                            String orderNumber = entry.getKey();
                            List<Object[]> groupedOrders = entry.getValue();

                            // Obtener la primera orden para extraer los datos comunes
                            Object[] firstOrderRow = groupedOrders.get(0);

                            // Extraer datos comunes desde la primera fila del grupo
                            Long idClient = (Long) firstOrderRow[1];  // id_client (posición 1)
                            String clientName = (String) firstOrderRow[2]; // client_name (posición 2)
                            String clientPhone = (String) firstOrderRow[3]; // cli_phone_number (posición 3)
                            Timestamp creationDate = (Timestamp) firstOrderRow[4]; // creation_date (posición 4)
                            Date deliveryDate = (Date) firstOrderRow[5]; // delivery_date (posición 5)
                            long servicesOrderCount = (long) firstOrderRow[6]; // services_count (posición 6)

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

                            // Construir el OrderDTOResponse
                            return OrderDTOResponse.builder()
                                    .orderNumber(orderNumber)
                                    .client(ClientDTO.builder()
                                            .identification(idClient)
                                            .name(clientName)
                                            .cellphone(clientPhone)
                                            .build())
                                    .createDate(creationDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm")))
                                    .deliveryDate(simpleDateFormat.format(deliveryDate))
                                    .servicesCount(servicesOrderCount) // Cantidad total de servicios
                                    .orderStatus(OrderStatusEnum.getValue(String.valueOf(firstOrderRow[7]))) // order_status (posición 7)
                                    .totalValue(((Double) firstOrderRow[8]).longValue()) // total_value (posición 8)
                                    .downPayment(((Double) firstOrderRow[9]).longValue()) // deposit (posición 9)
                                    .balance(((Double) firstOrderRow[10]).longValue())  // balance (posición 10)
                                    .paymentStatus(PaymentStatusEnum.getValue(String.valueOf(firstOrderRow[11])))  // payment_status (posición 11)
                                    .build();
                        })
                        .toList()
        );

        return orderListDTOResponse;
    }

    private WorkOrder saveWorkOrder(WorkOrder workOrder) {
        return workOrderRepository.save(workOrder);
    }

    private Double totalPrice(List<ServicesDTO> servicesDTO) {
        return servicesDTO.stream().mapToDouble(ServicesDTO::getPrice).sum();
    }

    private Integer generateRandomValueOrder() {
        Random random = new Random();
        return RANDOM_MIN + random.nextInt(RANDOM_MAX - RANDOM_MIN + 1);
    }

    private void validateRequest(WorkOrderDTORequest workOrderDTORequest) {
        String nit = workOrderDTORequest.getCompany().getNit();
        LocalDate deliveryDate = workOrderDTORequest.getDeliveryDate();
        LocalDate today = LocalDate.now();
        Long idClient = workOrderDTORequest.getClient().getIdentification();
        List<ServicesDTO> services = workOrderDTORequest.getServices();
        Double downPayment = workOrderDTORequest.getDownPayment();

        if (nit.isEmpty()) {
            throw new BadRequestException(String.format("La compañía con nit %s no esta registrada!!", nit));
        }

        if (ObjectUtils.isEmpty(deliveryDate)) {
            throw new BadRequestException("Se requiere la fecha de entrega para la orden de trabajo!!");
        }

        if (deliveryDate.isBefore(today)){
            throw new BadRequestException("La fecha de entrega para la orden de trabajo no puede ser menor que hoy!!");
        }

        if (ObjectUtils.isEmpty(idClient)) {
            throw new BadRequestException("Se requiere identificación del cliente!!");
        }

        if (CollectionUtils.isEmpty(services)) {
            throw new BadRequestException("Se requiere el listado de servicios para la orden de trabajo!!");
        }

        if (ObjectUtils.isEmpty(downPayment)) {
            throw new BadRequestException("Se requiere el abono del cliente!!");
        }
    }
}
