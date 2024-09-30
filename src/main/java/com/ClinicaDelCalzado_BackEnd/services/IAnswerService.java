package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.entity.Answer;

import java.util.List;

public interface IAnswerService {

    List<Answer> findAnswerAllByAdminId(Long adminId);

    void saveAnswer(Answer answer);
}
