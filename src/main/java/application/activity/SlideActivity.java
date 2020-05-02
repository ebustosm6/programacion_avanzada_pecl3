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
        LifeGuard guard = new SlideLifeGuard("VigilanteToboganA", getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), "-monitor", guard.getIdentificator());
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
        LifeGuard guard = new SlideLifeGuard(identificator, getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(identificatorActividad, "-monitor", guard.getIdentificator());
        return guard;
    }

    public void initOtherLifeguards() {
        vigilante2.start();
        vigilante3.start();
    }

    @Override
    public boolean goIn(ChildUser user) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            user.setPermisoActividad(Permission.NONE);

            getWaitingLine().offer(user);
            getRegistry().registerUserInActivity(getIdentificator(), COLA_ESPERA, user.getIdentificator());
            user.setCurrentActivity(getIdentificator());
            user.getSupervisor().setCurrentActivity(getIdentificator());
            getPiscinaGrande().getWaitingAreaSupervisor().offer(user.getSupervisor()); // se van a la zona de espera de la piscina para que no les den permiso y se tiren por el tobogan
            getRegistry().registerUserInActivity(getPiscinaGrande().getIdentificator(), ZONA_ESPERA, user.getSupervisor().getIdentificator());

            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(user);
            } else {
                throw new SecurityException();
            }
            printStatus();
            resultado = true;
        } catch (SecurityException e) {
            getWaitingLine().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), COLA_ESPERA, user.getIdentificator());
            getPiscinaGrande().getWaitingAreaSupervisor().remove(user.getSupervisor());
            getRegistry().unregisterUserFromActivity(getPiscinaGrande().getIdentificator(), ZONA_ESPERA, user.getSupervisor().getIdentificator());

            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");
        }
        return resultado;
    }

    @Override
    public boolean goIn(AdultUser user) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            user.setPermisoActividad(Permission.NONE);
            getWaitingLine().offer(user);
            user.setCurrentActivity(getIdentificator());
            getRegistry().registerUserInActivity(getIdentificator(), COLA_ESPERA, user.getIdentificator());

            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(user);
            }
            printStatus();
            resultado = true;
        } catch (SecurityException e) {
            getWaitingLine().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), COLA_ESPERA, user.getIdentificator());

            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }

    @Override
    public boolean goIn(YoungUser user) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            user.setPermisoActividad(Permission.NONE);
            getWaitingLine().offer(user);
            user.setCurrentActivity(getIdentificator());
            getRegistry().registerUserInActivity(getIdentificator(), COLA_ESPERA, user.getIdentificator());

            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(user);
            }
            printStatus();
            resultado = true;
        } catch (SecurityException e) {
            getWaitingLine().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), COLA_ESPERA, user.getIdentificator());

            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }
    
    @Override
    public void onGoOutSuccess(User user) {
        user.setCurrentActivity(getPiscinaGrande().getIdentificator());
        getPiscinaGrande().getActivityArea().offer(user);
        getRegistry().registerUserInActivity(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, user.getIdentificator());
        getPiscinaGrande().doActivity(user);
        getPiscinaGrande().goOut(user);
        user.setCurrentActivity("ParqueAcuatico");
    }

    @Override
    public void goOut(ChildUser user) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            if (user.getSlideTicket() == SlideTicket.SLIDE_A) {
                getActivityArea().remove(user); //Tobogan A
                getRegistry().unregisterUserFromActivity(getIdentificator(), ZONA_ACTIVIDAD, user.getIdentificator());
            } else {
                getToboganB().remove(user);
                getRegistry().unregisterUserFromActivity(getIdentificator(), ZONA_ACTIVIDAD_B, user.getIdentificator());
            }
            printStatus();
            
            onGoOutSuccess(user);
//            user.setCurrentActivity(getPiscinaGrande().getIdentificator());
//            getPiscinaGrande().getZonaActividad().offer(user);
//            getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, user.getIdentificator());
//            getPiscinaGrande().doActivity(user);
//            getPiscinaGrande().goOut(user);
//            user.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goOut(AdultUser user) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
            getToboganC().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), ZONA_ACTIVIDAD_C, user.getIdentificator());
            printStatus();
            
            onGoOutSuccess(user);
//            user.setCurrentActivity(getPiscinaGrande().getIdentificator());
//            getPiscinaGrande().getZonaActividad().offer(user);
//            getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, user.getIdentificator());
//            getPiscinaGrande().doActivity(user);
//            getPiscinaGrande().goOut(user);
//            user.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goOut(YoungUser user) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
            if (user.getSlideTicket() == SlideTicket.SLIDE_A) {
                getActivityArea().remove(user); //Tobogan A
                getRegistry().unregisterUserFromActivity(getIdentificator(), ZONA_ACTIVIDAD, user.getIdentificator());
            } else {
                getToboganB().remove(user);
                getRegistry().unregisterUserFromActivity(getIdentificator(), ZONA_ACTIVIDAD_B, user.getIdentificator());
            }
            printStatus();
            
            onGoOutSuccess(user);
//            user.setCurrentActivity(getPiscinaGrande().getIdentificator());
//            getPiscinaGrande().getZonaActividad().offer(user);
//            getRegistro().aniadirVisitanteZonaActividad(getPiscinaGrande().getIdentificator(), ZONA_ACTIVIDAD, user.getIdentificator());
//            getPiscinaGrande().doActivity(user);
//            getPiscinaGrande().goOut(user);
//            user.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goIntoSlide(User user) throws InterruptedException{
        getPiscinaGrande().getSemaforo().acquire();
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), COLA_ESPERA, user.getIdentificator());
        if (user.getSlideTicket() == SlideTicket.SLIDE_A) {
            while (!getActivityArea().offer(user)) {
                // esperar al tobogan vacio
            }
            user.setCurrentActivity("ToboganA");
            getRegistry().registerUserInActivity(getIdentificator(), ZONA_ACTIVIDAD, user.getIdentificator());

        } else if (user.getSlideTicket() == SlideTicket.SLIDE_B) {
            while (!getToboganB().offer(user)) {
                // esperar al tobogan vacio
            }
            user.setCurrentActivity("ToboganB");
            getRegistry().registerUserInActivity(getIdentificator(), ZONA_ACTIVIDAD_B, user.getIdentificator());
        } else {
            while (!getToboganC().offer(user)) {
                // esperar al tobogan vacio
            }
            user.setCurrentActivity("ToboganC");
            getRegistry().registerUserInActivity(getIdentificator(), ZONA_ACTIVIDAD_C, user.getIdentificator());
        }
    }

    public void printStatus() {
        System.out.println(getIdentificator() + " - cola de espera: " + getWaitingLine().toString());
        System.out.println(getIdentificator() + " - TOBOGAN A: " + getActivityArea().toString());  //Tobogan A
        System.out.println(getIdentificator() + " - TOBOGAN B: " + getToboganB().toString());       //Tobogan B
        System.out.println(getIdentificator() + " - TOBOGAN C: " + getToboganC().toString());       //Tobogan C
        System.out.println(getIdentificator() + " - zona de espera de actividad: " + getPiscinaGrande().getWaitingAreaSupervisor().toString());
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
