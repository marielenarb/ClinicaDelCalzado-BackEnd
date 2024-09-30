package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.entity.Administrator;
import com.ClinicaDelCalzado_BackEnd.entity.Answer;
import com.ClinicaDelCalzado_BackEnd.repository.userAdmin.IAnswerRepository;
import com.ClinicaDelCalzado_BackEnd.services.IAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService implements IAnswerService {

    private final IAnswerRepository answerRepository;

    @Autowired
    public AnswerService(IAnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public List<Answer> findAnswerAllByAdminId(Long adminId) {
        return answerRepository.findAnswerByIdAdministrator(Administrator.builder().idAdministrator(adminId).build());
    }

    @Override
    public void saveAnswer(Answer answer) {
        answerRepository.save(answer);
    }


}
