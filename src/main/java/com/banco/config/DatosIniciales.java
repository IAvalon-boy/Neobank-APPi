package com.banco.config;

import com.banco.model.Cliente;
import com.banco.model.Cuenta;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Inserta datos de prueba si la base está vacía (compatible con Spring MVC + API REST).
 */
@Component
public class DatosIniciales {

    @PostConstruct
    public void cargarDatosDemo() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                Long total = session.createQuery("select count(c) from Cliente c", Long.class)
                        .uniqueResult();
                if (total != null && total > 0) {
                    return;
                }

                Transaction tx = session.beginTransaction();

                Cliente cliente = new Cliente();
                cliente.setDui("12345678-9");
                cliente.setNombres("Cliente Demo");
                session.persist(cliente);

                Cuenta c1 = new Cuenta();
                c1.setCliente(cliente);
                c1.setNumeroCuenta("000-000001-00");
                c1.setSaldo(new BigDecimal("500.00"));
                session.persist(c1);

                Cuenta c2 = new Cuenta();
                c2.setCliente(cliente);
                c2.setNumeroCuenta("000-000002-00");
                c2.setSaldo(new BigDecimal("120.50"));
                session.persist(c2);

                tx.commit();
            } finally {
                session.close();
            }
        } catch (Exception e) {
            System.err.println("[Neobank] No se pudieron cargar datos demo: " + e.getMessage());
        }
    }
}
