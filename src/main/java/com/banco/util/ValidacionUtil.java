package com.banco.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ValidacionUtil {

    private static final String PATRON_DUI = "^\\d{8}-\\d$";
    private static final String PATRON_CUENTA = "^\\d{3}-\\d{6}-\\d{2}$";

    private ValidacionUtil() {
    }

    public static String validarDui(String dui) throws IllegalArgumentException {
        if (dui == null || dui.trim().isEmpty()) {
            throw new IllegalArgumentException("El DUI es obligatorio.");
        }
        String normalizado = dui.trim();
        if (!normalizado.matches(PATRON_DUI)) {
            throw new IllegalArgumentException("Formato de DUI inválido. Use el formato 12345678-9.");
        }
        return normalizado;
    }

    public static String validarNumeroCuenta(String numeroCuenta) throws IllegalArgumentException {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta es obligatorio.");
        }
        String normalizado = numeroCuenta.trim();
        if (!normalizado.matches(PATRON_CUENTA)) {
            throw new IllegalArgumentException("Formato de cuenta inválido. Use el formato 000-000000-00.");
        }
        return normalizado;
    }

    public static BigDecimal validarMonto(BigDecimal monto) throws IllegalArgumentException {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        return monto.setScale(2, RoundingMode.HALF_UP);
    }
}
