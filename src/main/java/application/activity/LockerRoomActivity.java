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
        LifeGuard guard = new LockerRoomLifeGuard("VigilanteVestuarios", getColaEspera(), getRegistro());
    	getRegistro().registerLifeguard(getIdentificator(), "-monitor", guard.getIdentificator());
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
    	System.out.println("******************************");
    	System.out.println(getIdentificator() + " - cola de espera: " + getColaEspera().toString());
    	System.out.println(getIdentificator() + " - zona de actividad: " + getZonaActividad().toString());
    	System.out.println(getIdentificator() + " - zona de actividad adultos: " + getZonaActividadAdultos().toString());
    	System.out.println("******************************");
    }

    @Override
    public boolean goIn(ChildUser visitante) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            visitante.setPermisoActividad(Permission.NONE);
            encolarNinio(visitante);
            printStatus();
            
            waitForLifeGuardPermission(visitante);
            
            if (visitante.getPermisoActividad() == Permission.SUPERVISED) {
                encolarNinioActividad(visitante);
            } else if (visitante.getPermisoActividad() == Permission.ALLOWED) {
            	desencolarNinioColaEspera(visitante);
            	getZonaActividad().offer(visitante);
                getRegistro().registerUserInActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
            	getZonaActividadAdultos().offer(visitante.getSupervisor());
                getRegistro().registerUserInActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getSupervisor().getIdentificator());
            }

            resultado = true;
        } catch (SecurityException e) {
            desencolarNinioColaEspera(visitante);
            
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
            getRegistro().registerUserInActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            printStatus();

            waitForLifeGuardPermission(visitante);
            
            getColaEspera().remove(visitante);
            getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            getZonaActividadAdultos().offer(visitante);
            getRegistro().registerUserInActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getIdentificator());
            imprimirZonaActividadAdultos();
            resultado = true;

        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            
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
            getRegistro().registerUserInActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            printStatus();
            
            waitForLifeGuardPermission(visitante);
            
            getColaEspera().remove(visitante);
            getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            getZonaActividad().offer(visitante);
            getRegistro().registerUserInActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
            imprimirZonaActividadAdultos();
            resultado = true;

        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            
            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }
    

    private void imprimirZonaActividadAdultos() {
        System.out.println("La actividad: " + getIdentificator() + " tiene una cola de adultos: " + zonaActividadAdultos.toString());
    }

    @Override
    public void goOut(AdultUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
    	getZonaActividadAdultos().remove(visitante);
        getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getIdentificator());
        
        onGoOutSuccess(visitante);
//        visitante.setPermisoActividad(Permission.NONE);
//        imprimirColas();
//        visitante.setCurrentActivity("ParqueAcuatico");
    }
    
    
//    public void goOut(YoungUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
//    	getZonaActividad().remove(visitante);
//        getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
//        visitante.setPermisoActividad(Permission.NONE);
//        visitante.setCurrentActivity("ParqueAcuatico");
//    }

//    public void goOut(ChildUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
//        if (visitante.getPermisoActividad() == Permission.SUPERVISED) {
//            desencolarNinio(visitante);
//        } else {
//            getZonaActividad().remove(visitante);
//            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
//            getZonaActividadAdultos().remove(visitante.getSupervisor());
//            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getSupervisor().getIdentificator());
//        }
//        visitante.setPermisoActividad(Permission.NONE);
//        visitante.setCurrentActivity("ParqueAcuatico");
//    }

    public ArrayBlockingQueue<User> getZonaActividadAdultos() {
        return zonaActividadAdultos;
    }
    

    public void setZonaActividadAdultos(ArrayBlockingQueue<User> zonaActividadAdultos) {
        this.zonaActividadAdultos = zonaActividadAdultos;
    }
    

}
