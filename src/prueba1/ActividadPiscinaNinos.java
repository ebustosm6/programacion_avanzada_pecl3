/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba1;

/**
 *
 * @author vic88
 */
public class ActividadPiscinaNinos extends Actividad {

    private static int CAPACIDAD = 15;
    private static String IDENTIFICADOR = "ActividadPiscinaNinos";

    public ActividadPiscinaNinos(RegistroVisitantes registro) {
        super(IDENTIFICADOR, CAPACIDAD, registro);
    }

    public Vigilante iniciarVigilante() {
        Vigilante vigilante = new VigilantePiscinaNinos("VigilantePiscinaNinos", getColaEspera(), getRegistro());
    	getRegistro().aniadirMonitorEnZona(getIdentificador(), "-monitor", vigilante.getIdentificador());
        return vigilante;
    }

    public long getTiempoActividad() {
        return (long) ((int) (1000) + (2000 * Math.random()));
    }

}
