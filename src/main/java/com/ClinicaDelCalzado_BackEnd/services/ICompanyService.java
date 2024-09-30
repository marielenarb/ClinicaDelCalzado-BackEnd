package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.CompanyDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.CompanyListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.entity.Company;

import java.util.Optional;

public interface ICompanyService {

    Optional<Company> findCompanyByNit(String nit);
    Company save(Company company);
    CompanyListDTOResponse findCompanyAll();
    CompanyDTOResponse findCompany(String nit);
    Company findCompanyWorkOrder(WorkOrderDTORequest workOrderDTORequest);
}
