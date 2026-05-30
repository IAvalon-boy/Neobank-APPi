package com.banco.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * SessionFactory singleton (Hibernate + JDBC MySQL). Lee {@code hibernate.cfg.xml}
 * y permite sobreescribir credenciales con variables de entorno (MYSQL_USER, MYSQL_PASSWORD).
 */
public final class HibernateUtil {

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        SessionFactory factory = sessionFactory;
        if (factory == null) {
            synchronized (HibernateUtil.class) {
                factory = sessionFactory;
                if (factory == null) {
                    sessionFactory = factory = buildSessionFactory();
                }
            }
        }
        return factory;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver MySQL no encontrado en el classpath.", e);
        }

        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder().configure();
        applyEnvSetting(registryBuilder, "hibernate.connection.username", "MYSQL_USER");
        applyEnvSetting(registryBuilder, "hibernate.connection.password", "MYSQL_PASSWORD");

        final StandardServiceRegistry registry = registryBuilder.build();
        try {
            return new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new IllegalStateException(
                    "No se pudo conectar a MySQL (localhost:3306/banco_neobank). "
                            + "Ejecuta setup-db y revisa config.local.ps1. Detalle: " + e.getMessage(), e);
        }
    }

    private static void applyEnvSetting(StandardServiceRegistryBuilder builder, String hibernateKey, String envKey) {
        String value = System.getenv(envKey);
        if (value != null && !value.trim().isEmpty()) {
            builder.applySetting(hibernateKey, value.trim());
        }
    }
}
