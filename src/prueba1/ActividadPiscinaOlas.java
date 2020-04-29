package prueba1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ActividadPiscinaOlas extends Actividad {

    private static int CAPACIDAD = 20;
    private static String IDENTIFICADOR = "ActividadPiscinaOlas";
    private CyclicBarrier barrera = new CyclicBarrier(2);
    private static final String COLA_ESPERA = "-colaEspera";
    private static final String ZONA_ACTIVIDAD = "-zonaActividad";
    private static final String ZONA_ESPERA = "-zonaEsperaAcompanante";

    public ActividadPiscinaOlas(RegistroVisitantes registro) {
        super(IDENTIFICADOR, CAPACIDAD, registro);

    }
    
    public List<String> getAreasActividad() {
    	ArrayList<String> areas = new ArrayList<>();
    	areas.add(COLA_ESPERA);
    	areas.add(ZONA_ACTIVIDAD);
    	areas.add(ZONA_ESPERA);
    	return areas;
    }

    public Vigilante iniciarVigilante() {
    	Vigilante vigilante = new VigilantePiscinaOlas("VigilantePisinaOlas", getColaEspera(), getRegistro());
    	getRegistro().aniadirMonitorEnZona(getIdentificador(), "-monitor", vigilante.getIdentificador());
        return vigilante;
    }

    public long getTiempoActividad() {
        return (long) ((int) (2000) + (3000 * Math.random()));
    }

    public boolean entrar(VisitanteNinio visitante) {
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
                encolarNinioActividad(visitante);
            } else if (visitante.getPermisoActividad() == Permiso.PERMITIDO) {
                espaciosOcupados = 1;
                barrera.await();
                desencolarNinioColaEspera(visitante);
                getZonaActividad().offer(visitante);
                getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
                getZonaEsperaAcompanante().offer(visitante.getAcompaniante());
                getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ESPERA, visitante.getAcompaniante().getIdentificador());
            }

            resultado = true;
        } catch (SecurityException | InterruptedException | BrokenBarrierException e) {
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

            if (visitante.getPermisoActividad() != Permiso.PERMITIDO) {
                throw new SecurityException();
            }
            barrera.await();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA,visitante.getIdentificador());
            getZonaActividad().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD,visitante.getIdentificador());


            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA,visitante.getIdentificador());
            
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            visitante.setActividadActual("ParqueAcuatico");
            imprimirColas();
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
            barrera.await();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA,visitante.getIdentificador());
            getZonaActividad().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD,visitante.getIdentificador());


            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA,visitante.getIdentificador());
            
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);
            visitante.setActividadActual("ParqueAcuatico");
            imprimirColas();
        }
        return resultado;
    }

}
