package prueba1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class ParqueAcuatico implements Serializable {

    private String identificador = "ParqueAcuatico";
    private static int NUM_VISITANTES = 100;
    private Semaphore semaforo = new Semaphore(NUM_VISITANTES, true);
    private List<Actividad> actividades = new ArrayList<>();
    private BlockingQueue<Visitante> colaEspera = new ArrayBlockingQueue<>(5000, true);
    private static final String COLA_ESPERA = "-colaEspera";
    private RegistroVisitantes registro;
    private ActividadPiscinaGrande piscinaGrande;

    public ParqueAcuatico(RegistroVisitantes registro) {
        this.registro = registro;
        iniciarActividades();
        registrarZonaActividad();
    }

    public void iniciarActividades() {
        this.actividades.add(new ActividadVestuario(registro));
        this.actividades.add(new ActividadTumbonas(registro));
        this.actividades.add(new ActividadPiscinaOlas(registro));
        this.actividades.add(new ActividadPiscinaNinos(registro));
        this.piscinaGrande = new ActividadPiscinaGrande(registro);
        this.actividades.add(piscinaGrande);
        this.actividades.add(new ActividadTobogan(registro, piscinaGrande));
    }

    public RegistroVisitantes getRegistro() {
        return registro;
    }

    public List<String> getAreasActividad() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(COLA_ESPERA);
        return areas;
    }

    public void registrarZonaActividad() {
        this.registro.registrarZonaActividad(identificador);
        this.registro.registrarZonasActividad(identificador, getAreasActividad());

    }

    public List<Actividad> escogerActividades(int cantidad) {
        List<Actividad> actividadesEscogidas = new ArrayList<>();
        if (cantidad <= 0) {
            cantidad = actividades.size();
        }
        actividadesEscogidas.add(actividades.get(0));
        //actividadesEscogidas.add(actividades.get(5));

        while (cantidad > 0) {
            int indice_random = (int) (actividades.size() * Math.random());
            if (indice_random == 0) { // vestuario
                indice_random = 1;
            }
            actividadesEscogidas.add(actividades.get(indice_random));
            cantidad--;
        }
        actividadesEscogidas.add(actividades.get(0));

        return actividadesEscogidas;

    }

    public boolean entrar(VisitanteNinio visitante) {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            encolarNinio(visitante);
            visitante.setActividadActual(identificador);
            imprimirColaEspera();
            //visitante.sleep(500);
            semaforo.acquire(2);
            desencolarNinioColaEspera(visitante);
            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
            desencolarNinioColaEspera(visitante);
            visitante.setActividadActual("Fuera");
        }
        return resultado;
    }

    public boolean entrar(VisitanteAdulto visitante) {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            getColaEspera().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
            visitante.setActividadActual(identificador);
            imprimirColaEspera();
            //visitante.sleep(500);
            semaforo.acquire();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
            getRegistro().eliminarVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
            visitante.setActividadActual("Fuera");
        }
        return resultado;
    }

    public boolean entrar(VisitanteMenor visitante) {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            getColaEspera().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
            visitante.setActividadActual(identificador);
            imprimirColaEspera();
            //visitante.sleep(500);
            semaforo.acquire();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
            getRegistro().eliminarVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
            visitante.setActividadActual("Fuera");
        }
        return resultado;
    }

    public void salir(VisitanteAdulto visitante) {
        getRegistro().comprobarDetenerReanudar();
        getColaEspera().remove(visitante);
        imprimirColaEspera();
        semaforo.release();
        visitante.setActividadActual("Fuera");
        getRegistro().eliminarVisitante(visitante);
    }

    public void salir(VisitanteMenor visitante) {
        getRegistro().comprobarDetenerReanudar();
        getColaEspera().remove(visitante);
        imprimirColaEspera();
        semaforo.release();
        visitante.setActividadActual("Fuera");
        getRegistro().eliminarVisitante(visitante);
    }

    public void salir(VisitanteNinio visitante) {
        getRegistro().comprobarDetenerReanudar();
        desencolarNinioColaEspera(visitante);
        imprimirColaEspera();
        semaforo.release(2);
        visitante.setActividadActual("Fuera");
        getRegistro().eliminarVisitante(visitante);
    }

    public synchronized void encolarNinio(VisitanteNinio visitante) {
        getColaEspera().offer(visitante);
        getRegistro().aniadirVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
        getColaEspera().offer(visitante.getAcompaniante());
        getRegistro().aniadirVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getAcompaniante().getIdentificador());
    }

    public synchronized void desencolarNinioColaEspera(VisitanteNinio visitante) {
        getColaEspera().remove(visitante);
        getRegistro().eliminarVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getIdentificador());
        getColaEspera().remove(visitante.getAcompaniante());
        getRegistro().eliminarVisitanteZonaActividad(identificador, COLA_ESPERA, visitante.getAcompaniante().getIdentificador());
    }

    private void imprimirColaEspera() {

    }

    public static int getNUM_VISITANTES() {
        return NUM_VISITANTES;
    }

    public static void setNUM_VISITANTES(int NUM_VISITANTES) {
        ParqueAcuatico.NUM_VISITANTES = NUM_VISITANTES;
    }

    public Semaphore getSemaforo() {
        return semaforo;
    }

    public void setSemaforo(Semaphore semaforo) {
        this.semaforo = semaforo;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }

    public BlockingQueue<Visitante> getColaEspera() {
        return colaEspera;
    }

    public void setColaEspera(BlockingQueue<Visitante> colaEspera) {
        this.colaEspera = colaEspera;
    }

}
