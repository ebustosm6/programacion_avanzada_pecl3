package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import application.activity.MainPoolActivity;
import application.activity.WavePoolActivity;
import application.activity.SlideActivity;
import application.activity.DeckChairActivity;
import application.activity.Activity;
import application.activity.ChildPoolActivity;
import application.activity.LockerRoomActivity;
import application.user.User;
import application.user.AdultUser;
import application.user.YoungUser;
import application.user.ChildUser;

public class AquaticPark implements Serializable {

    private String identificator = "ParqueAcuatico";
    private static int NUM_VISITANTES = 100;
    private Semaphore semaforo = new Semaphore(NUM_VISITANTES, true);
    private List<Activity> actividades = new ArrayList<>();
    private BlockingQueue<User> colaEspera = new ArrayBlockingQueue<>(5000, true);
    private static final String COLA_ESPERA = "-colaEspera";
    private UserRegistry userRegistry;
    private MainPoolActivity piscinaGrande;

    public AquaticPark(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
        iniciarActividades();
        registrarZonaActividad();
    }

    public void iniciarActividades() {
        this.actividades.add(new LockerRoomActivity(userRegistry));
        this.actividades.add(new DeckChairActivity(userRegistry));
        this.actividades.add(new WavePoolActivity(userRegistry));
        this.actividades.add(new ChildPoolActivity(userRegistry));
        this.piscinaGrande = new MainPoolActivity(userRegistry);
        this.actividades.add(piscinaGrande);
        this.actividades.add(new SlideActivity(userRegistry, piscinaGrande));
    }

    public UserRegistry getRegistro() {
        return userRegistry;
    }

    public List<String> getAreasActividad() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(COLA_ESPERA);
        return areas;
    }

    public void registrarZonaActividad() {
        this.userRegistry.registrarZonaActividad(identificator);
        this.userRegistry.registrarZonasActividad(identificator, getAreasActividad());

    }

    public List<Activity> escogerActividades(int cantidad) {
        List<Activity> actividadesEscogidas = new ArrayList<>();
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

    public boolean entrar(ChildUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            encolarNinio(visitante);
            visitante.setCurrentActivity(identificator);
            imprimirColaEspera();
            //visitante.sleep(500);
            semaforo.acquire(2);
            desencolarNinioColaEspera(visitante);
            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
            desencolarNinioColaEspera(visitante);
            visitante.setCurrentActivity("Fuera");
        }
        return resultado;
    }

    public boolean entrar(AdultUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            getColaEspera().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
            visitante.setCurrentActivity(identificator);
            imprimirColaEspera();
            //visitante.sleep(500);
            semaforo.acquire();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
            getRegistro().eliminarVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
            visitante.setCurrentActivity("Fuera");
        }
        return resultado;
    }

    public boolean entrar(YoungUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            getColaEspera().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
            visitante.setCurrentActivity(identificator);
            imprimirColaEspera();
            //visitante.sleep(500);
            semaforo.acquire();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
            getRegistro().eliminarVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
            visitante.setCurrentActivity("Fuera");
        }
        return resultado;
    }

    public void salir(AdultUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        getColaEspera().remove(visitante);
        imprimirColaEspera();
        semaforo.release();
        visitante.setCurrentActivity("Fuera");
        getRegistro().eliminarVisitante(visitante);
    }

    public void salir(YoungUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        getColaEspera().remove(visitante);
        imprimirColaEspera();
        semaforo.release();
        visitante.setCurrentActivity("Fuera");
        getRegistro().eliminarVisitante(visitante);
    }

    public void salir(ChildUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        desencolarNinioColaEspera(visitante);
        imprimirColaEspera();
        semaforo.release(2);
        visitante.setCurrentActivity("Fuera");
        getRegistro().eliminarVisitante(visitante);
    }

    public synchronized void encolarNinio(ChildUser visitante) {
        getColaEspera().offer(visitante);
        getRegistro().aniadirVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
        getColaEspera().offer(visitante.getSupervisor());
        getRegistro().aniadirVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getSupervisor().getIdentificator());
    }

    public synchronized void desencolarNinioColaEspera(ChildUser visitante) {
        getColaEspera().remove(visitante);
        getRegistro().eliminarVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getIdentificator());
        getColaEspera().remove(visitante.getSupervisor());
        getRegistro().eliminarVisitanteZonaActividad(identificator, COLA_ESPERA, visitante.getSupervisor().getIdentificator());
    }

    private void imprimirColaEspera() {

    }

    public static int getNUM_VISITANTES() {
        return NUM_VISITANTES;
    }

    public static void setNUM_VISITANTES(int NUM_VISITANTES) {
        AquaticPark.NUM_VISITANTES = NUM_VISITANTES;
    }

    public Semaphore getSemaforo() {
        return semaforo;
    }

    public void setSemaforo(Semaphore semaforo) {
        this.semaforo = semaforo;
    }

    public List<Activity> getActividades() {
        return actividades;
    }

    public void setActividades(List<Activity> actividades) {
        this.actividades = actividades;
    }

    public BlockingQueue<User> getColaEspera() {
        return colaEspera;
    }

    public void setColaEspera(BlockingQueue<User> colaEspera) {
        this.colaEspera = colaEspera;
    }

}
