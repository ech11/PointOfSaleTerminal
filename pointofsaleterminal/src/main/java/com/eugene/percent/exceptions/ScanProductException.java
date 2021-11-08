package com.eugene.percent.exceptions;

import com.eugene.percent.model.Product;

/**
 * Exception when scanning a {@link Product}.
 */
public class ScanProductException extends Exception {
    public ScanProductException() {
    }

    public ScanProductException(String message) {
        super(message);
    }

    public ScanProductException(Throwable cause) {
        super(cause);
    }

    public ScanProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
