package com.ClinicaDelCalzado_BackEnd.repository.workOrders;

import com.ClinicaDelCalzado_BackEnd.entity.ServicesEntity;
import com.ClinicaDelCalzado_BackEnd.exceptions.RepositoryException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IServicesRepository extends JpaRepository <ServicesEntity, Integer> {
    @Query(value = "SELECT * FROM service s INNER JOIN work_order wo ON s.id_order = wo.order_number WHERE s.id_order = :orderNumber", nativeQuery = true)
    List<ServicesEntity> findByWorkOrder(@Param("orderNumber") String idOrder) throws RepositoryException;
}
