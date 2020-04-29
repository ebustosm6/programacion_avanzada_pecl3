/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba1;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author vic88
 */
public class VigilantePiscinaNinos extends Vigilante {
    
    public VigilantePiscinaNinos(String id, ArrayBlockingQueue<Visitante> colaEspera, RegistroVisitantes registro) {
		super(id, colaEspera, registro);
	}
    
    public long getTiempoVigilancia() {
        return (long) ((int) (1000) + (500 * Math.random()));
    }
    
    public Permiso tipoPermiso(Visitante visitante) {
    	Permiso tipoPermiso = Permiso.NO_PERMITIDO;
    	if (visitante.getEdad() >= 1 && visitante.getEdad() <= 5) {
    		tipoPermiso = Permiso.CON_ACOMPANIANTE;
    	} else if (visitante.getEdad() >= 6 && visitante.getEdad() <= 10) {
    		tipoPermiso = Permiso.PERMITIDO;
    	}
        return tipoPermiso;
    }
    
}
