package application.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.enums.SlideTicket;
import application.lifeguard.LifeGuard;
import application.lifeguard.SlideLifeGuard;
import application.user.AdultUser;
import application.user.ChildUser;
import application.user.User;
import application.user.YoungUser;

public class SlideActivity extends Activity {

//    private static int CAPACITY = ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY;
    private static String IDENTIFICATOR = "ActividadTobogan";
    private SlideTicket ticket;
    private ArrayBlockingQueue<User> toboganC = new ArrayBlockingQueue<User>(ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, true);
    private ArrayBlockingQueue<User> toboganB = new ArrayBlockingQueue<User>(ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, true);
    private MainPoolActivity piscinaGrande;
    private LifeGuard vigilante2;
    private LifeGuard vigilante3;
    private static final String COLA_ESPERA = "-colaEspera";
    private static final String ZONA_ACTIVIDAD = "-zonaActividad";
    private static final String ZONA_ACTIVIDAD_B = "-zonaActividadB";
    private static final String ZONA_ACTIVIDAD_C = "-zonaActividadC";
    private static final String ZONA_ESPERA = "-zonaEsperaAcompaniante";

    public SlideActivity(UserRegistry userRegistry, MainPoolActivity piscinaGrande) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, userRegistry);
        this.piscinaGrande = piscinaGrande;
        this.vigilante2 = initLifeGuard("VigilanteToboganB", getIdentificator() + "B");
        this.vigilante3 = initLifeGuard("VigilanteToboganC", getIdentificator() + "C");
        initOtherLifeguards();
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_SLIDE_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_SLIDE_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_SLIDE_MIN_MILISECONDS * Math.random()));
    }
    
    @Override
    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new SlideLifeGuard("VigilanteToboganA", getColaEspera(), getRegistro());
        getRegistro().aniadirMonitorEnZona(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }

    @Override
    public List<String> getActivitySubareas() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(COLA_ESPERA);
        areas.add(ZONA_ACTIVIDAD);
        areas.add(ZONA_ACTIVIDAD_B);
        areas.add(ZONA_ACTIVIDAD_C);
        areas.add(ZONA_ESPERA);
        return areas;
    }

    public LifeGuard initLifeGuard(String identificator, String identificatorActividad) {
        LifeGuard guard = new SlideLifeGuard(identificator, getColaEspera(), getRegistro());
        getRegistro().aniadirMonitorEnZona(identificatorActividad, "-monitor", guard.getIdentificator());
        return guard;
    }

    public void initOtherLifeguards() {
        vigilante2.start();
        vigilante3.start();
    }

    @Override
    public boolean goIn(ChildUser visitante) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            visitante.setPermisoActividad(Permission.NONE);

            getColaEspera().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), COLA_ESPERA, visitante.getIdentificator());
            visitante.setCurrentActivity(getIdentificator());
            visitante.getSupervisor().setCurrentActivity(getIdentificator());
            getPiscinaGrande().getZonaEsperaAcompanante().offer(visitante.getSupervisor()); // se van a la zona de espera de la piscina para que no les den permiso y se tiren por el tobogan
            getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ESPERA, visitante.getSupervisor().getIdentificator());

            imprimirColas();
            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(visitante);
            } else {
                throw new SecurityException();
            }
            imprimirColas();
            resultado = true;
        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), COLA_ESPERA, visitante.getIdentificator());
            getPiscinaGrande().getZonaEsperaAcompanante().remove(visitante.getSupervisor());
            getRegistro().eliminarVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ESPERA, visitante.getSupervisor().getIdentificator());

            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");
        }
        return resultado;
    }

    @Override
    public boolean goIn(AdultUser visitante) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            visitante.setPermisoActividad(Permission.NONE);
            getColaEspera().offer(visitante);
            visitante.setCurrentActivity(getIdentificator());
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), COLA_ESPERA, visitante.getIdentificator());

            imprimirColas();
            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(visitante);
            }
            imprimirColas();
            resultado = true;
        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), COLA_ESPERA, visitante.getIdentificator());

            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }

    @Override
    public boolean goIn(YoungUser visitante) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            visitante.setPermisoActividad(Permission.NONE);
            getColaEspera().offer(visitante);
            visitante.setCurrentActivity(getIdentificator());
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), COLA_ESPERA, visitante.getIdentificator());

            imprimirColas();
            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(visitante);
            }
            imprimirColas();
            resultado = true;
        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), COLA_ESPERA, visitante.getIdentificator());

            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }
    
    @Override
    public void onGoOutSuccess(User visitante) {
        visitante.setCurrentActivity(getPiscinaGrande().getIdentificator());
        getPiscinaGrande().getZonaActividad().offer(visitante);
        getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, visitante.getIdentificator());
        getPiscinaGrande().doActivity(visitante);
        getPiscinaGrande().goOut(visitante);
        visitante.setCurrentActivity("ParqueAcuatico");
    }

    @Override
    public void goOut(ChildUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            if (visitante.getSlideTicket() == SlideTicket.SLIDE_A) {
                getZonaActividad().remove(visitante); //Tobogan A
                getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD, visitante.getIdentificator());
            } else {
                getToboganB().remove(visitante);
                getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD_B, visitante.getIdentificator());
            }
            imprimirColas();
            
            onGoOutSuccess(visitante);
