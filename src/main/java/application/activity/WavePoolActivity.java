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

//    private static int CAPACITY = ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_CAPACITY;
    private static String IDENTIFICATOR = "ActividadPiscinaOlas";
    private CyclicBarrier barrera = new CyclicBarrier(2);
    private static final String WAITING_LINE = "-colaEspera";
    private static final String ACTIVITY = "-zonaActividad";
    private static final String WAITING_AREA_SUPERVISORS = "-zonaEsperaAcompanante";

    public WavePoolActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_CAPACITY, userRegistry);

    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_MIN_MILISECONDS * Math.random()));
    }
    
    @Override
    public LifeGuard initActivityLifeguard() {
    	LifeGuard guard = new WavePoolLifeGuard("VigilantePisinaOlas", getWaitingLine(), getRegistry());
    	getRegistry().registerLifeguard(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }
    
    @Override
    public List<String> getActivitySubareas() {
    	ArrayList<String> areas = new ArrayList<>();
    	areas.add(WAITING_LINE);
    	areas.add(ACTIVITY);
    	areas.add(WAITING_AREA_SUPERVISORS);
    	return areas;
    }

    @Override
    public boolean goIn(ChildUser user) {
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
                passFromWaitingLineToActivity(user);
            } else if (user.getPermisoActividad() == Permission.ALLOWED) {
                barrera.await();
                goOutWaitingLine(user);
                getActivityArea().offer(user);
                getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
                getWaitingAreaSupervisor().offer(user.getSupervisor());
                getRegistry().registerUserInActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
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
//            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            
            printStatus();

            waitForLifeGuardPermission(user);

            if (user.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            barrera.await();
            resultado = passFromWaitingLineToActivity(user);
//            goOutWaitingLine(user);
////            getWaitingLine().remove(user);
////            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE,user.getIdentificator());
//            goIntoActivityArea(user);
////            getActivityArea().offer(user);
////            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY,user.getIdentificator());
//            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE,user.getIdentificator());
            
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

            if (user.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            barrera.await();
            resultado = passFromWaitingLineToActivity(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE,user.getIdentificator());
//            getActivityArea().offer(user);
//            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY,user.getIdentificator());
//            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
        	goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE,user.getIdentificator());
            
            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");
            
        }
        return resultado;
    }

}
