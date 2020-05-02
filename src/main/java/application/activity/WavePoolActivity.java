package application.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.LifeGuard;
import application.lifeguard.WavePoolLifeGuard;
import application.user.AdultUser;
import application.user.ChildUser;
import application.user.YoungUser;

public class WavePoolActivity extends Activity {

    private static String IDENTIFICATOR = "ActividadPiscinaOlas";
    private static String LIFEGUARD_IDENTIFICATOR = "VigilantePisinaOlas";
    private CyclicBarrier enteringBarrier = new CyclicBarrier(ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_ENTRANCE_USERS);
//	private static final String WAITING_LINE = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE; 
//    private static final String ACTIVITY = ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY; 
//    private static final String WAITING_AREA_SUPERVISORS = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS;

    public WavePoolActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_CAPACITY, userRegistry);
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MIN_MILISECONDS * Math.random()));
    }
    
    @Override
    protected LifeGuard initActivityLifeguard() {
    	LifeGuard guard = new WavePoolLifeGuard(LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getRegistry());
    	getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }
    
//    @Override
//    public List<String> getActivitySubareas() {
//    	ArrayList<String> areas = new ArrayList<>();
//    	areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE);
//    	areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY);
//    	areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS);
//    	return areas;
//    }

    @Override
    public boolean goIn(ChildUser user) {
    	boolean resultado = false;
        waitIfProgramIsStopped();
        
        try {
            user.setPermisoActividad(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() == Permission.NOT_ALLOWED) {
                throw new SecurityException();
            } else if (user.getPermisoActividad() == Permission.SUPERVISED) {
                passFromWaitingLineToActivity(user);
            } else if (user.getPermisoActividad() == Permission.ALLOWED) {
            	getEnteringBarrier().await();
                goOutWaitingLine(user);
                goIntoActivityAreaWithoutSupervisor(user);
//                getActivityArea().offer(user);
//                getRegistry().registerUserInActivity(getIdentificator(),  ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
//                getWaitingAreaSupervisor().offer(user.getSupervisor());
//                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
            }

            resultado = true;
        } catch (SecurityException | InterruptedException | BrokenBarrierException e) {
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

            if (user.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            getEnteringBarrier().await();
            resultado = passFromWaitingLineToActivity(user);
//            goOutWaitingLine(user);
////            getWaitingLine().remove(user);
////            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE,user.getIdentificator());
//            goIntoActivityArea(user);
////            getActivityArea().offer(user);
////            getRegistry().registerUserInActivity(getIdentificator(),  ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY,user.getIdentificator());
//            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE,user.getIdentificator());
            
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
            getEnteringBarrier().await();
            resultado = passFromWaitingLineToActivity(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE,user.getIdentificator());
//            getActivityArea().offer(user);
//            getRegistry().registerUserInActivity(getIdentificator(),  ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY,user.getIdentificator());
//            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE,user.getIdentificator());
            
            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");
            
        }
        return resultado;
    }
    
    public CyclicBarrier getEnteringBarrier() {
		return enteringBarrier;
	}

}
