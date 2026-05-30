package com.banco.service;

import com.banco.config.HibernateUtil;
import com.banco.dto.CuentaListaItem;
import com.banco.dto.MensajeResponse;
import com.banco.dto.TransaccionRequest;
import com.banco.model.Cliente;
import com.banco.model.Cuenta;
import com.banco.util.ValidacionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BancoService {

    public List<CuentaListaItem> listarCuentasPorDui(String dui) throws BancoException {
        String duiNorm;
        try {
            duiNorm = ValidacionUtil.validarDui(dui);
        } catch (IllegalArgumentException e) {
            throw new BancoException(400, e.getMessage());
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Cliente cliente = session.createQuery(
                            "select distinct c from Cliente c left join fetch c.cuentas where c.dui = :dui",
                            Cliente.class)
                    .setParameter("dui", duiNorm)
                    .uniqueResult();

            if (cliente == null) {
                throw new BancoException(404, "No existe un cliente con ese DUI.");
            }

            return cliente.getCuentas().stream()
                    .map(c -> new CuentaListaItem(c.getNumeroCuenta(), c.getSaldo()))
                    .collect(Collectors.toList());
        } catch (BancoException e) {
            throw e;
        } catch (Exception e) {
            throw new BancoException(500, "Error al consultar la base de datos: " + e.getMessage());
        }
    }

    public MensajeResponse abonar(TransaccionRequest body) throws BancoException {
        return procesarMovimiento(body, true);
    }

    public MensajeResponse retirar(TransaccionRequest body) throws BancoException {
        return procesarMovimiento(body, false);
    }

    private MensajeResponse procesarMovimiento(TransaccionRequest body, boolean abono) throws BancoException {
        if (body == null) {
            throw new BancoException(400, "Datos de transacción requeridos.");
        }

        String numero;
        BigDecimal monto;
        try {
            numero = ValidacionUtil.validarNumeroCuenta(body.getNumeroCuenta());
            monto = ValidacionUtil.validarMonto(body.getMonto());
        } catch (IllegalArgumentException e) {
            throw new BancoException(400, e.getMessage());
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            Cuenta cuenta = session.createQuery(
                            "from Cuenta c where c.numeroCuenta = :n",
                            Cuenta.class)
                    .setParameter("n", numero)
                    .uniqueResult();

            if (cuenta == null) {
                tx.rollback();
                throw new BancoException(404, "Cuenta no encontrada.");
            }

            BigDecimal saldo = cuenta.getSaldo();
            if (!abono) {
                if (saldo.compareTo(monto) < 0) {
                    tx.rollback();
                    throw new BancoException(400, "Fondos insuficientes.");
                }
                cuenta.setSaldo(saldo.subtract(monto));
            } else {
                cuenta.setSaldo(saldo.add(monto));
            }

            tx.commit();

            String mensaje = abono
                    ? "Depósito realizado. Nuevo saldo: " + cuenta.getSaldo()
                    : "Retiro realizado. Nuevo saldo: " + cuenta.getSaldo();
            return new MensajeResponse(mensaje);
        } catch (BancoException e) {
            throw e;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new BancoException(500, "Error al procesar la operación: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}
