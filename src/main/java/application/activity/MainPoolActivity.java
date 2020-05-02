package application.activity;

import java.util.concurrent.Semaphore;
import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.LifeGuard;
import application.lifeguard.MainPoolLifeGuard;
import application.user.User;
import application.user.AdultUser;
import application.user.YoungUser;
import application.user.ChildUser;

public class MainPoolActivity extends Activity {

//    private static int CAPACITY = ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY;
    private static String IDENTIFICATOR = "ActividadPiscinaGrande";
    private Semaphore semaforo;
    private static final String WAITING_LINE = "-colaEspera";
    private static final String ACTIVITY = "-zonaActividad";
    private static final String WAITING_AREA_SUPERVISORS = "-zonaEsperaAcompaniante";

    public MainPoolActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, userRegistry);
        this.semaforo = new Semaphore(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, true);
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS * Math.random()));
    }

    @Override
    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new MainPoolLifeGuard("VigilantePiscinaGrande", getWaitingLine(), getActivityArea(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }

    public synchronized void encolarNinioActividadSemaforo(ChildUser user) throws InterruptedException{
        getWaitingLine().remove(user);
        getSemaforo().acquire(2);
        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
        getWaitingLine().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getSupervisor().getIdentificator());
        while (!getActivityArea().offer(user)) {
            //espera
        }
        getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
        while (!getActivityArea().offer(user.getSupervisor())) {
            //espera
        }
        getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getSupervisor().getIdentificator());
    }
    
    public synchronized void goOutActivityArea(ChildUser user) {
        getActivityArea().remove(user);
        semaforo.release();
        getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
        getActivityArea().remove(user.getSupervisor());
        semaforo.release();
        getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY, user.getSupervisor().getIdentificator());
    }

    @Override
    public boolean goIn(ChildUser user) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            user.setPermisoActividad(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.NOT_ALLOWED) {
                throw new SecurityException();
            } else if (user.getPermisoActividad() == Permission.SUPERVISED) {
                encolarNinioActividadSemaforo(user);
            } else if (user.getPermisoActividad() == Permission.ALLOWED) {
                goOutWaitingLine(user);
                semaforo.acquire();
                getActivityArea().offer(user);
                getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
                getWaitingAreaSupervisor().offer(user.getSupervisor());
                getRegistry().registerUserInActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
            }
            resultado = true;
        } catch (SecurityException e) {
            goOutWaitingLine(user);
            
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
            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.ALLOWED) {
                getWaitingLine().remove(user);
                semaforo.acquire();
                getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
                getActivityArea().offer(user);
                getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
                resultado = true;
            } else {
                throw new SecurityException();
            }

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
            getWaitingLine().offer(user);
            user.setCurrentActivity(getIdentificator());
            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }

            getWaitingLine().remove(user);
            semaforo.acquire();
            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            getActivityArea().offer(user);
            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
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
    public void onDoActivityFail(User user) {
    	if (user instanceof ChildUser) {
            getActivityArea().remove(user);
            getActivityArea().remove(user.getSupervisor());
            semaforo.release(2);
        } else {
            getActivityArea().remove(user);
            semaforo.release();
        }
        user.setCurrentActivity("ParqueAcuatico");
    }
    
//    public void doActivity(User user) {
//        getRegistro().comprobarDetenerReanudar();
//        try {
//            imprimirColas();
//            user.sleep(getActivityTime());
//        } catch (InterruptedException e) {
//            if (user instanceof ChildUser) {
//                getZonaActividad().remove(user);
//                getZonaActividad().remove(user.getSupervisor());
//                semaforo.release(2);
//            } else {
//                getZonaActividad().remove(user);
//                semaforo.release();
//            }
//            user.setCurrentActivity("ParqueAcuatico");
//        }
//    }

    public void onTryGoOut(User user) {
    	getActivityArea().remove(user);
    	semaforo.release();
    	getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
    }
    
//    public void goOut(AdultUser user) {
////        getRegistro().comprobarDetenerReanudar();
//        waitIfProgramIsStopped();
//        try {
//        	onTryGoOut(user);
////            getZonaActividad().remove(user);
////            semaforo.release();
////            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, user.getIdentificator());
//            
//            onGoOutSuccess(user);
////            user.setPermisoActividad(Permission.NONE);
////            imprimirColas();
////            user.setCurrentActivity("ParqueAcuatico");
//        } catch (Exception e) {
//        }
//    }

//    public void goOut(YoungUser user) {
////        getRegistro().comprobarDetenerReanudar();
//        waitIfProgramIsStopped();
//        try {
//        	onTryGoOut(user);
////            getZonaActividad().remove(user);
////            semaforo.release();
////            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, user.getIdentificator());
//            
//            onGoOutSuccess(user);
////            user.setPermisoActividad(Permission.NONE);
////            imprimirColas();
////            user.setCurrentActivity("ParqueAcuatico");
//        } catch (Exception e) {
//        }
//    }

    public void goOut(ChildUser user) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
            if (user.getPermisoActividad() == Permission.SUPERVISED) {
                goOutActivityArea(user);
            } else {
                getActivityArea().remove(user);
                semaforo.release();
                getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
                getWaitingAreaSupervisor().remove(user.getSupervisor());
                getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());

            }
            
            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);// poner el permiso a false (que deambulen por ahi sin permiso)
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");
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
