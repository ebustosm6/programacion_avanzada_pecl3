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

    private static String IDENTIFICATOR = "ActividadTobogan";
    private static String LIFEGUARD_A_IDENTIFICATOR = "VigilanteToboganA";
    private static String LIFEGUARD_B_IDENTIFICATOR = "VigilanteToboganB";
    private static String LIFEGUARD_C_IDENTIFICATOR = "VigilanteToboganC";
    private SlideTicket ticket;
    private ArrayBlockingQueue<User> slideB = new ArrayBlockingQueue<User>(ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, true);
    private ArrayBlockingQueue<User> slideC = new ArrayBlockingQueue<User>(ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, true);
    private MainPoolActivity mainPool;
    private LifeGuard lifeguardB;
    private LifeGuard lifeguardC;
    private static final String WAITING_LINE = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE;
    private static final String ACTIVITY_AREA_A = ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY; 
    private static final String ACTIVITY_AREA_B = ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY+"B";
    private static final String ACTIVITY_AREA_C = ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY+"C";
    private static final String WAITING_AREA_SUPERVISORS = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS;

    public SlideActivity(UserRegistry userRegistry, MainPoolActivity piscinaGrande) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, userRegistry);
        this.mainPool = piscinaGrande;
        this.lifeguardB = initLifeGuard(LIFEGUARD_B_IDENTIFICATOR, getIdentificator() + "B");
        this.lifeguardC = initLifeGuard(LIFEGUARD_C_IDENTIFICATOR, getIdentificator() + "C");
        initOtherLifeguards();
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_SLIDE_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_SLIDE_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_SLIDE_MIN_MILISECONDS * Math.random()));
    }
    
    @Override
    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new SlideLifeGuard(LIFEGUARD_A_IDENTIFICATOR, getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    @Override
    public List<String> getActivitySubareas() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(WAITING_LINE);
        areas.add(ACTIVITY_AREA_A);
        areas.add(ACTIVITY_AREA_B);
        areas.add(ACTIVITY_AREA_C);
        areas.add(WAITING_AREA_SUPERVISORS);
        return areas;
    }

    public LifeGuard initLifeGuard(String identificator, String identificatorActividad) {
        LifeGuard guard = new SlideLifeGuard(identificator, getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(identificatorActividad, ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    public void initOtherLifeguards() {
        lifeguardB.start();
        lifeguardC.start();
    }
    
    protected synchronized void goIntoWaitingLine(ChildUser user) {
    	getWaitingLine().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
        user.setCurrentActivity(getIdentificator());
        user.getSupervisor().setCurrentActivity(getIdentificator());
        getPiscinaGrande().getWaitingAreaSupervisor().offer(user.getSupervisor()); // se van a la zona de espera de la piscina para que no les den permiso y se tiren por el tobogan
        getRegistry().registerUserInActivity(getPiscinaGrande().getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
    }
    
    protected synchronized void goOutWaitingLine(ChildUser user) {
    	getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
        getPiscinaGrande().getWaitingAreaSupervisor().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getPiscinaGrande().getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
    }

    @Override
    public boolean goIn(ChildUser user) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            user.setPermisoActividad(Permission.NONE);

            goIntoWaitingLine(user);
//            getWaitingLine().offer(user);
//            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
//            user.setCurrentActivity(getIdentificator());
//            user.getSupervisor().setCurrentActivity(getIdentificator());
//            getPiscinaGrande().getWaitingAreaSupervisor().offer(user.getSupervisor()); // se van a la zona de espera de la piscina para que no les den permiso y se tiren por el tobogan
//            getRegistry().registerUserInActivity(getPiscinaGrande().getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());

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
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
//            getPiscinaGrande().getWaitingAreaSupervisor().remove(user.getSupervisor());
//            getRegistry().unregisterUserFromActivity(getPiscinaGrande().getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());

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
            
            goIntoWaitingLine(user);
//            getWaitingLine().offer(user);
//            user.setCurrentActivity(getIdentificator());
//            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());

            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(user);
            }
            printStatus();
            resultado = true;
        } catch (SecurityException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());

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
            
            goIntoWaitingLine(user);
//            getWaitingLine().offer(user);
//            user.setCurrentActivity(getIdentificator());
//            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());

            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.ALLOWED) {
                goIntoSlide(user);
            }
            printStatus();
            resultado = true;
        } catch (SecurityException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());

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
        getRegistry().registerUserInActivity(getPiscinaGrande().getIdentificator(), ACTIVITY_AREA_A, user.getIdentificator());
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
                getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY_AREA_A, user.getIdentificator());
            } else {
                getSlideB().remove(user);
                getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY_AREA_B, user.getIdentificator());
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
            getSlideC().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY_AREA_C, user.getIdentificator());
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
                getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY_AREA_A, user.getIdentificator());
            } else {
                getSlideB().remove(user);
                getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY_AREA_B, user.getIdentificator());
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
        getPiscinaGrande().getSemaphore().acquire();
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
        if (user.getSlideTicket() == SlideTicket.SLIDE_A) {
            while (!getActivityArea().offer(user)) {
            }
            user.setCurrentActivity("ToboganA");
            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY_AREA_A, user.getIdentificator());

        } else if (user.getSlideTicket() == SlideTicket.SLIDE_B) {
            while (!getSlideB().offer(user)) {
            }
            user.setCurrentActivity("ToboganB");
            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY_AREA_B, user.getIdentificator());
        } else {
            while (!getSlideC().offer(user)) {
            }
            user.setCurrentActivity("ToboganC");
            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY_AREA_C, user.getIdentificator());
        }
    }

    public void printStatus() {
        System.out.println(getIdentificator() + " - cola de espera: " + getWaitingLine().toString());
        System.out.println(getIdentificator() + " - TOBOGAN A: " + getActivityArea().toString());
        System.out.println(getIdentificator() + " - TOBOGAN B: " + getSlideB().toString());
        System.out.println(getIdentificator() + " - TOBOGAN C: " + getSlideC().toString());
        System.out.println(getIdentificator() + " - zona de espera de actividad: " + getPiscinaGrande().getWaitingAreaSupervisor().toString());
    }

    public SlideTicket getTicket() {
        return ticket;
    }

//    public void setTicket(SlideTicket ticket) {
//        this.ticket = ticket;
//    }

    public ArrayBlockingQueue<User> getSlideC() {
        return slideC;
    }

//    public void setToboganC(ArrayBlockingQueue<User> toboganC) {
//        this.slideC = toboganC;
//    }

    public ArrayBlockingQueue<User> getSlideB() {
        return slideB;
    }

//    public void setToboganB(ArrayBlockingQueue<User> toboganB) {
//        this.slideB = toboganB;
//    }

    public MainPoolActivity getPiscinaGrande() {
        return mainPool;
    }

//    public void setPiscinaGrande(MainPoolActivity piscinaGrande) {
//        this.mainPool = piscinaGrande;
//    }

}
