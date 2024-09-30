package com.ClinicaDelCalzado_BackEnd.repository.operator;

import com.ClinicaDelCalzado_BackEnd.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOperatorRepository  extends JpaRepository<Operator, Long> {
}
