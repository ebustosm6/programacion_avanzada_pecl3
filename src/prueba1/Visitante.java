package prueba1;

import java.io.Serializable;
import java.util.List;

public class Visitante extends Thread implements Serializable{

    private String identificador;
    private int edad;
    private Visitante acompaniante;
    private ParqueAcuatico parque;
    private String actividadActual = "Exterior";
    private int conteoActividades = 0;
	private List<Actividad> actividades;
    private Permiso permisoActividad = Permiso.NO_ESPECIFICADO;
    private TicketTobogan ticket;
            

    public Visitante(String identificador, int edad, Visitante acompaniante, ParqueAcuatico parque) {
        this.identificador = identificador;
        this.edad = edad;
        this.acompaniante = acompaniante;
        this.parque = parque;
        this.ticket = null;
    }

    public TicketTobogan getTicket() {
        return ticket;
    }

    public void setTicket(TicketTobogan ticket) {
        this.ticket = ticket;
    }

    public String toString() {
        return this.identificador;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Visitante getAcompaniante() {
        return acompaniante;
    }

    public void setAcompaniante(Visitante acompaniante) {
        this.acompaniante = acompaniante;
    }
    
    public String getActividadActual() {
		return actividadActual;
	}

	public void setActividadActual(String actividadActual) {
		this.actividadActual = actividadActual;
	}
	
    public int getConteoActividades() {
		return conteoActividades;
	}

	public void aniadirConteoActividades() {
		this.conteoActividades++;
	}

	public ParqueAcuatico getParque() {
        return parque;
    }

    public void setParque(ParqueAcuatico parque) {
        this.parque = parque;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }
    
    public synchronized Permiso getPermisoActividad() {
        return permisoActividad;
    }

    public synchronized void setPermisoActividad(Permiso permiso) {
        this.permisoActividad = permiso;
    }

}
