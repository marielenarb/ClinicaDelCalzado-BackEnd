package com.ClinicaDelCalzado_BackEnd.repository.userAdmin;

import com.ClinicaDelCalzado_BackEnd.entity.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISecurityQuestionRepository extends JpaRepository<SecurityQuestion, Integer> {
}
