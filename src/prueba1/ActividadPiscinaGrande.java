package prueba1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActividadPiscinaGrande extends Actividad {

    private static int CAPACIDAD = 50;
    private static String IDENTIFICADOR = "ActividadPiscinaGrande";
    private Semaphore semaforo;
    private static final String COLA_ESPERA = "-colaEspera";
    private static final String ZONA_ACTIVIDAD = "-zonaActividad";
    private static final String ZONA_ESPERA = "-zonaEsperaAcompaniante";

    public ActividadPiscinaGrande(RegistroVisitantes registro) {
        super(IDENTIFICADOR, CAPACIDAD, registro);
        this.semaforo = new Semaphore(CAPACIDAD, true);
    }

    public Vigilante iniciarVigilante() {
        Vigilante vigilante = new VigilantePiscinaGrande("VigilantePiscinaGrande", getColaEspera(), getZonaActividad(), getRegistro());
        getRegistro().aniadirMonitorEnZona(getIdentificador(), "-monitor", vigilante.getIdentificador());
        return vigilante;
    }

    public long getTiempoActividad() {
        return (long) ((int) (3000) + (2000 * Math.random()));
    }

    public synchronized void encolarNinioActividadSemaforo(VisitanteNinio visitante) throws InterruptedException{
        getColaEspera().remove(visitante);
        getSemaforo().acquire(2);
        getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());
        getColaEspera().remove(visitante.getAcompaniante());
        getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getAcompaniante().getIdentificador());
        while (!getZonaActividad().offer(visitante)) {
            //espera
        }
        getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
        while (!getZonaActividad().offer(visitante.getAcompaniante())) {
            //espera
        }
        getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getAcompaniante().getIdentificador());
    }
    
    public synchronized void desencolarNinio(VisitanteNinio visitante) {
        getZonaActividad().remove(visitante);
        semaforo.release();
        getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
        getZonaActividad().remove(visitante.getAcompaniante());
        semaforo.release();
        getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getAcompaniante().getIdentificador());
    }

    public boolean entrar(VisitanteNinio visitante) throws InterruptedException {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        int espaciosOcupados = 2;
        try {
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            encolarNinio(visitante);
            imprimirColas();

            while (visitante.getPermisoActividad() == Permiso.NO_ESPECIFICADO) {
                visitante.sleep(500);
            }

            if (visitante.getPermisoActividad() == Permiso.NO_PERMITIDO) {
                throw new SecurityException();
            } else if (visitante.getPermisoActividad() == Permiso.CON_ACOMPANIANTE) {
                encolarNinioActividadSemaforo(visitante);
            } else if (visitante.getPermisoActividad() == Permiso.PERMITIDO) {
                espaciosOcupados = 1;
                desencolarNinioColaEspera(visitante);
                semaforo.acquire();
                getZonaActividad().offer(visitante);
                getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
                getZonaEsperaAcompanante().offer(visitante.getAcompaniante());
                getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ESPERA, visitante.getAcompaniante().getIdentificador());
            }
            resultado = true;
        } catch (SecurityException e) {
            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            desencolarNinioColaEspera(visitante);
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            visitante.setActividadActual("ParqueAcuatico");
            imprimirColas();

        }
        return resultado;
    }

    public boolean entrar(VisitanteAdulto visitante) throws InterruptedException {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            getColaEspera().offer(visitante);
            visitante.setActividadActual(getIdentificador());
            getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());
            imprimirColas();

            while (visitante.getPermisoActividad() == Permiso.NO_ESPECIFICADO) {
                visitante.sleep(500);
            }

            if (visitante.getPermisoActividad() == Permiso.PERMITIDO) {
                getColaEspera().remove(visitante);
                semaforo.acquire();
                getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());
                getZonaActividad().offer(visitante);
                getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
                resultado = true;
            } else {
                throw new SecurityException();
            }

        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());

            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            imprimirColas();
            visitante.setActividadActual("ParqueAcuatico");

        }
        return resultado;
    }

    public boolean entrar(VisitanteMenor visitante) throws InterruptedException {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            getColaEspera().offer(visitante);
            visitante.setActividadActual(getIdentificador());
            getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());
            imprimirColas();

            while (visitante.getPermisoActividad() == Permiso.NO_ESPECIFICADO) {
                visitante.sleep(500);
            }

            if (visitante.getPermisoActividad() != Permiso.PERMITIDO) {
                throw new SecurityException();
            }

            getColaEspera().remove(visitante);
            semaforo.acquire();
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());
            getZonaActividad().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
            resultado = true;

        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());

            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            imprimirColas();
            visitante.setActividadActual("ParqueAcuatico");

        }
        return resultado;
    }

    public void disfrutar(Visitante visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            imprimirColas();
            visitante.sleep(getTiempoActividad());
        } catch (InterruptedException e) {
            if (visitante instanceof VisitanteNinio) {
                getZonaActividad().remove(visitante);
                getZonaActividad().remove(visitante.getAcompaniante());
                semaforo.release(2);
            } else {
                getZonaActividad().remove(visitante);
                semaforo.release();
            }
            visitante.setActividadActual("ParqueAcuatico");
        }
    }

    public void salir(VisitanteAdulto visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            getZonaActividad().remove(visitante);
            semaforo.release();
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            imprimirColas();
            visitante.setActividadActual("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void salir(VisitanteMenor visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            getZonaActividad().remove(visitante);
            semaforo.release();
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            imprimirColas();
            visitante.setActividadActual("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void salir(VisitanteNinio visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            if (visitante.getPermisoActividad() == Permiso.CON_ACOMPANIANTE) {
                desencolarNinio(visitante);
            } else {
                getZonaActividad().remove(visitante);
                semaforo.release();
                getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
                getZonaEsperaAcompanante().remove(visitante.getAcompaniante());
                getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ESPERA, visitante.getAcompaniante().getIdentificador());

            }
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);// poner el permiso a false (que deambulen por ahi sin permiso)
            visitante.setActividadActual("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public Semaphore getSemaforo() {
        return semaforo;
    }

    public void setSemaforo(Semaphore semaforo) {
        this.semaforo = semaforo;
    }

}
