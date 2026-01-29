package org.example.Persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final String PU_NAME = "ppaiPU";
    private static EntityManagerFactory emf;

    // Inicializa la fábrica una sola vez
    public static void init() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(PU_NAME);
        }
    }

    // Devuelve un EntityManager nuevo (una "sesión" de DB)
    public static EntityManager getEntityManager() {
        init();
        return emf.createEntityManager();
    }

    // Cierra la fábrica al final del programa
    public static void close() {
        if (emf != null) {
            emf.close();
            emf = null;
        }
    }
}
