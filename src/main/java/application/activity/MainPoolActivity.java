package application.activity;

import java.util.concurrent.Semaphore;
import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.MainPoolLifeGuard;
import application.user.User;
import application.user.AdultUser;
import application.user.YoungUser;
import application.user.ChildUser;

public class MainPoolActivity extends BaseActivity {

//    private static String IDENTIFICATOR = ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_NAME; //"ActividadPiscinaGrande";
//    private static String LIFEGUARD_IDENTIFICATOR = ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_LIFEGUARD_IDENTIFICATOR; //"VigilantePiscinaGrande";
    private Semaphore semaphore;
//    private static final String WAITING_LINE = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE; 
//    private static final String ACTIVITY = ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY; 
//    private static final String WAITING_AREA_SUPERVISORS = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS;

    public MainPoolActivity(UserRegistry userRegistry) {
        super(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_NAME, ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, userRegistry);
        this.semaphore = new Semaphore(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, true);
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS * Math.random()));
    }

    @Override
    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new MainPoolLifeGuard(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getActivityArea(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

//    protected synchronized void encolarNinioActividadSemaforo(ChildUser user) throws InterruptedException{
//    	goOutWaitingLine(user);
////    	getWaitingLine().remove(user);
////        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
////        getWaitingLine().remove(user.getSupervisor());
////        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getSupervisor().getIdentificator());
//        
//    	goIntoActivityArea(user);
////        while (!getActivityArea().offer(user)) {
////            //espera
////        }
////        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
////        while (!getActivityArea().offer(user.getSupervisor())) {
////            //espera
////        }
////        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getSupervisor().getIdentificator());
//    }
    
    
    @Override
    protected synchronized void goOutActivityArea(ChildUser user) {
        getActivityArea().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getActivityArea().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getSupervisor().getIdentificator());
        getSemaphore().release(2);
    }
    
    @Override
    protected synchronized void goOutActivityAreaWithoutSupervisor(ChildUser user) {
        getActivityArea().remove(user);
        getSemaphore().release();
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getWaitingAreaSupervisor().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
    }
    
    @Override
    public boolean goIn(ChildUser user) throws InterruptedException {
    	boolean result = false;
        waitIfProgramIsStopped();
        
        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() == Permission.NOT_ALLOWED) {
                throw new SecurityException();
            } else if (user.getActivityPermissionType() == Permission.SUPERVISED) {
            	getSemaphore().acquire(2);
            	passFromWaitingLineToActivity(user);
//                encolarNinioActividadSemaforo(user);
//            } else if (user.getPermisoActividad() == Permission.ALLOWED) {
//            	getSemaphore().acquire();
//            	goOutWaitingLine(user);
//                goIntoActivityAreaWithoutSupervisor(user);
////                getActivityArea().offer(user);
////                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
////                getWaitingAreaSupervisor().offer(user.getSupervisor());
////                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
            	result = true;
            }
            
        } catch (SecurityException e) {
            goOutWaitingLine(user);
            
            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");

        }
        return result;
    }
    
    @Override
    public boolean goIn(AdultUser user) throws InterruptedException {
    	boolean result = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
//            getWaitingLine().offer(user);
//            user.setCurrentActivity(getIdentificator());
//            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
            
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() == Permission.ALLOWED) {
            	getSemaphore().acquire();
                goOutWaitingLine(user);
//                getWaitingLine().remove(user);
//                semaphore.acquire();
//                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
                goIntoActivityArea(user);
//                getActivityArea().offer(user);
//                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
                
                result = true;
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
        return result;
    }

    @Override
    public boolean goIn(YoungUser user) throws InterruptedException {
    	boolean result = false;
//        getRegistro().comprobarDetenerReanudar();
    	waitIfProgramIsStopped();
        
        try {
            user.setActivityPermissionType(Permission.NONE);
            
            goIntoWaitingLine(user);
//            getWaitingLine().offer(user);
//            user.setCurrentActivity(getIdentificator());
//            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
            
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() != Permission.ALLOWED) {
                throw new SecurityException();
            }

            getSemaphore().acquire();
            goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            semaphore.acquire();
//            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
            goIntoActivityArea(user);
//            getActivityArea().offer(user);
//            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
            result = true;

        } catch (SecurityException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());

            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");

        }
        return result;
    }

    @Override
    protected void onDoActivityFail(User user) {
    	if (user instanceof ChildUser) {
            getActivityArea().remove(user);
            getActivityArea().remove(user.getSupervisor());
            getSemaphore().release(2);
        } else {
            getActivityArea().remove(user);
            getSemaphore().release();
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
    	getSemaphore().release();
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
