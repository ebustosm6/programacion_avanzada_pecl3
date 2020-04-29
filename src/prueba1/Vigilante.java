package prueba1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Vigilante extends Thread {

    private String identificador;
    private BlockingQueue<Visitante> colaEspera;
    private RegistroVisitantes registro;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Vigilante(String id, ArrayBlockingQueue<Visitante> colaEspera, RegistroVisitantes registro) {
        this.identificador = id;
        this.colaEspera = colaEspera;
        this.registro = registro;
    }

    public Permiso tipoPermiso(Visitante visitante) {
        Permiso tipoPermiso = Permiso.NO_PERMITIDO;
        if (visitante.getEdad() > 0) {
            tipoPermiso = Permiso.PERMITIDO;
        }
        return tipoPermiso;
    }

    public long getTiempoVigilancia() {
        return (long) ((int) (500) + (400 * Math.random()));
    }

    public RegistroVisitantes getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroVisitantes registro) {
        this.registro = registro;
    }

    public void run() {
        while (true) {
            try {
                for (Visitante visitante : colaEspera) {
                    getRegistro().comprobarDetenerReanudar();
                    sleep(getTiempoVigilancia());
                    Permiso permiso = tipoPermiso(visitante);
                    visitante.setPermisoActividad(permiso);
                    if (permiso == Permiso.NO_PERMITIDO) {
                        System.out.println("Vigilante " + getIdentificador() + " echando al visitante " + visitante.getIdentificador() + " con edad " + visitante.getEdad());
                        System.out.println("----------------------------------------------------------------------------------------------------------");
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public BlockingQueue<Visitante> getColaEspera() {
        return colaEspera;
    }

    public void setColaEspera(BlockingQueue<Visitante> colaEspera) {
        this.colaEspera = colaEspera;
    }

}
