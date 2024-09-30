package com.ClinicaDelCalzado_BackEnd.dtos.enums;

import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ServicesStatusEnum {
    RECEIVED("RECEIVED","RECIBIDO"),
    FINISHED("FINISHED","TERMINADO"),
    DISPATCHED("DISPATCHED","DESPACHADO");

    final String keyName;
    final String value;

    public static String getValue(String keyName) {
        return Arrays.stream(ServicesStatusEnum.values())
                .filter(x -> x.name()
                        .equalsIgnoreCase(keyName))
                .findFirst()
                .map(ServicesStatusEnum::getValue)
                .orElse(StringUtils.EMPTY);
    }

    public static String getName(String keyValue) {
        return Arrays.stream(ServicesStatusEnum.values())
                .filter(x -> x.getValue()
                        .equalsIgnoreCase(keyValue))
                .findFirst()
                .map(ServicesStatusEnum::getKeyName)
                .orElseThrow(() -> new BadRequestException(String.format("Services status %s is invalid", keyValue)));
    }
}
