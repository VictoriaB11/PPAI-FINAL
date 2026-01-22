/**
 * 2. Su trabajo es Recibir esos datos iniciales y guardarlos en sus atributos.
 * Esta clase es la BOUNDARY (pantalla) que inicia el Caso de Uso 37.
 */
package org.example.Vistas;

import org.example.Gestores.GestorRI;
import org.example.Modelos.*; // Importa todos los modelos para tenerlos disponibles

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MenuPrincipal extends JFrame {
    // --- Componentes visuales ---
    private JPanel panelPrincipal;
    private JButton btnCerrarOrdenInspeccion;

    // --- Estado interno de la pantalla ---
    // Guarda TODO lo que la cátedra nos da “ya disponible” para el CU.
    // La pantalla los conserva y luego se los pasa al Gestor en el PASO 1.
    private Sesion sesionActual;
    private List<OrdenDeInspeccion> todasLasOrdenes;
    private List<MotivoTipo> motivosDisponibles;
    private List<Estado> estadosDisponibles;
    private List<Sismografo> sismografosDisponibles;

    // El constructor recibe los DATOS (no el Gestor).
    public MenuPrincipal(Sesion sesion, List<OrdenDeInspeccion> ordenes, List<MotivoTipo> motivos, List<Estado> estados, List<Sismografo> sismografos) {

        // Persistimos los datos iniciales en atributos de la pantalla.
        // Esto permite que, al iniciar el CU, podamos inyectarlos al Gestor
        this.sesionActual = sesion;
        this.todasLasOrdenes = ordenes;
        this.motivosDisponibles = motivos;
        this.estadosDisponibles = estados;
        this.sismografosDisponibles = sismografos;

        // --- Configuración estándar de la ventana Swing ---
        setTitle("Menú Principal - Red Sísmica");
        setContentPane(panelPrincipal); // Usa el panel del diseñador
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Ajusta el tamaño automáticamente
        setLocationRelativeTo(null); // Centra la ventana
        setVisible(true); // la hace visible


        // --- WIRING DEL BOTÓN: INICIO DEL CASO DE USO (PASO 1) ---
        // Cuando el actor RI hace click en “Cerrar Orden de Inspección”,
        // la pantalla dispara el método que inicia el CU.
        btnCerrarOrdenInspeccion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opcionCerrarOrdenInspeccion(); // → PASO 1 (actor presiona opción)
            }
        });
    }

    /**
     * PASO 1 (Boundary): método que reacciona al click del usuario.
     * Solo delega en abrirVentana(), que crea el Gestor y le inicia el CU.
     */
    private void opcionCerrarOrdenInspeccion() {
        System.out.println("Pantalla: Se ha presionado la opción para cerrar la orden.");
        // Llamamos al nuevo metodo que se encargará de todo el proceso.
        this.abrirVentana();
    }

    private void abrirVentana() {
        //Crear gestor (Controller)
        GestorRI gestor = new GestorRI();

        // Iniciar el CU del lado del Gestor con los datos base → FIN PASO 1
        gestor.nuevoCierreOrdenInspeccion(this.sesionActual, this.todasLasOrdenes, this.sismografosDisponibles);

        // Parametrizar listas de soporte que usará el Gestor en pasos posteriores
        gestor.setEstadosDisponibles(this.estadosDisponibles);
        gestor.setMotivosDisponibles(this.motivosDisponibles);

        // Paso 6: obtener motivos desde el gestor
        //Se obtiene la lista de motivos disponibles desde el gestor
        List<MotivoTipo> motivosGestor = gestor.buscarTiposDeMotivos();


        // PASO 2: La Boundary “dispara” la búsqueda.
        // El Gestor filtra y ordena, y además hará el callback a mostrarOrdenesDeInspeccionRealizadas(...).
        List<OrdenDeInspeccion> ordenesAMostrar = gestor.buscarOrdenesDeInspeccionRealizadas();

        // Paso 2: Si hay resultados, abrimos directamente la pantalla de selección (inicio Paso 3).
        // (Si NO hay resultados, el callback ya mostró el JOptionPane y esto no se ejecuta).
        if (!ordenesAMostrar.isEmpty()) {
            new SeleccionOrdenDeInspeccion(gestor, motivosGestor, estadosDisponibles);
        }
    }
} //FIN PASO 2

