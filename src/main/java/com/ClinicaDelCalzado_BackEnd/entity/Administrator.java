package com.ClinicaDelCalzado_BackEnd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "administrator")
public class Administrator {

    @Id
    @Column(name = "id_administrator", length = 20)
    private Long idAdministrator;

    @Column(name = "admin_name", nullable = false, length = 100)
    private String adminName;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "adm_phone_number", length = 20)
    private String admPhoneNumber;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "admin_status", length = 15)
    private String adminStatus;

    @Column(name = "temporary_pwd", nullable = false)
    private Boolean hasTemporaryPassword;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAdministrator")
    private List<Answer> answersList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "attendedBy")
    private List<WorkOrder> workOrdersList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "commentBy")
    private List<Comment> commentList;
}