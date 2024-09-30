package com.ClinicaDelCalzado_BackEnd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comment", length = 20)
    private Integer idComment;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private WorkOrder idOrderCom;

    @Column(name = "admin_comment")
    private String adminComment;

    @Column(name = "creation_date_comment", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime creationDateComment;

    @ManyToOne
    @JoinColumn(name = "comment_by")
    private Administrator commentBy;

}