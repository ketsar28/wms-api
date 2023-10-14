package com.enigma.wms_api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ETransactionType {
    EAT_IN("Eat In"),
    ONLINE("Online"),
    TAKE_AWAY("Take Away");

    private String name;

    public static ETransactionType getType(String value) {
        return Arrays.stream(values()).filter(eTransactionType -> eTransactionType.name.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(ETransactionType.class)));
    }

    public static ETransactionType getTypeNumber(String number) {
        try {
            return Arrays.stream(values()).filter(eTransactionType -> eTransactionType.ordinal() == Integer.parseInt(number) - 1)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.getNotFoundResourceMessage(ETransactionType.class)));
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid Transaction Type");
        }
    }

}
