package com.ClinicaDelCalzado_BackEnd.dtos.enums;

import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AdminTypeEnum {
    PRINCIPAL("PRINCIPAL", "PRINCIPAL"),
    SECONDARY("SECONDARY", "SECUNDARIO"),
    ADMINISTRATOR("ADMINISTRATOR", "ADMINISTRADOR");

    final String keyName;
    final String value;

    public static String getValue(String keyName) {
        return Arrays.stream(AdminTypeEnum.values())
                .filter(x -> x.getKeyName()
                        .equalsIgnoreCase(keyName))
                .findFirst()
                .map(AdminTypeEnum::getValue)
                .orElseThrow(() -> new BadRequestException(String.format("Administrator role type %s is invalid", keyName)));
    }

    public static String getName(String value) {
        return Arrays.stream(AdminTypeEnum.values())
                .filter(x -> x.getValue()
                        .equalsIgnoreCase(value))
                .findFirst()
                .map(AdminTypeEnum::getKeyName)
                .orElseThrow(() -> new BadRequestException(String.format("Administrator role type %s is invalid", value)));
    }
}
