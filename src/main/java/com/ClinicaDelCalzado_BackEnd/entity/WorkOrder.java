package com.ClinicaDelCalzado_BackEnd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "work_order")
public class WorkOrder {

    @Id
    @Column(name = "order_number", length = 20)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "id_company")
    private Company idCompany;

    @Column(name = "creation_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "modification_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime modificationDate;

    @Column(name = "delivery_date", columnDefinition = "DATE")
    private LocalDate deliveryDate;

    @Column(name = "order_status", length = 15)
    private String orderStatus;

    @Column(name = "payment_status", length = 15)
    private String paymentStatus;

    @ManyToOne
    @JoinColumn(name = "attended_by")
    private Administrator attendedBy;

    @Column(name = "last_modification_by")
    private Long lastModificationBy;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client idClient;

    @Column(name = "deposit")
    private Double deposit;

    @Column(name = "total_value")
    private Double totalValue;

    @Column(name = "balance")
    private Double balance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idOrderSer")
    private List<ServicesEntity> servicesList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idOrderCom")
    private List<Comment> commentsList;
}