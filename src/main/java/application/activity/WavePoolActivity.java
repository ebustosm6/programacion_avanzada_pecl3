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
    	LifeGuard guard = new WavePoolLifeGuard("VigilantePisinaOlas", getColaEspera(), getRegistro());
    	getRegistro().aniadirMonitorEnZona(getIdentificator(), "-monitor", guard.getIdentificator());
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
    public boolean goIn(ChildUser visitante) {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            visitante.setPermisoActividad(Permission.NONE);
            encolarNinio(visitante);
            imprimirColas();

            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() == Permission.NOT_ALLOWED) {
                throw new SecurityException();
            } else if (visitante.getPermisoActividad() == Permission.SUPERVISED) {
                encolarNinioActividad(visitante);
            } else if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                barrera.await();
                desencolarNinioColaEspera(visitante);
                getZonaActividad().offer(visitante);
                getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
                getZonaEsperaAcompanante().offer(visitante.getSupervisor());
                getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getSupervisor().getIdentificator());
            }

            resultado = true;
        } catch (SecurityException | InterruptedException | BrokenBarrierException e) {
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
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            imprimirColas();

            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            barrera.await();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE,visitante.getIdentificator());
            getZonaActividad().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY,visitante.getIdentificator());


            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE,visitante.getIdentificator());
            
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
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            imprimirColas();

            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            barrera.await();
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE,visitante.getIdentificator());
            getZonaActividad().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY,visitante.getIdentificator());


            resultado = true;

        } catch (SecurityException | BrokenBarrierException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE,visitante.getIdentificator());
            
            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");
            
        }
        return resultado;
    }

}
