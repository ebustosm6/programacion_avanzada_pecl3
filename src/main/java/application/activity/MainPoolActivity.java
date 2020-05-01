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
        LifeGuard guard = new MainPoolLifeGuard("VigilantePiscinaGrande", getColaEspera(), getZonaActividad(), getRegistro());
        getRegistro().registerLifeguard(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }

    public synchronized void encolarNinioActividadSemaforo(ChildUser visitante) throws InterruptedException{
        getColaEspera().remove(visitante);
        getSemaforo().acquire(2);
        getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
        getColaEspera().remove(visitante.getSupervisor());
        getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getSupervisor().getIdentificator());
        while (!getZonaActividad().offer(visitante)) {
            //espera
        }
        getRegistro().registerUserInActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
        while (!getZonaActividad().offer(visitante.getSupervisor())) {
            //espera
        }
        getRegistro().registerUserInActivity(getIdentificator(), ACTIVITY, visitante.getSupervisor().getIdentificator());
    }
    
    public synchronized void desencolarNinio(ChildUser visitante) {
        getZonaActividad().remove(visitante);
        semaforo.release();
        getRegistro().unregisterUserFromActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
        getZonaActividad().remove(visitante.getSupervisor());
        semaforo.release();
        getRegistro().unregisterUserFromActivity(getIdentificator(), ACTIVITY, visitante.getSupervisor().getIdentificator());
    }

    @Override
    public boolean goIn(ChildUser visitante) throws InterruptedException {
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
                encolarNinioActividadSemaforo(visitante);
            } else if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                desencolarNinioColaEspera(visitante);
                semaforo.acquire();
                getZonaActividad().offer(visitante);
                getRegistro().registerUserInActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
                getZonaEsperaAcompanante().offer(visitante.getSupervisor());
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
            imprimirColas();

            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() == Permission.ALLOWED) {
                getColaEspera().remove(visitante);
                semaforo.acquire();
                getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
                getZonaActividad().offer(visitante);
                getRegistro().registerUserInActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
                resultado = true;
            } else {
                throw new SecurityException();
            }

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
            imprimirColas();

            waitForLifeGuardPermission(visitante);

            if (visitante.getPermisoActividad() != Permission.ALLOWED) {
                throw new SecurityException();
            }

            getColaEspera().remove(visitante);
            semaforo.acquire();
            getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
            getZonaActividad().offer(visitante);
            getRegistro().registerUserInActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
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
    public void onDoActivityFail(User visitante) {
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
    
//    public void doActivity(User visitante) {
//        getRegistro().comprobarDetenerReanudar();
//        try {
//            imprimirColas();
//            visitante.sleep(getActivityTime());
//        } catch (InterruptedException e) {
//            if (visitante instanceof ChildUser) {
//                getZonaActividad().remove(visitante);
//                getZonaActividad().remove(visitante.getSupervisor());
//                semaforo.release(2);
//            } else {
//                getZonaActividad().remove(visitante);
//                semaforo.release();
//            }
//            visitante.setCurrentActivity("ParqueAcuatico");
//        }
//    }

    public void onTryGoOut(User visitante) {
    	getZonaActividad().remove(visitante);
    	semaforo.release();
    	getRegistro().unregisterUserFromActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
    }
    
//    public void goOut(AdultUser visitante) {
////        getRegistro().comprobarDetenerReanudar();
//        waitIfProgramIsStopped();
//        try {
//        	onTryGoOut(visitante);
////            getZonaActividad().remove(visitante);
////            semaforo.release();
////            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
//            
//            onGoOutSuccess(visitante);
////            visitante.setPermisoActividad(Permission.NONE);
////            imprimirColas();
////            visitante.setCurrentActivity("ParqueAcuatico");
//        } catch (Exception e) {
//        }
//    }

//    public void goOut(YoungUser visitante) {
////        getRegistro().comprobarDetenerReanudar();
//        waitIfProgramIsStopped();
//        try {
//        	onTryGoOut(visitante);
////            getZonaActividad().remove(visitante);
////            semaforo.release();
////            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());
//            
//            onGoOutSuccess(visitante);
////            visitante.setPermisoActividad(Permission.NONE);
////            imprimirColas();
////            visitante.setCurrentActivity("ParqueAcuatico");
//        } catch (Exception e) {
//        }
//    }

    public void goOut(ChildUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
            if (visitante.getPermisoActividad() == Permission.SUPERVISED) {
                desencolarNinio(visitante);
            } else {
                getZonaActividad().remove(visitante);
                semaforo.release();
                getRegistro().unregisterUserFromActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
                getZonaEsperaAcompanante().remove(visitante.getSupervisor());
                getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_AREA_SUPERVISORS, visitante.getSupervisor().getIdentificator());

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
