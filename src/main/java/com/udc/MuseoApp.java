package com.udc;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase principal con menú de consola.
 * Responsabilidad: interacción con el usuario, delega toda la lógica a MuseoServicio.
 * Relación: depende de MuseoServicio (composición); usa VisitanteReserva y EstadoReserva.
 */
public class MuseoApp {

    private static final MuseoServicio servicio = new MuseoServicio();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        cargarDatosDePrueba();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerInt("Opción: ");
            System.out.println();
            switch (opcion) {
                case 1  -> registrar();
                case 2  -> servicio.verTodos();
                case 3  -> servicio.verPendientes();
                case 4  -> System.out.println(servicio.procesarSiguiente());
                case 5  -> servicio.verHistorial();
                case 6  -> buscarPorCodigo();
                case 7  -> buscarPorNombre();
                case 8  -> filtrar();
                case 9  -> ordenar();
                case 10 -> servicio.verEstadisticas();
                case 11 -> verAgrupamientos();
                case 12 -> cancelar();
                case 13 -> System.out.println(servicio.deshacerUltimo());
                case 14 -> servicio.verCantidades();
                case 0  -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida.");
            }
            System.out.println();
        } while (opcion != 0);
    }

    // ── Menú ──────────────────────────────────────────────────────────────────
    private static void mostrarMenu() {
        System.out.println("══════ MUSEO · GESTIÓN DE RESERVAS ══════");
        System.out.println(" 1. Registrar reserva");
        System.out.println(" 2. Ver todas las reservas       (List)");
        System.out.println(" 3. Ver pendientes               (Queue)");
        System.out.println(" 4. Procesar siguiente           (Queue→Deque)");
        System.out.println(" 5. Ver historial                (Deque)");
        System.out.println(" 6. Buscar por código            (Map)");
        System.out.println(" 7. Buscar por nombre            (Stream)");
        System.out.println(" 8. Filtrar reservas             (Stream)");
        System.out.println(" 9. Ordenar reservas             (Stream)");
        System.out.println("10. Estadísticas                 (Stream+Map)");
        System.out.println("11. Agrupamientos                (Stream+Map)");
        System.out.println("12. Cancelar pendiente");
        System.out.println("13. Deshacer último procesamiento");
        System.out.println("14. Ver cantidad de elementos");
        System.out.println(" 0. Salir");
        System.out.println("═════════════════════════════════════════");
    }

    private static void registrar() {
        System.out.print("Código reserva   : "); String codigo = sc.nextLine().trim();
        System.out.print("Nombre visitante : "); String nombre = sc.nextLine().trim();
        System.out.print("Tipo (INDIVIDUAL/GRUPAL/ESCOLAR): "); String tipo = sc.nextLine().trim().toUpperCase();
        LocalDate fecha = null;
        while (fecha == null) {
            System.out.print("Fecha visita (YYYY-MM-DD): ");
            try { fecha = LocalDate.parse(sc.nextLine().trim()); }
            catch (DateTimeParseException e) { System.out.println("Formato inválido, intente de nuevo."); }
        }
        System.out.print("Cantidad personas: "); int personas = leerInt("");
        System.out.println(servicio.registrar(new VisitanteReserva(codigo, nombre, tipo, fecha, personas)));
    }

    private static void buscarPorCodigo() {
        System.out.print("Código: ");
        VisitanteReserva r = servicio.buscarPorCodigo(sc.nextLine().trim());
        System.out.println(r != null ? r : "No encontrada.");
    }

    private static void buscarPorNombre() {
        System.out.print("Texto a buscar en nombre: ");
        List<VisitanteReserva> resultados = servicio.buscarPorNombre(sc.nextLine().trim());
        if (resultados.isEmpty()) System.out.println("Sin resultados.");
        else resultados.forEach(System.out::println);
    }

    private static void filtrar() {
        System.out.println("Filtrar por:  1-Estado  2-Tipo de visita");
        int op = leerInt("Opción: ");
        List<VisitanteReserva> resultado;
        if (op == 1) {
            System.out.println("Estado (PENDIENTE / PROCESADO / CANCELADO): ");
            try {
                EstadoReserva e = EstadoReserva.valueOf(sc.nextLine().trim().toUpperCase());
                resultado = servicio.filtrarPorEstado(e);
            } catch (IllegalArgumentException ex) {
                System.out.println("Estado inválido."); return;
            }
        } else {
            System.out.print("Tipo (INDIVIDUAL/GRUPAL/ESCOLAR): ");
            resultado = servicio.filtrarPorTipo(sc.nextLine().trim());
        }
        if (resultado.isEmpty()) System.out.println("Sin resultados.");
        else resultado.forEach(System.out::println);
    }

    private static void ordenar() {
        System.out.println("Ordenar por:  1-Fecha  2-Nombre");
        int op = leerInt("Opción: ");
        List<VisitanteReserva> ordenada = (op == 2)
                ? servicio.ordenarPorNombre()
                : servicio.ordenarPorFecha();
        ordenada.forEach(System.out::println);
    }

    private static void verAgrupamientos() {
        System.out.println("── Por estado ──");
        Map<EstadoReserva, Long> porEstado = servicio.agruparPorEstado();
        porEstado.forEach((e, c) -> System.out.println("  " + e + ": " + c));

        System.out.println("── Por tipo de visita ──");
        Map<String, List<VisitanteReserva>> porTipo = servicio.agruparPorTipo();
        porTipo.forEach((tipo, lista) -> {
            System.out.println("  " + tipo + " (" + lista.size() + "):");
            lista.forEach(r -> System.out.println("    " + r));
        });
    }

    private static void cancelar() {
        System.out.print("Código a cancelar: ");
        System.out.println(servicio.cancelar(sc.nextLine().trim()));
    }

    private static int leerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Ingrese un número."); }
        }
    }

    private static void cargarDatosDePrueba() {
        servicio.registrar(new VisitanteReserva("R001", "Ana García",    "INDIVIDUAL", LocalDate.of(2026,6,10), 1));
        servicio.registrar(new VisitanteReserva("R002", "Colegio Norte", "ESCOLAR",    LocalDate.of(2026,6,11), 30));
        servicio.registrar(new VisitanteReserva("R003", "Pedro Ruiz",    "INDIVIDUAL", LocalDate.of(2026,6,9),  2));
        servicio.registrar(new VisitanteReserva("R004", "Tour Express",  "GRUPAL",     LocalDate.of(2026,6,12), 15));
        servicio.registrar(new VisitanteReserva("R005", "Laura Mora",    "INDIVIDUAL", LocalDate.of(2026,6,8),  1));
    }
}
