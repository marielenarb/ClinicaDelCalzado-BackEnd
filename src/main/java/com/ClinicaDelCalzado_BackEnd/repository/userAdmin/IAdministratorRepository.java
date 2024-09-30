package com.ClinicaDelCalzado_BackEnd.repository.userAdmin;

import com.ClinicaDelCalzado_BackEnd.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdministratorRepository extends JpaRepository<Administrator, Long> {
}
