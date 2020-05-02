package application.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.LifeGuard;
import application.lifeguard.LockerRoomLifeGuard;
import application.user.AdultUser;
import application.user.ChildUser;
import application.user.User;
import application.user.YoungUser;

public class LockerRoomActivity extends Activity {

    private static String IDENTIFICATOR = "ActividadVestuario";
    private static String LIFEGUARD_IDENTIFICATOR = "VigilanteVestuarios";
    private ArrayBlockingQueue<User> activityAreaAdultUsers;
    private static boolean IS_FAIR_QUEUE = true;
//    private static final String WAITING_LINE = ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE;
//    private static final String ACTIVITY = ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY; 
    private static final String ACTIVITY_AREA_ADULT_USERS = "-zonaActividadAdultos"; 

    public LockerRoomActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CAPACITY,
        		ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CHILD_CAPACITY, IS_FAIR_QUEUE, userRegistry);
        this.activityAreaAdultUsers = new ArrayBlockingQueue<>(ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_ADULT_CAPACITY, true);
    }
    
    @Override
    public long getActivityTime() {
        return ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_MILISECONDS;
    }
    
    @Override
    protected LifeGuard initActivityLifeguard() {
        LifeGuard guard = new LockerRoomLifeGuard(LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getRegistry());
    	getRegistry().registerLifeguard(getIdentificator(),  ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    @Override
    protected List<String> getActivitySubareas() {
    	ArrayList<String> areas = new ArrayList<>();
    	areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE);
    	areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY);
    	areas.add(ACTIVITY_AREA_ADULT_USERS);
    	return areas;
    }
    
    @Override
    public void printStatus() {
    	System.out.println(getIdentificator() + " - cola de espera: " + getWaitingLine().toString());
    	System.out.println(getIdentificator() + " - zona de actividad: " + getActivityArea().toString());
    	System.out.println(getIdentificator() + " - zona de actividad adultos: " + getActivityAreaAdultUsers().toString());
    }
    
    protected synchronized void goIntoActivityAreaWithoutSupervisor(ChildUser user) {
    	getActivityArea().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
    	getActivityAreaAdultUsers().offer(user.getSupervisor());
        getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY_AREA_ADULT_USERS, user.getSupervisor().getIdentificator());
    }
    
    protected synchronized void goIntoActivityArea(AdultUser user) {
    	getActivityAreaAdultUsers().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY_AREA_ADULT_USERS, user.getIdentificator());
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
            
            if (user.getPermisoActividad() == Permission.SUPERVISED) {
                resultado = passFromWaitingLineToActivity(user);
            } else if (user.getPermisoActividad() == Permission.ALLOWED) {
            	goOutWaitingLine(user);
            	goIntoActivityAreaWithoutSupervisor(user);
//            	getActivityArea().offer(user);
//                getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
//            	getActivityAreaAdultUsers().offer(user.getSupervisor());
//                getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY_AREA_ADULT_USERS, user.getSupervisor().getIdentificator());
            	resultado = true;
            }

            
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
//            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            
            printStatus();

            waitForLifeGuardPermission(user);
            
            goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            goIntoActivityArea(user);
//            getActivityAreaAdultUsers().offer(user);
//            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY_AREA_ADULT_USERS, user.getIdentificator());
            
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
    
//    @Override
//    public boolean goIn(YoungUser user) throws InterruptedException {
//    	boolean resultado = false;
////        getRegistro().comprobarDetenerReanudar();
//        waitIfProgramIsStopped();
//        
//        try {
//        	
//            user.setPermisoActividad(Permission.NONE);
//            goIntoWaitingLine(user);
////            getWaitingLine().offer(user);
////            user.setCurrentActivity(getIdentificator());
////            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
//            
//            printStatus();
//            
//            waitForLifeGuardPermission(user);
//            resultado = passFromWaitingLineToActivity(user);
////            goOutWaitingLine(user);
//////            getWaitingLine().remove(user);
//////            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
////            goIntoActivityArea(user);
//////            getActivityArea().offer(user);
//////            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
//            
//
//        } catch (SecurityException e) {
//        	goOutWaitingLine(user);
////            getWaitingLine().remove(user);
////            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
//            
//            onGoOutSuccess(user);
////            user.setPermisoActividad(Permission.NONE);
////            imprimirColas();
////            user.setCurrentActivity("ParqueAcuatico");
//
//        }
//        return resultado;
//    }
    
    protected void onTryGoOut(AdultUser user) {
    	getActivityAreaAdultUsers().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ACTIVITY_AREA_ADULT_USERS, user.getIdentificator());
    }
    
    public void goOut(AdultUser user) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        onTryGoOut(user);
//    	getActivityAreaAdultUsers().remove(user);
//        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, user.getIdentificator());
        
        onGoOutSuccess(user);
//        user.setPermisoActividad(Permission.NONE);
//        imprimirColas();
//        user.setCurrentActivity("ParqueAcuatico");
    }
    
    
//    public void goOut(YoungUser user) {
//        getRegistro().comprobarDetenerReanudar();
//    	getZonaActividad().remove(user);
//        getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, user.getIdentificator());
//        user.setPermisoActividad(Permission.NONE);
//        user.setCurrentActivity("ParqueAcuatico");
//    }

//    public void goOut(ChildUser user) {
//        getRegistro().comprobarDetenerReanudar();
//        if (user.getPermisoActividad() == Permission.SUPERVISED) {
//            desencolarNinio(user);
//        } else {
//            getZonaActividad().remove(user);
//            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, user.getIdentificator());
//            getZonaActividadAdultos().remove(user.getSupervisor());
//            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
//        }
//        user.setPermisoActividad(Permission.NONE);
//        user.setCurrentActivity("ParqueAcuatico");
//    }

    public ArrayBlockingQueue<User> getActivityAreaAdultUsers() {
        return activityAreaAdultUsers;
    }
    

}
