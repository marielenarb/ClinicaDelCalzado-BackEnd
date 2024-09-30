package com.ClinicaDelCalzado_BackEnd.controller;

import com.ClinicaDelCalzado_BackEnd.dtos.response.QuestionListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.services.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    @GetMapping("/list")
    public ResponseEntity<QuestionListDTOResponse> getAllQuestions(Authentication authentication) {

        QuestionListDTOResponse responseDTO = questionService.findAllQuestions();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
