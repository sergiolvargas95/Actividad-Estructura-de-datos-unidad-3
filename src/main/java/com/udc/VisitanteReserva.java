package com.udc;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad principal del sistema de museo.
 * Representa una reserva de visita al museo hecha por un visitante.
 *
 * Atributos:
 *   codigoReserva  - identificador principal (usado en equals/hashCode y Map)
 *   nombreVisitante - nombre de la persona que reserva
 *   tipoVisita      - INDIVIDUAL, GRUPAL, ESCOLAR
 *   fechaVisita     - fecha programada de la visita
 *   cantidadPersonas - número de personas incluidas
 *   estado          - PENDIENTE, PROCESADO, CANCELADO
 */
public class VisitanteReserva {

    private String codigoReserva;
    private String nombreVisitante;
    private String tipoVisita;
    private LocalDate fechaVisita;
    private int cantidadPersonas;
    private EstadoReserva estado;

    public VisitanteReserva(String codigoReserva, String nombreVisitante,
                            String tipoVisita, LocalDate fechaVisita, int cantidadPersonas) {
        this.codigoReserva = codigoReserva;
        this.nombreVisitante = nombreVisitante;
        this.tipoVisita = tipoVisita;
        this.fechaVisita = fechaVisita;
        this.cantidadPersonas = cantidadPersonas;
        this.estado = EstadoReserva.PENDIENTE;
    }

    // Getters
    public String getCodigoReserva()    { return codigoReserva; }
    public String getNombreVisitante()  { return nombreVisitante; }
    public String getTipoVisita()       { return tipoVisita; }
    public LocalDate getFechaVisita()   { return fechaVisita; }
    public int getCantidadPersonas()    { return cantidadPersonas; }
    public EstadoReserva getEstado()    { return estado; }

    // Setters
    public void setCodigoReserva(String codigoReserva)     { this.codigoReserva = codigoReserva; }
    public void setNombreVisitante(String nombreVisitante) { this.nombreVisitante = nombreVisitante; }
    public void setTipoVisita(String tipoVisita)           { this.tipoVisita = tipoVisita; }
    public void setFechaVisita(LocalDate fechaVisita)      { this.fechaVisita = fechaVisita; }
    public void setCantidadPersonas(int cantidadPersonas)  { this.cantidadPersonas = cantidadPersonas; }
    public void setEstado(EstadoReserva estado)            { this.estado = estado; }

    /** equals y hashCode basados en codigoReserva (identificador principal) */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitanteReserva v)) return false;
        return Objects.equals(codigoReserva, v.codigoReserva);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoReserva);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Tipo: %-10s | Fecha: %s | Personas: %2d | Estado: %s",
                codigoReserva, nombreVisitante, tipoVisita, fechaVisita, cantidadPersonas, estado);
    }
}
