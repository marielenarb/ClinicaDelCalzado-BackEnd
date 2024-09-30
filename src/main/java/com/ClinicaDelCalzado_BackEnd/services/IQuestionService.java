package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.response.QuestionListDTOResponse;

public interface IQuestionService {

    QuestionListDTOResponse findAllQuestions();
}
