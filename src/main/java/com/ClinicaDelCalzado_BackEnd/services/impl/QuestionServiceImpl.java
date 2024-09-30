package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.response.QuestionListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.questions.QuestionDTO;
import com.ClinicaDelCalzado_BackEnd.entity.SecurityQuestion;
import com.ClinicaDelCalzado_BackEnd.repository.userAdmin.ISecurityQuestionRepository;
import com.ClinicaDelCalzado_BackEnd.services.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements IQuestionService {

    private final ISecurityQuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(ISecurityQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public QuestionListDTOResponse findAllQuestions() {
        List<SecurityQuestion> questionList = questionRepository.findAll();

        QuestionListDTOResponse questionListDTOResponse = new QuestionListDTOResponse();
        questionListDTOResponse.setQuestions(questionList.stream().map(p ->
                new QuestionDTO(p.getIdSecurityQuestion(), p.getQuestion())).toList());

        return questionListDTOResponse;
    }
}
