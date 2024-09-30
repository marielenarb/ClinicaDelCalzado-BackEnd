package com.ClinicaDelCalzado_BackEnd.repository.workOrders;

import com.ClinicaDelCalzado_BackEnd.entity.Comment;
import com.ClinicaDelCalzado_BackEnd.exceptions.RepositoryException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Integer> {
    @Query(value = "SELECT * FROM comment c INNER JOIN work_order wo ON c.id_order = wo.order_number WHERE c.id_order = :orderNumber", nativeQuery = true)
    List<Comment> findCommentByWorkOrder(@Param("orderNumber") String idOrder) throws RepositoryException;
}
