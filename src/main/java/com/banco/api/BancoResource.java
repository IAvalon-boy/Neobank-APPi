package com.banco.api;

import com.banco.config.HibernateUtil;
import com.banco.dto.CuentaListaItem;
import com.banco.dto.ErrorResponse;
import com.banco.dto.MensajeResponse;
import com.banco.dto.TransaccionRequest;
import com.banco.model.Cliente;
import com.banco.model.Cuenta;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Operaciones del dependiente: listar cuentas por DUI, abonar y retirar efectivo.
 */
@Path("/")
public class BancoResource {

    @GET
    @Path("cuentas/{dui}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCuentasPorDui(@PathParam("dui") String dui) {
        if (dui == null || dui.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("DUI requerido."))
                    .build();
        }
        String duiNorm = dui.trim();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Cliente cliente = session.createQuery(
                            "select distinct c from Cliente c left join fetch c.cuentas where c.dui = :dui",
                            Cliente.class)
                    .setParameter("dui", duiNorm)
                    .uniqueResult();

            if (cliente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No existe un cliente con ese DUI."))
                        .build();
            }

            List<CuentaListaItem> items = cliente.getCuentas().stream()
                    .map(c -> new CuentaListaItem(c.getNumeroCuenta(), c.getSaldo()))
                    .collect(Collectors.toList());

            return Response.ok(items).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al consultar la base de datos: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("abonarefectivo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response abonar(TransaccionRequest body) {
        return procesarMovimiento(body, true);
    }

    @POST
    @Path("retirarefectivo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retirar(TransaccionRequest body) {
        return procesarMovimiento(body, false);
    }

    private Response procesarMovimiento(TransaccionRequest body, boolean abono) {
        if (body == null || body.getNumeroCuenta() == null || body.getNumeroCuenta().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Número de cuenta requerido."))
                    .build();
        }
        if (body.getMonto() == null || body.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("El monto debe ser mayor a cero."))
                    .build();
        }

        BigDecimal monto = body.getMonto().setScale(2, RoundingMode.HALF_UP);
        String numero = body.getNumeroCuenta().trim();

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
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Cuenta no encontrada."))
                        .build();
            }

            BigDecimal saldo = cuenta.getSaldo();
            if (!abono) {
                if (saldo.compareTo(monto) < 0) {
                    tx.rollback();
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Fondos insuficientes."))
                            .build();
                }
                cuenta.setSaldo(saldo.subtract(monto));
            } else {
                cuenta.setSaldo(saldo.add(monto));
            }

            tx.commit();

            String mensaje = abono
                    ? "Depósito realizado. Nuevo saldo: " + cuenta.getSaldo().setScale(2, RoundingMode.HALF_UP)
                    : "Retiro realizado. Nuevo saldo: " + cuenta.getSaldo().setScale(2, RoundingMode.HALF_UP);
            return Response.ok(new MensajeResponse(mensaje)).build();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al procesar la operación: " + e.getMessage()))
                    .build();
        } finally {
            session.close();
        }
    }
}
