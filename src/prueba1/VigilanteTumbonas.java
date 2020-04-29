package prueba1;

import java.util.concurrent.ArrayBlockingQueue;

public class VigilanteTumbonas extends Vigilante {
	
	public VigilanteTumbonas(String id, ArrayBlockingQueue<Visitante> colaEspera, RegistroVisitantes registro) {
		super(id, colaEspera, registro);
	}
	
	public long getTiempoVigilancia() {
        return (long) ((int) (500) + (400 * Math.random()));
    }
	
	public Permiso tipoPermiso(Visitante visitante) {
    	Permiso tipoPermiso = Permiso.NO_PERMITIDO;
    	if (visitante.getEdad() >= 15) {
    		tipoPermiso = Permiso.PERMITIDO;
    	}
        return tipoPermiso;
    }

}
