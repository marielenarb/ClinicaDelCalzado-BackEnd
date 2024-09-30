package com.ClinicaDelCalzado_BackEnd.dtos.enums;

import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PaymentStatusEnum {
    PENDING("PENDING","PENDIENTE"),
    PAID("PAID","PAGADO");

    final String keyName;
    final String value;

    public static String getValue(String keyName) {
        return Arrays.stream(PaymentStatusEnum.values())
                .filter(x -> x.name()
                        .equalsIgnoreCase(keyName))
                .findFirst()
                .map(PaymentStatusEnum::getValue)
                .orElse(StringUtils.EMPTY);
    }

    public static String getName(String keyValue) {
        return Arrays.stream(PaymentStatusEnum.values())
                .filter(x -> x.getValue()
                        .equalsIgnoreCase(keyValue))
                .findFirst()
                .map(PaymentStatusEnum::getKeyName)
                .orElseThrow(() -> new BadRequestException(String.format("Payment status %s is invalid", keyValue)));
    }
}
