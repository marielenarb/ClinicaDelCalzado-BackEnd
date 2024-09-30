package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.request.WorkOrderDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.CompanyDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.CompanyListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.CompanyDTO;
import com.ClinicaDelCalzado_BackEnd.entity.Company;
import com.ClinicaDelCalzado_BackEnd.repository.workOrders.ICompanyRepository;
import com.ClinicaDelCalzado_BackEnd.services.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements ICompanyService {

    private final ICompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl (ICompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Optional<Company> findCompanyByNit(String nit) {
        return companyRepository.findByNit(nit);
    }

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public CompanyListDTOResponse findCompanyAll() {

        List<Company> companiesList = companyRepository.findAll();

        CompanyListDTOResponse companyListDTOResponse = new CompanyListDTOResponse();
        companyListDTOResponse.setCompanies(companiesList.stream().map(p ->
                new CompanyDTO(p.getName(), p.getNit(), p.getAddress(), Collections.singletonList(p.getPhones()))).toList());

        return companyListDTOResponse;
    }

    @Override
    public CompanyDTOResponse findCompany(String nit) {

        CompanyDTOResponse companyDTOResponse = new CompanyDTOResponse();
        Optional<Company> company = findCompanyByNit(nit);

        if (company.isEmpty()) {
            throw new NotFoundException(String.format("La compañía con nit %s no esta registrada!!", nit));
        }

        companyDTOResponse.setMessage("Detalles de la compañia recuperados exitosamente.");
        companyDTOResponse.setCompany(company.map(p ->
                new CompanyDTO(p.getName(), p.getNit(), p.getAddress(), Collections.singletonList(p.getPhones()))).get());

        return companyDTOResponse;
    }

    @Override
    public Company findCompanyWorkOrder(WorkOrderDTORequest workOrderDTORequest) {

        return findCompanyByNit(workOrderDTORequest.getCompany().getNit())
                .orElseGet(() -> save(
                        Company.builder()
                                .nit(workOrderDTORequest.getCompany().getNit())
                                .name(workOrderDTORequest.getCompany().getName())
                                .address(workOrderDTORequest.getCompany().getAddress())
                                .phones(String.join(",", workOrderDTORequest.getCompany().getPhones()))
                                .build())
                );
    }
}
