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

    private static String IDENTIFICATOR = "ActividadPiscinaGrande";
    private static String LIFEGUARD_IDENTIFICATOR = "VigilantePiscinaGrande";
    private Semaphore semaphore;
//    private static final String WAITING_LINE = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE; 
//    private static final String ACTIVITY = ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY; 
//    private static final String WAITING_AREA_SUPERVISORS = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS;

    public MainPoolActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, userRegistry);
        this.semaphore = new Semaphore(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, true);
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS * Math.random()));
    }

    @Override
    protected LifeGuard initActivityLifeguard() {
        LifeGuard guard = new MainPoolLifeGuard(LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getActivityArea(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    protected synchronized void encolarNinioActividadSemaforo(ChildUser user) throws InterruptedException{
        getWaitingLine().remove(user);
        getSemaphore().acquire(2);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
        getWaitingLine().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getSupervisor().getIdentificator());
        while (!getActivityArea().offer(user)) {
            //espera
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        while (!getActivityArea().offer(user.getSupervisor())) {
            //espera
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getSupervisor().getIdentificator());
    }
    
    @Override
    protected synchronized void goOutActivityArea(ChildUser user) {
        getActivityArea().remove(user);
        semaphore.release();
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getActivityArea().remove(user.getSupervisor());
        semaphore.release();
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getSupervisor().getIdentificator());
    }
    
    @Override
    protected synchronized void goOutActivityAreaWithoutSupervisor(ChildUser user) {
        getActivityArea().remove(user);
        semaphore.release();
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getWaitingAreaSupervisor().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
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
                semaphore.acquire();
                getActivityArea().offer(user);
                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
                getWaitingAreaSupervisor().offer(user.getSupervisor());
                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
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
            goIntoWaitingLine(user);
//            getWaitingLine().offer(user);
//            user.setCurrentActivity(getIdentificator());
//            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
            
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.ALLOWED) {
                getWaitingLine().remove(user);
                semaphore.acquire();
                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
                goIntoActivityArea(user);
//                getActivityArea().offer(user);
//                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
                
                resultado = true;
            } else {
                throw new SecurityException();
            }

        } catch (SecurityException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());

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
//            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
            
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }

            getWaitingLine().remove(user);
            semaphore.acquire();
            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
            goIntoActivityArea(user);
//            getActivityArea().offer(user);
//            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
            resultado = true;

        } catch (SecurityException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());

            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }

    @Override
    protected void onDoActivityFail(User user) {
    	if (user instanceof ChildUser) {
            getActivityArea().remove(user);
            getActivityArea().remove(user.getSupervisor());
            semaphore.release(2);
        } else {
            getActivityArea().remove(user);
            semaphore.release();
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

    protected void onTryGoOut(User user) {
    	getActivityArea().remove(user);
    	semaphore.release();
    	getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
    }
    
//    public void goOut(AdultUser user) {
////        getRegistro().comprobarDetenerReanudar();
//        waitIfProgramIsStopped();
//        try {
//        	onTryGoOut(user);
////            getZonaActividad().remove(user);
////            semaforo.release();
////            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
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
////            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
//            
//            onGoOutSuccess(user);
////            user.setPermisoActividad(Permission.NONE);
////            imprimirColas();
////            user.setCurrentActivity("ParqueAcuatico");
//        } catch (Exception e) {
//        }
//    }

//    public void goOut(ChildUser user) {
////        getRegistro().comprobarDetenerReanudar();
//        waitIfProgramIsStopped();
//        try {
//            if (user.getPermisoActividad() == Permission.SUPERVISED) {
//                goOutActivityArea(user);
//            } else {
//            	goOutActivityAreaWithoutSupervisor(user);
////                getActivityArea().remove(user);
////            	  semaphore.release();
////                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
////                getWaitingAreaSupervisor().remove(user.getSupervisor());
////                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
//
//            }
//            
//            onGoOutSuccess(user);
////            user.setPermisoActividad(Permission.NONE);// poner el permiso a false (que deambulen por ahi sin permiso)
////            imprimirColas();
////            user.setCurrentActivity("ParqueAcuatico");
//        } catch (Exception e) {
//        }
//    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

//    public void setSemaforo(Semaphore semaforo) {
//        this.semaphore = semaforo;
//    }

}
