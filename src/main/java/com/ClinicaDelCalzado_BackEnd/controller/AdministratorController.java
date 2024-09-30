package com.ClinicaDelCalzado_BackEnd.controller;

import com.ClinicaDelCalzado_BackEnd.dtos.request.AdminDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateAdminDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateAdminPasswordDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateAdminQuestionDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.response.AdminDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.AdminListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.UpdateAdminPasswordDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.UpdateAdminQuestionDTOResponse;
import com.ClinicaDelCalzado_BackEnd.exceptions.UnauthorizedException;
import com.ClinicaDelCalzado_BackEnd.services.IAdminService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admins")
public class AdministratorController {

    @Autowired
    private IAdminService adminService;

    @PostMapping("/created")
    public ResponseEntity<AdminDTOResponse> createAdministrator(@RequestBody AdminDTORequest adminDTORequest, Authentication authentication) {

        AdminDTOResponse responseDTO = adminService.create(adminDTORequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/updated/{adminId}")
    public ResponseEntity<AdminDTOResponse> updateAdministrator(@PathVariable Long adminId,
                                                                @RequestBody UpdateAdminDTORequest adminDTORequest, Authentication authentication) {

        Long userId = getUserAuth(authentication);
        AdminDTOResponse responseDTO = adminService.update(adminId, adminDTORequest, userId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<AdminListDTOResponse> getAllAdministrator(Authentication authentication) {

        AdminListDTOResponse responseDTO = adminService.findAdministratorAll();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminDTOResponse> getAdministratorById(@PathVariable Long adminId, Authentication authentication) {

        AdminDTOResponse responseDTO = adminService.findAdministratorByIdAdmin(adminId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/password/{adminId}")
    public ResponseEntity<UpdateAdminPasswordDTOResponse> updatePasswordAdministrator(@PathVariable Long adminId,
                                                                                      @RequestBody UpdateAdminPasswordDTO adminDTORequest, Authentication authentication) {

        UpdateAdminPasswordDTOResponse responseDTO = adminService.updatePassword(adminId, adminDTORequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/edit-personal-information/{adminId}")
    public ResponseEntity<UpdateAdminQuestionDTOResponse> updatePersonalInformation(@PathVariable Long adminId,
                                                                                    @RequestBody UpdateAdminQuestionDTO adminDTORequest, Authentication authentication) {

        UpdateAdminQuestionDTOResponse responseDTO = adminService.updateAnswer(adminId, adminDTORequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    private Long getUserAuth(Authentication authentication) {
        if (ObjectUtils.isEmpty(authentication.getPrincipal())) {
            throw new UnauthorizedException("La sesión ha caducado o no esta autorizado para realizar esta acción");
        }
        return Long.valueOf(((User) authentication.getPrincipal()).getUsername());
    }

}
