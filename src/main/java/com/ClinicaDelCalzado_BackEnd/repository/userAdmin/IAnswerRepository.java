package com.ClinicaDelCalzado_BackEnd.repository.userAdmin;

import com.ClinicaDelCalzado_BackEnd.entity.Administrator;
import com.ClinicaDelCalzado_BackEnd.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAnswerRepository extends JpaRepository<Answer, Integer> {

    List<Answer> findAnswerByIdAdministrator(Administrator administrator);
}
