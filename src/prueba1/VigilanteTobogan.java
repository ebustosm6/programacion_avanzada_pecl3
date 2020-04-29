package prueba1;

import java.util.concurrent.ArrayBlockingQueue;

public class VigilanteTobogan extends Vigilante {

    public VigilanteTobogan(String id, ArrayBlockingQueue<Visitante> colaEspera, RegistroVisitantes registro) {
        super(id, colaEspera, registro);
    }

    public long getTiempoVigilancia() {
        return (long) ((int) (400) + (100 * Math.random()));
    }

    public Permiso tipoPermiso(Visitante visitante) {
        Permiso tipoPermiso = Permiso.NO_PERMITIDO;
        if (visitante.getEdad() >= 11 && visitante.getEdad() <= 14) {
            tipoPermiso = Permiso.PERMITIDO;
            visitante.setTicket(TicketTobogan.TOBOGAN_A);
        } else if (visitante.getEdad() >= 15 && visitante.getEdad() <= 17) {
            tipoPermiso = Permiso.PERMITIDO;
            visitante.setTicket(TicketTobogan.TOBOGAN_B);
        }else if(visitante.getEdad() > 17){
            tipoPermiso = Permiso.PERMITIDO;
            visitante.setTicket(TicketTobogan.TOBOGAN_C);
        }
        return tipoPermiso;
    }

}
