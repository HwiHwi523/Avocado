package com.avocado.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE"),
    INVALID_INSERT(500, "INVALID_SAVE"),
    INVALID_UPDATE(500, "INVALID_UPDATE"),
    INVALID_DELETE(500, "INVALID_DELETE"),

    // Consumer
    NO_MEMBER(404, "NO_MEMBER"),

    // Merchandise
    NO_MERCHANDISE(404, "NO_MERCHANDISE"),

    // Purchasing
    NO_PURCHASING(500, "NO_WAIT_PURCHASE"),

    // Pay
    NOT_SAME_WITH_TOTAL_PRICE(400, "NOT_SAME_WITH_TOTAL_PRICE"),
    READY_ERROR(500, "READY_ERROR"),
    APPROVE_ERROR(500, "APPROVE_ERROR"),
    ;

    final int status;
    final String message;
}
