package com.udc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase de servicio / gestor del sistema de museo.
 *
 * Responsabilidad: centraliza toda la lógica de negocio y manejo de colecciones.
 *
 * Colecciones utilizadas:
 *   List<VisitanteReserva>  lista      - registro general de TODAS las reservas
 *   Queue<VisitanteReserva> pendientes - cola FIFO de reservas por procesar
 *   Deque<VisitanteReserva> historial  - pila LIFO de reservas ya procesadas
 *   Map<String, VisitanteReserva> mapa - búsqueda rápida por codigoReserva
 *
 * Flujo de información:
 *   registrar()  → lista + pendientes (Queue) + mapa
 *   procesar()   → poll() de Queue → push() en Deque, estado = PROCESADO
 *   deshacer()   → pop() de Deque  → offer() en Queue, estado = PENDIENTE
 *   cancelar()   → removeIf() en Queue, estado = CANCELADO (permanece en lista y mapa)
 */
public class MuseoServicio {

    // ── Colecciones SDK ────────────────────────────────────────────────────────
    private final List<VisitanteReserva>         lista      = new ArrayList<>();
    private final Queue<VisitanteReserva>        pendientes = new LinkedList<>();
    private final Deque<VisitanteReserva>        historial  = new ArrayDeque<>();
    private final Map<String, VisitanteReserva>  mapa       = new HashMap<>();

    // ── 1. Registrar ──────────────────────────────────────────────────────────
    /**
     * Registra una reserva nueva.
     * - add() en List  → registro general
     * - offer() en Queue → cola de pendientes (FIFO)
     * - put() en Map   → búsqueda rápida
     */
    public String registrar(VisitanteReserva r) {
        if (mapa.containsKey(r.getCodigoReserva()))
            return "Error: ya existe una reserva con código " + r.getCodigoReserva();
        lista.add(r);
        pendientes.offer(r);
        mapa.put(r.getCodigoReserva(), r);
        return "Reserva registrada: " + r.getCodigoReserva();
    }

    // ── 2. Ver todos ──────────────────────────────────────────────────────────
    /** Recorre la List con forEach. */
    public void verTodos() {
        if (lista.isEmpty()) { System.out.println("  Sin registros."); return; }
        lista.forEach(System.out::println);
    }

    // ── 3. Ver pendientes ─────────────────────────────────────────────────────
    /** Recorre la Queue con stream (no destructivo). */
    public void verPendientes() {
        if (pendientes.isEmpty()) { System.out.println("  Sin pendientes."); return; }
        pendientes.stream().forEach(System.out::println);
    }

    // ── 4. Procesar siguiente ─────────────────────────────────────────────────
    /**
     * FIFO: poll() saca el primero de Queue.
     * LIFO: push() apila en Deque.
     */
    public String procesarSiguiente() {
        VisitanteReserva r = pendientes.poll();         // FIFO
        if (r == null) return "No hay reservas pendientes.";
        r.setEstado(EstadoReserva.PROCESADO);
        historial.push(r);                              // LIFO
        return "Procesada: " + r.getCodigoReserva();
    }

    // ── 5. Ver historial ──────────────────────────────────────────────────────
    /** Recorre el Deque con stream (últimos procesados primero). */
    public void verHistorial() {
        if (historial.isEmpty()) { System.out.println("  Historial vacío."); return; }
        historial.stream().forEach(System.out::println);
    }

    // ── 6. Buscar por código (Map) ────────────────────────────────────────────
    public VisitanteReserva buscarPorCodigo(String codigo) {
        return mapa.get(codigo);
    }