//            visitante.setCurrentActivity(getPiscinaGrande().getIdentificator());
//            getPiscinaGrande().getZonaActividad().offer(visitante);
//            getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, visitante.getIdentificator());
//            getPiscinaGrande().doActivity(visitante);
//            getPiscinaGrande().goOut(visitante);
//            visitante.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    @Override
    public void goOut(AdultUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
            getToboganC().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD_C, visitante.getIdentificator());
            imprimirColas();
            
            onGoOutSuccess(visitante);
//            visitante.setCurrentActivity(getPiscinaGrande().getIdentificator());
//            getPiscinaGrande().getZonaActividad().offer(visitante);
//            getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, visitante.getIdentificator());
//            getPiscinaGrande().doActivity(visitante);
//            getPiscinaGrande().goOut(visitante);
//            visitante.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    @Override
    public void goOut(YoungUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
            if (visitante.getSlideTicket() == SlideTicket.SLIDE_A) {
                getZonaActividad().remove(visitante); //Tobogan A
                getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD, visitante.getIdentificator());
            } else {
                getToboganB().remove(visitante);
                getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD_B, visitante.getIdentificator());
            }
            imprimirColas();
            
            onGoOutSuccess(visitante);
//            visitante.setCurrentActivity(getPiscinaGrande().getIdentificator());
//            getPiscinaGrande().getZonaActividad().offer(visitante);
//            getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, visitante.getIdentificator());
//            getPiscinaGrande().doActivity(visitante);
//            getPiscinaGrande().goOut(visitante);
//            visitante.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goIntoSlide(User visitante) throws InterruptedException{
        getPiscinaGrande().getSemaforo().acquire();
        getColaEspera().remove(visitante);
        getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), COLA_ESPERA, visitante.getIdentificator());
        if (visitante.getSlideTicket() == SlideTicket.SLIDE_A) {
            while (!getZonaActividad().offer(visitante)) {
                // esperar al tobogan vacio
            }
            visitante.setCurrentActivity("ToboganA");
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD, visitante.getIdentificator());

        } else if (visitante.getSlideTicket() == SlideTicket.SLIDE_B) {
            while (!getToboganB().offer(visitante)) {
                // esperar al tobogan vacio
            }
            visitante.setCurrentActivity("ToboganB");
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD_B, visitante.getIdentificator());
        } else {
            while (!getToboganC().offer(visitante)) {
                // esperar al tobogan vacio
            }
            visitante.setCurrentActivity("ToboganC");
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ZONA_ACTIVIDAD_C, visitante.getIdentificator());
        }
    }

    public void imprimirColas() {
        System.out.println("******************************");
        System.out.println(getIdentificator() + " - cola de espera: " + getColaEspera().toString());
        System.out.println(getIdentificator() + " - TOBOGAN A: " + getZonaActividad().toString());  //Tobogan A
        System.out.println(getIdentificator() + " - TOBOGAN B: " + getToboganB().toString());       //Tobogan B
        System.out.println(getIdentificator() + " - TOBOGAN C: " + getToboganC().toString());       //Tobogan C
        System.out.println(getIdentificator() + " - zona de espera de actividad: " + getPiscinaGrande().getZonaEsperaAcompanante().toString());
        System.out.println("******************************");
    }

    public SlideTicket getTicket() {
        return ticket;
    }

    public void setTicket(SlideTicket ticket) {
        this.ticket = ticket;
    }

    public ArrayBlockingQueue<User> getToboganC() {
        return toboganC;
    }

    public void setToboganC(ArrayBlockingQueue<User> toboganC) {
        this.toboganC = toboganC;
    }

    public ArrayBlockingQueue<User> getToboganB() {
        return toboganB;
    }

    public void setToboganB(ArrayBlockingQueue<User> toboganB) {
        this.toboganB = toboganB;
    }

    public MainPoolActivity getPiscinaGrande() {
        return piscinaGrande;
    }

    public void setPiscinaGrande(MainPoolActivity piscinaGrande) {
        this.piscinaGrande = piscinaGrande;
    }

}
