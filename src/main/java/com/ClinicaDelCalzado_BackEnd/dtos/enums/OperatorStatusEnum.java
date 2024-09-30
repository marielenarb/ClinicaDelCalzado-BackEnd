package com.ClinicaDelCalzado_BackEnd.dtos.enums;

import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OperatorStatusEnum {
    ACTIVE("ACTIVE", "ACTIVO"),
    INACTIVE("INACTIVE","INACTIVO");

    final String keyName;
    final String value;

    public static String getValue(String keyName) {
        return Arrays.stream(OperatorStatusEnum.values())
                .filter(x -> x.getKeyName()
                        .equalsIgnoreCase(keyName))
                .findFirst()
                .map(OperatorStatusEnum::getValue)
                .orElse(StringUtils.EMPTY);
    }

    public static String getName(String keyValue) {
        return Arrays.stream(OperatorStatusEnum.values())
                .filter(x -> x.getValue()
                        .equalsIgnoreCase(keyValue))
                .findFirst()
                .map(OperatorStatusEnum::getKeyName)
                .orElseThrow(() -> new BadRequestException(String.format("Operator status %s is invalid", keyValue)));
    }
}
