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

//    private static int CAPACIDAD_TOTAL = ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CAPACITY;
//    private static int CAPACIDAD_ADULTOS = ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_ADULT_CAPACITY;
//    private static int CAPACIDAD_NINIOS = ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CHILD_CAPACITY;
    private static String IDENTIFICATOR = "ActividadVestuario";
    private ArrayBlockingQueue<User> zonaActividadAdultos;
    private static boolean IS_FAIR_QUEUE = true;
    private static final String WAITING_LINE = "-colaEspera"; 
    private static final String ACTIVITY = "-zonaActividad";
    private static final String WAITING_AREA_SUPERVISORS = "-zonaActividadAdultos"; 

    public LockerRoomActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CAPACITY,
        		ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CHILD_CAPACITY, IS_FAIR_QUEUE, userRegistry);
        this.zonaActividadAdultos = new ArrayBlockingQueue<>(ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_ADULT_CAPACITY, true);
    }
    
    @Override
    public long getActivityTime() {
        return ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_MILISECONDS;
    }
    
    @Override
    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new LockerRoomLifeGuard("VigilanteVestuarios", getWaitingLine(), getRegistry());
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
    public void printStatus() {
    	System.out.println(getIdentificator() + " - cola de espera: " + getWaitingLine().toString());
    	System.out.println(getIdentificator() + " - zona de actividad: " + getActivityArea().toString());
    	System.out.println(getIdentificator() + " - zona de actividad adultos: " + getZonaActividadAdultos().toString());
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
                passFromWaitingLineToActivity(user);
            } else if (user.getPermisoActividad() == Permission.ALLOWED) {
            	goOutWaitingLine(user);
            	getActivityArea().offer(user);
                getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
            	getZonaActividadAdultos().offer(user.getSupervisor());
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
            
            goIntoWaitingLine(user);
//            getWaitingLine().offer(user);
//            user.setCurrentActivity(getIdentificator());
//            getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            
            printStatus();

            waitForLifeGuardPermission(user);
            
            goOutWaitingLine(user);
//            getWaitingLine().remove(user);
//            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            getZonaActividadAdultos().offer(user);
            getRegistry().registerUserInActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, user.getIdentificator());
            
            imprimirZonaActividadAdultos();
            resultado = true;

        } catch (SecurityException e) {
            getWaitingLine().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            
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
            resultado = passFromWaitingLineToActivity(user);
//            goOutWaitingLine(user);
////            getWaitingLine().remove(user);
////            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
//            goIntoActivityArea(user);
////            getActivityArea().offer(user);
////            getRegistry().registerUserInActivity(getIdentificator(), ACTIVITY, user.getIdentificator());
            
            imprimirZonaActividadAdultos();

        } catch (SecurityException e) {
            getWaitingLine().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
            
            onGoOutSuccess(user);
//            user.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            user.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }
    

    private void imprimirZonaActividadAdultos() {
        System.out.println("La actividad: " + getIdentificator() + " tiene una cola de adultos: " + zonaActividadAdultos.toString());
    }

    public void goOut(AdultUser user) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
    	getZonaActividadAdultos().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, user.getIdentificator());
        
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

    public ArrayBlockingQueue<User> getZonaActividadAdultos() {
        return zonaActividadAdultos;
    }
    

    public void setZonaActividadAdultos(ArrayBlockingQueue<User> zonaActividadAdultos) {
        this.zonaActividadAdultos = zonaActividadAdultos;
    }
    

}
