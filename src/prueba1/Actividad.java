package prueba1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class Actividad implements Serializable {

    private String identificador;
    private int capacidadTotal = 5;
    private int capacidadInterior = 5;
    private ArrayBlockingQueue<Visitante> colaEspera;
    private static final String COLA_ESPERA = "-colaEspera";
    private ArrayBlockingQueue<Visitante> zonaActividad;
    private static final String ZONA_ACTIVIDAD = "-zonaActividad";
    private ArrayBlockingQueue<Visitante> zonaEsperaAcompanante;
    private static final String ZONA_ESPERA = "-zonaEsperaAcompaniante";
    private Vigilante vigilante;
    private RegistroVisitantes registro;

    public Actividad(String identificador, int capacidad, RegistroVisitantes registro) {
        this.identificador = identificador;
        this.capacidadTotal = capacidad;
        this.capacidadInterior = capacidad;
        this.registro = registro;
        this.colaEspera = new ArrayBlockingQueue<>(5000, true);
        this.zonaActividad = new ArrayBlockingQueue<>(capacidad, true);
        this.zonaEsperaAcompanante = new ArrayBlockingQueue<>(capacidad, true);
        this.vigilante = iniciarVigilante();
        registrarZonaActividad();
        getVigilante().start();
    }

    public Actividad(String identificador, int capacidadTotal, int capacidadInterior, boolean colaFifo, RegistroVisitantes registro) {
        this.identificador = identificador;
        this.capacidadTotal = capacidadTotal;
        this.capacidadInterior = capacidadInterior;
        this.registro = registro;
        this.colaEspera = new ArrayBlockingQueue<>(5000, colaFifo);
        this.zonaActividad = new ArrayBlockingQueue<>(capacidadInterior, true);
        this.zonaEsperaAcompanante = new ArrayBlockingQueue<>(capacidadTotal, true);
        this.vigilante = iniciarVigilante();
        registrarZonaActividad();
        getVigilante().start();
    }

    public List<String> getAreasActividad() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(COLA_ESPERA);
        areas.add(ZONA_ACTIVIDAD);
        areas.add(ZONA_ESPERA);
        return areas;
    }

    public void registrarZonaActividad() {
        this.registro.registrarZonaActividad(identificador);
        this.registro.registrarZonasActividad(identificador, getAreasActividad());

    }

    public Vigilante iniciarVigilante() {
        Vigilante vigilante = new Vigilante("VigilanteDefault", getColaEspera(), registro);
        registro.aniadirMonitorEnZona(getIdentificador(), "-monitor", vigilante.getIdentificador());
        return vigilante;
    }

    public long getTiempoActividad() {
        return (long) ((int) (5000) + (5000 * Math.random()));
    }

    public void imprimirColas() {
        System.out.println("******************************");
        System.out.println(getIdentificador() + " - cola de espera: " + getColaEspera().toString());
        System.out.println(getIdentificador() + " - zona de actividad: " + getZonaActividad().toString());
        System.out.println(getIdentificador() + " - zona de espera de actividad: " + getZonaEsperaAcompanante().toString());
        System.out.println("******************************");
    }

    public synchronized void encolarNinio(VisitanteNinio visitante) {
        while (!getColaEspera().offer(visitante)) {
            //espera
        }
        getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());
        while (!getColaEspera().offer(visitante.getAcompaniante())) {
            //espera
        }

        getRegistro().aniadirVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getAcompaniante().getIdentificador());
        visitante.setActividadActual(getIdentificador());
        visitante.getAcompaniante().setActividadActual(getIdentificador());
    }

    public synchronized void desencolarNinioColaEspera(VisitanteNinio visitante) {
        getColaEspera().remove(visitante);
        getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getIdentificador());
        getColaEspera().remove(visitante.getAcompaniante());
        getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), COLA_ESPERA, visitante.getAcompaniante().getIdentificador());
    }

    public synchronized void encolarNinioActividad(VisitanteNinio visitante) {
        getColaEspera().remove(visitante);
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
        getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
        getZonaActividad().remove(visitante.getAcompaniante());
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
                encolarNinioActividad(visitante);
            } else if (visitante.getPermisoActividad() == Permiso.PERMITIDO) {
                espaciosOcupados = 1;
                desencolarNinioColaEspera(visitante);
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
            } else {
                getZonaActividad().remove(visitante);
            }
            visitante.setActividadActual("ParqueAcuatico");
        }
    }

    public void salir(VisitanteAdulto visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            getZonaActividad().remove(visitante);
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
                getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ACTIVIDAD, visitante.getIdentificador());
                getZonaEsperaAcompanante().remove(visitante.getAcompaniante());
                getRegistro().eliminarVisitanteZonaActividad(getIdentificador(), ZONA_ESPERA, visitante.getAcompaniante().getIdentificador());

            }
            visitante.setPermisoActividad(Permiso.NO_ESPECIFICADO);// poner el permiso a false (que deambulen por ahi sin permiso)
            visitante.setActividadActual("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public RegistroVisitantes getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroVisitantes registro) {
        this.registro = registro;
    }

    public String toString() {
        return this.identificador;
    }

    protected void imprimirColaEspera() {
        System.out.println("La actividad: " + identificador + " y la cola de espera es: " + colaEspera.toString());
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public ArrayBlockingQueue<Visitante> getZonaActividad() {
        return this.zonaActividad;
    }

    public void setZonaActividad(ArrayBlockingQueue<Visitante> zonaActividad) {
        this.zonaActividad = zonaActividad;
    }

    public ArrayBlockingQueue<Visitante> getColaEspera() {
        return colaEspera;
    }

    public void setColaEspera(ArrayBlockingQueue<Visitante> colaEspera) {
        this.colaEspera = colaEspera;
    }

    public Vigilante getVigilante() {
        return vigilante;
    }

    public void setVigilante(Vigilante vigilante) {
        this.vigilante = vigilante;
    }

    public int getCapacidadTotal() {
        return capacidadTotal;
    }

    public void setCapacidadTotal(int capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
    }

    public int getCapacidadInterior() {
        return capacidadInterior;
    }

    public void setCapacidadInterior(int capacidadInterior) {
        this.capacidadInterior = capacidadInterior;
    }

    public ArrayBlockingQueue<Visitante> getZonaEsperaAcompanante() {
        return zonaEsperaAcompanante;
    }

    public void setZonaEsperaAcompanante(ArrayBlockingQueue<Visitante> zonaEsperaAcompanante) {
        this.zonaEsperaAcompanante = zonaEsperaAcompanante;
    }
}
