package org.example.Persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // Nombre de la unidad de persistencia (debe coincidir con persistence.xml)
    private static final String PERSISTENCE_UNIT_NAME = "ppaiPU";

    private static EntityManagerFactory factory;

    // Bloque estático para inicializar la fábrica una sola vez
    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            System.err.println("Error al inicializar EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Obtiene un EntityManager nuevo.
     */
    public static EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory.createEntityManager();
    }

    /**
     * Cierra la fábrica de EntityManagers.
     * Este es el método que te faltaba.
     */
    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            System.out.println("EntityManagerFactory cerrado correctamente.");
        }
    }
}