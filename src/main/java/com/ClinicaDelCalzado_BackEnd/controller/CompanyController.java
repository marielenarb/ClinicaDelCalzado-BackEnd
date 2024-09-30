package com.ClinicaDelCalzado_BackEnd.controller;

import com.ClinicaDelCalzado_BackEnd.dtos.response.CompanyDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.CompanyListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.services.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    @Autowired
    private ICompanyService companyService;

    @GetMapping("/list")
    public ResponseEntity<CompanyListDTOResponse> getAllCompany(Authentication authentication) {

        CompanyListDTOResponse companyListDTO = companyService.findCompanyAll();
        return new ResponseEntity<>(companyListDTO, HttpStatus.OK);
    }

    @GetMapping("/{nit}")
    public ResponseEntity<CompanyDTOResponse> getCompanyByNit(@PathVariable String nit, Authentication authentication) {

        CompanyDTOResponse companyResponseDTO = companyService.findCompany(nit);
        return new ResponseEntity<>(companyResponseDTO, HttpStatus.OK);
    }
}
