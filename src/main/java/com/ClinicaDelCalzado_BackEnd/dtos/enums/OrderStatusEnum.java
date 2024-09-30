package com.ClinicaDelCalzado_BackEnd.dtos.enums;

import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OrderStatusEnum {
    VALID("VALID", "VIGENTE"),
    FINISHED("FINISHED", "TERMINADA"),
    CANCELED("CANCELED", "ANULADA");

    final String keyName;
    final String value;

    public static String getValue(String keyName) {
        return Arrays.stream(OrderStatusEnum.values())
                .filter(x -> x.name()
                        .equalsIgnoreCase(keyName))
                .findFirst()
                .map(OrderStatusEnum::getValue)
                .orElse(StringUtils.EMPTY);
    }

    public static String getName(String keyValue) {
        return Arrays.stream(OrderStatusEnum.values())
                .filter(x -> x.getValue()
                        .equalsIgnoreCase(keyValue))
                .findFirst()
                .map(OrderStatusEnum::getKeyName)
                .orElseThrow(() -> new BadRequestException(String.format("Order status %s is invalid", keyValue)));
    }
}
