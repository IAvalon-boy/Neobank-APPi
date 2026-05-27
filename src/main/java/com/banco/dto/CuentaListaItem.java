package com.banco.dto;

public class CuentaListaItem {

    private String numeroCuenta;
    private java.math.BigDecimal saldo;

    public CuentaListaItem() {
    }

    public CuentaListaItem(String numeroCuenta, java.math.BigDecimal saldo) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public java.math.BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(java.math.BigDecimal saldo) {
        this.saldo = saldo;
    }
}