    // ── 7. Buscar por nombre (Stream) ─────────────────────────────────────────
    public List<VisitanteReserva> buscarPorNombre(String texto) {
        return lista.stream()
                .filter(r -> r.getNombreVisitante().toLowerCase()
                              .contains(texto.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ── 8. Filtrar por estado (Stream) ────────────────────────────────────────
    public List<VisitanteReserva> filtrarPorEstado(EstadoReserva estado) {
        return lista.stream()
                .filter(r -> r.getEstado() == estado)
                .collect(Collectors.toList());
    }

    // ── 8b. Filtrar por tipo de visita (Stream) ───────────────────────────────
    public List<VisitanteReserva> filtrarPorTipo(String tipo) {
        return lista.stream()
                .filter(r -> r.getTipoVisita().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    // ── 9. Ordenar (Stream) ───────────────────────────────────────────────────
    public List<VisitanteReserva> ordenarPorFecha() {
        return lista.stream()
                .sorted(Comparator.comparing(VisitanteReserva::getFechaVisita))
                .collect(Collectors.toList());
    }

    public List<VisitanteReserva> ordenarPorNombre() {
        return lista.stream()
                .sorted(Comparator.comparing(VisitanteReserva::getNombreVisitante))
                .collect(Collectors.toList());
    }

    // ── 10. Estadísticas (Stream + Map) ──────────────────────────────────────
    public void verEstadisticas() {
        System.out.println("  Total registradas  : " + lista.size());
        System.out.println("  Pendientes (Queue) : " + pendientes.size());
        System.out.println("  Procesadas (Deque) : " + historial.size());

        // count() con Stream
        long canceladas = lista.stream()
                .filter(r -> r.getEstado() == EstadoReserva.CANCELADO)
                .count();
        System.out.println("  Canceladas         : " + canceladas);

        // Total personas con Stream
        int totalPersonas = lista.stream()
                .mapToInt(VisitanteReserva::getCantidadPersonas)
                .sum();
        System.out.println("  Total personas     : " + totalPersonas);

        // anyMatch / allMatch / noneMatch
        boolean hayGrupal = lista.stream().anyMatch(r -> r.getTipoVisita().equalsIgnoreCase("GRUPAL"));
        boolean todosPend  = lista.stream().allMatch(r -> r.getEstado() == EstadoReserva.PENDIENTE);
        boolean sinCancel  = lista.stream().noneMatch(r -> r.getEstado() == EstadoReserva.CANCELADO);
        System.out.println("  ¿Hay grupales?     : " + hayGrupal);
        System.out.println("  ¿Todos pendientes? : " + todosPend);
        System.out.println("  ¿Sin cancelados?   : " + sinCancel);
    }

    // ── 11. Agrupamientos (Stream + Collectors) ───────────────────────────────
    /** groupingBy estado → counting */
    public Map<EstadoReserva, Long> agruparPorEstado() {
        return lista.stream()
                .collect(Collectors.groupingBy(VisitanteReserva::getEstado, Collectors.counting()));
    }

    /** groupingBy tipo de visita */
    public Map<String, List<VisitanteReserva>> agruparPorTipo() {
        return lista.stream()
                .collect(Collectors.groupingBy(VisitanteReserva::getTipoVisita));
    }

    /** Collectors.toMap: reconstruir índice desde lista */
    public Map<String, VisitanteReserva> indiceDesdeLista() {
        return lista.stream()
                .collect(Collectors.toMap(
                        VisitanteReserva::getCodigoReserva,
                        r -> r,
                        (a, b) -> a));   // manejo de colisión: conserva el primero
    }

    // ── 12. Cancelar pendiente ────────────────────────────────────────────────
    /**
     * Solo cancela si estado == PENDIENTE.
     * removeIf() en Queue elimina el elemento.
     * Permanece en List y Map (evidencia del registro).
     */
    public String cancelar(String codigo) {
        VisitanteReserva r = mapa.get(codigo);
        if (r == null) return "No existe reserva con código " + codigo;
        if (r.getEstado() != EstadoReserva.PENDIENTE)
            return "Solo se pueden cancelar reservas PENDIENTES. Estado actual: " + r.getEstado();
        r.setEstado(EstadoReserva.CANCELADO);
        pendientes.removeIf(x -> x.getCodigoReserva().equals(codigo));
        return "Reserva cancelada: " + codigo;
    }

    // ── 13. Deshacer último procesamiento ─────────────────────────────────────
    /**
     * pop() saca el tope del Deque (LIFO).
     * offer() lo reingresa a la Queue.
     * Estado regresa a PENDIENTE.
     */
    public String deshacerUltimo() {
        VisitanteReserva r = historial.poll();          // peek/pop del tope
        if (r == null) return "El historial está vacío.";
        r.setEstado(EstadoReserva.PENDIENTE);
        pendientes.offer(r);
        return "Deshecho: " + r.getCodigoReserva() + " → regresó a pendientes.";
    }

    // ── 14. Cantidad ──────────────────────────────────────────────────────────
    public void verCantidades() {
        System.out.println("  Registradas: " + lista.size());
        System.out.println("  En cola    : " + pendientes.size());
        System.out.println("  Historial  : " + historial.size());
        System.out.println("  En mapa    : " + mapa.size());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public boolean estaVacia() { return lista.isEmpty(); }

    /** peek() no destructivo sobre Queue */
    public VisitanteReserva proximoPendiente() { return pendientes.peek(); }

    /** peek() no destructivo sobre Deque */
    public VisitanteReserva ultimoProcesado() { return historial.peek(); }
}
