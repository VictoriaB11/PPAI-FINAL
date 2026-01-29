package org.example.Main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.Modelos.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Iniciar JPA
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ppaiPU");
        EntityManager em = emf.createEntityManager();

        try {
            System.out.println("=== INICIANDO CONFIGURACIÓN DE DATOS DE PRUEBA ===");
            em.getTransaction().begin();

            // ---------------------------------------------------------
            // PASO 1: Crear Datos Maestros (Rol, Empleado)
            // ---------------------------------------------------------
            // Asumo que tienes una clase Rol. Si no tienes constructor, ajusta esto.
            Rol rolTecnico = new Rol();
            rolTecnico.setNombre("Tecnico");
            rolTecnico.setDescripcion("Responsable de Reparacion");
            em.persist(rolTecnico);

            Empleado empleado = new Empleado("Gomez", "Juan", "juan@ppai.com", "123456", rolTecnico);
            em.persist(empleado);

            // ---------------------------------------------------------
            // PASO 2: Crear el Estado Inicial (InhabilitadoPorInspeccion)
            // ---------------------------------------------------------
            // Este estado suele existir en la BD, así que lo creamos y persistimos primero.
            EstadoSismografo estadoInhabilitado = new InhabilitadoPorInspeccion();
            em.persist(estadoInhabilitado);

            // ---------------------------------------------------------
            // PASO 3: Crear Sismógrafo con Historial Inicial
            // ---------------------------------------------------------
            Sismografo sismografo = new Sismografo(LocalDate.now(), 1001, 555, null);

            // Simulamos que el sismógrafo ya estaba en estado "Inhabilitado..." desde ayer
            CambioEstado cambioInicial = new CambioEstado(
                    estadoInhabilitado,
                    LocalDateTime.now().minusDays(1),
                    null, // Fecha fin null = es el actual
                    empleado
            );

            // Usamos el método del sismógrafo para vincular correctamente
            sismografo.agregarCambioEstado(cambioInicial);

            // Guardamos el sismógrafo (por CascadeType.ALL guardará el CambioEstado)
            em.persist(sismografo);

            em.getTransaction().commit();
            System.out.println("Datos iniciales guardados. ID Sismógrafo: " + sismografo.getId());
            System.out.println("Estado Actual Inicial: " + sismografo.getEstadoActual().getEstado().getNombre());

            // ---------------------------------------------------------
            // PASO 4: PROBAR LA LÓGICA DE NEGOCIO (Poner en Reparación)
            // ---------------------------------------------------------
            System.out.println("\n=== EJECUTANDO ponerEnReparacion() ===");
            em.getTransaction().begin();

            // Recuperamos el sismógrafo (aunque ya lo tenemos en memoria, es buena práctica asegurar que está 'managed')
            Sismografo sismoManaged = em.find(Sismografo.class, sismografo.getId());

            List<MotivoFueraDeServicio> motivos = new ArrayList<>();
            // (Opcional) Podrías agregar motivos aquí si tuvieras la clase Motivo lista

            // --- LA LLAMADA MÁGICA ---
            // Esto debería:
            // 1. Cerrar el cambio de estado anterior (poner fecha fin).
            // 2. Crear una instancia de FueraDeServicio.
            // 3. Crear un nuevo CambioEstado.
            // 4. Agregarlo al historial.
            sismoManaged.ponerEnReparacion(LocalDateTime.now(), motivos, empleado);

            // Hacemos merge/commit para guardar los cambios en la BD
            em.merge(sismoManaged);
            em.getTransaction().commit();

            System.out.println("Transacción completada con éxito.");

            // ---------------------------------------------------------
            // PASO 5: VERIFICACIÓN
            // ---------------------------------------------------------
            System.out.println("\n=== VERIFICANDO RESULTADOS ===");

            // Limpiamos la caché del EntityManager para traer datos frescos de la BD
            em.clear();
            Sismografo sismoVerificado = em.find(Sismografo.class, sismografo.getId());

            List<CambioEstado> historial = sismoVerificado.getHistorialEstados();
            System.out.println("Cantidad de cambios en el historial: " + historial.size() + " (Deberían ser 2)");

            CambioEstado ultimo = sismoVerificado.getEstadoActual();
            System.out.println("Nuevo Estado Actual: " + ultimo.getEstado().getNombre());
            System.out.println("Clase del Estado: " + ultimo.getEstado().getClass().getSimpleName());

            if (ultimo.getEstado() instanceof FueraDeServicio) {
                System.out.println("✅ ÉXITO: El estado ahora es FueraDeServicio.");
            } else {
                System.out.println("❌ ERROR: El estado no es el esperado.");
            }

            // Verificar que el anterior se cerró
            for (CambioEstado ce : historial) {
                if (!ce.equals(ultimo)) {
                    System.out.println("Estado anterior (" + ce.getEstado().getNombre() + ") Fecha Fin: " + ce.getFechaHoraFin());
                    if (ce.getFechaHoraFin() != null) {
                        System.out.println("✅ El estado anterior fue cerrado correctamente.");
                    } else {
                        System.out.println("❌ El estado anterior NO tiene fecha de fin.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
            emf.close();
        }
    }
}