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
    
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS * Math.random()));
    }

    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new MainPoolLifeGuard("VigilantePiscinaGrande", getColaEspera(), getZonaActividad(), getRegistro());
        getRegistro().aniadirMonitorEnZona(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }

    public synchronized void encolarNinioActividadSemaforo(ChildUser visitante) throws InterruptedException{
        getColaEspera().remove(visitante);
        getSemaforo().acquire(2);
        getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
        getColaEspera().remove(visitante.getSupervisor());
        getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getSupervisor().getIdentificator());
        while (!getZonaActividad().offer(visitante)) {
            //espera
        }
        getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
        while (!getZonaActividad().offer(visitante.getSupervisor())) {
            //espera
        }
        getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getSupervisor().getIdentificator());
    }
    
    public synchronized void desencolarNinio(ChildUser visitante) {
        getZonaActividad().remove(visitante);
        semaforo.release();
        getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
        getZonaActividad().remove(visitante.getSupervisor());
        semaforo.release();
        getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getSupervisor().getIdentificator());
    }

    public boolean goIn(ChildUser visitante) throws InterruptedException {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        int espaciosOcupados = 2;
        try {
            visitante.setPermisoActividad(Permission.NONE);
            encolarNinio(visitante);
            imprimirColas();

            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() == Permission.NOT_ALLOWED) {
                throw new SecurityException();
            } else if (visitante.getPermisoActividad() == Permission.SUPERVISED) {
                encolarNinioActividadSemaforo(visitante);
            } else if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                espaciosOcupados = 1;
                desencolarNinioColaEspera(visitante);
                semaforo.acquire();
                getZonaActividad().offer(visitante);
                getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
                getZonaEsperaAcompanante().offer(visitante.getSupervisor());
                getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getSupervisor().getIdentificator());
            }
            resultado = true;
        } catch (SecurityException e) {
            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            desencolarNinioColaEspera(visitante);
            visitante.setPermisoActividad(Permission.NONE);
            visitante.setCurrentActivity("ParqueAcuatico");
            imprimirColas();

        }
        return resultado;
    }

    public boolean goIn(AdultUser visitante) throws InterruptedException {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
        try {
            visitante.setPermisoActividad(Permission.NONE);
            getColaEspera().offer(visitante);
            visitante.setCurrentActivity(getIdentificator());
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            imprimirColas();

            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                getColaEspera().remove(visitante);
                semaforo.acquire();
                getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
                getZonaActividad().offer(visitante);
                getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
                resultado = true;
            } else {
                throw new SecurityException();
            }

        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());

            visitante.setPermisoActividad(Permission.NONE);
            imprimirColas();
            visitante.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }

    public boolean goIn(YoungUser visitante) throws InterruptedException {
        getRegistro().comprobarDetenerReanudar();
        boolean resultado = false;
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

            getColaEspera().remove(visitante);
            semaforo.acquire();
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            getZonaActividad().offer(visitante);
            getRegistro().aniadirVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
            resultado = true;

        } catch (SecurityException e) {
            getColaEspera().remove(visitante);
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_LINE, visitante.getIdentificator());

            visitante.setPermisoActividad(Permission.NONE);
            imprimirColas();
            visitante.setCurrentActivity("ParqueAcuatico");

        }
        return resultado;
    }

    public void doActivity(User visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            imprimirColas();
            visitante.sleep(getActivityTime());
        } catch (InterruptedException e) {
            if (visitante instanceof ChildUser) {
                getZonaActividad().remove(visitante);
                getZonaActividad().remove(visitante.getSupervisor());
                semaforo.release(2);
            } else {
                getZonaActividad().remove(visitante);
                semaforo.release();
            }
            visitante.setCurrentActivity("ParqueAcuatico");
        }
    }

    public void goOut(AdultUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            getZonaActividad().remove(visitante);
            semaforo.release();
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
            
            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goOut(YoungUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            getZonaActividad().remove(visitante);
            semaforo.release();
            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
            
            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goOut(ChildUser visitante) {
        getRegistro().comprobarDetenerReanudar();
        try {
            if (visitante.getPermisoActividad() == Permission.SUPERVISED) {
                desencolarNinio(visitante);
            } else {
                getZonaActividad().remove(visitante);
                semaforo.release();
                getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
                getZonaEsperaAcompanante().remove(visitante.getSupervisor());
                getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getSupervisor().getIdentificator());

            }
            
            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);// poner el permiso a false (que deambulen por ahi sin permiso)
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");
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
