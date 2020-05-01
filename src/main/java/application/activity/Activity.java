package application.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.LifeGuard;
import application.user.User;
import application.user.AdultUser;
import application.user.YoungUser;
import application.user.ChildUser;

public class Activity implements ActivityInterface, Serializable {

    private String identificator;
    private int capacityActivity = ApplicationGlobalConfig.ACTIVITY_DEFAULT_CAPACITY;
    private int capacityUseActivity = ApplicationGlobalConfig.ACTIVITY_DEFAULT_CAPACITY;
    private ArrayBlockingQueue<User> waitingLine;
    private static final String WAITING_LINE = "-colaEspera"; // AreaActivity.WAITING_LINE
    private ArrayBlockingQueue<User> activityArea;
    private static final String ACTIVITY = "-zonaActividad"; // AreaActivity.ACTIVITY
    private ArrayBlockingQueue<User> waitingAreaSupervisor;
    private static final String WAITING_AREA_SUPERVISORS = "-zonaEsperaAcompaniante"; // AreaActivity.WAITING_AREA_SUPERVISORS
    private LifeGuard lifeguard;
    private UserRegistry userRegistry;
    
    public Activity(String identificator, int capacity, UserRegistry userRegistry) {
        this.identificator = identificator;
        this.capacityActivity = capacity;
        this.capacityUseActivity = capacity;
        this.userRegistry = userRegistry;
        this.waitingLine = new ArrayBlockingQueue<>(ApplicationGlobalConfig.TOTAL_CREATED_USERS, true);
        this.activityArea = new ArrayBlockingQueue<>(capacity, true);
        this.waitingAreaSupervisor = new ArrayBlockingQueue<>(capacity, true);
        this.lifeguard = initActivityLifeguard();
        initActivityArea();
        startActivityLifeguard();
    }

    public Activity(String identificator, int capacityActivity, int capacityUseActivity, boolean colaFifo, UserRegistry userRegistry) {
        this.identificator = identificator;
        this.capacityActivity = capacityActivity;
        this.capacityUseActivity = capacityUseActivity;
        this.userRegistry = userRegistry;
        this.waitingLine = new ArrayBlockingQueue<>(ApplicationGlobalConfig.TOTAL_CREATED_USERS, colaFifo);
        this.activityArea = new ArrayBlockingQueue<>(capacityUseActivity, true);
        this.waitingAreaSupervisor = new ArrayBlockingQueue<>(capacityActivity, true);
        this.lifeguard = initActivityLifeguard();
        initActivityArea();
        startActivityLifeguard();
    }
    
    public long getActivityTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_MIN_MILISECONDS * Math.random()));
    }
    
    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new LifeGuard(ApplicationGlobalConfig.ACTIVITY_DEFAULT_LIFEGUARD_IDENTIFICATOR, getColaEspera(), userRegistry);
        userRegistry.registerLifeguard(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }
    
    public void startActivityLifeguard() {
    	getVigilante().start();
    }
    
    public void initActivityArea() {
        this.userRegistry.registerActivity(getIdentificator());
        this.userRegistry.registerActivityAreas(getIdentificator(), getActivitySubareas());

    }
    
    public List<String> getActivitySubareas() {
        ArrayList<String> subAreas = new ArrayList<>();
        subAreas.add(WAITING_LINE);
        subAreas.add(ACTIVITY);
        subAreas.add(WAITING_AREA_SUPERVISORS);
        return subAreas;
    }

    public void imprimirColas() {
        System.out.println("******************************");
        System.out.println(getIdentificator() + " - cola de espera: " + getColaEspera().toString());
        System.out.println(getIdentificator() + " - zona de actividad: " + getZonaActividad().toString());
        System.out.println(getIdentificator() + " - zona de espera de actividad: " + getZonaEsperaAcompanante().toString());
        System.out.println("******************************");
    }

    public synchronized void encolarNinio(ChildUser visitante) {
        while (!getColaEspera().offer(visitante)) {
            //espera
        }
        getRegistro().registerUserInActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
        while (!getColaEspera().offer(visitante.getSupervisor())) {
            //espera
        }

        getRegistro().registerUserInActivity(getIdentificator(), WAITING_LINE, visitante.getSupervisor().getIdentificator());
        visitante.setCurrentActivity(getIdentificator());
        visitante.getSupervisor().setCurrentActivity(getIdentificator());
    }

    public synchronized void desencolarNinioColaEspera(ChildUser visitante) {
        getColaEspera().remove(visitante);
        getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getIdentificator());
        getColaEspera().remove(visitante.getSupervisor());
        getRegistro().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, visitante.getSupervisor().getIdentificator());
    }

    public synchronized void encolarNinioActividad(ChildUser visitante) {
        getColaEspera().remove(visitante);
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
        getRegistro().unregisterUserFromActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
        getZonaActividad().remove(visitante.getSupervisor());
        getRegistro().unregisterUserFromActivity(getIdentificator(), ACTIVITY, visitante.getSupervisor().getIdentificator());
    }
    
    public void waitForLifeGuardPermission(User user) throws InterruptedException {
    	while (user.getPermisoActividad() == Permission.NONE) {
    		user.sleep(500);
        }
    }
    
    public void waitIfProgramIsStopped() {
    	getRegistro().waitIfProgramIsStopped();
    }

    public boolean goIn(ChildUser visitante) throws InterruptedException {
    	boolean resultado = false;
//        getRegistro().comprobarDetenerReanudar();
    	waitIfProgramIsStopped();
        
        int espaciosOcupados = 2;
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
                espaciosOcupados = 1;
                desencolarNinioColaEspera(visitante);
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

    public void onDoActivityFail(User visitante) {
    	if (visitante instanceof ChildUser) {
            getZonaActividad().remove(visitante);
            getZonaActividad().remove(visitante.getSupervisor());
        } else {
            getZonaActividad().remove(visitante);
        }
        visitante.setCurrentActivity("ParqueAcuatico");
    }
    
    public void doActivity(User visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
            imprimirColas();
            visitante.sleep(getActivityTime());
        } catch (InterruptedException e) {
        	onDoActivityFail(visitante);
//            if (visitante instanceof ChildUser) {
//                getZonaActividad().remove(visitante);
//                getZonaActividad().remove(visitante.getSupervisor());
//            } else {
//                getZonaActividad().remove(visitante);
//            }
//            visitante.setCurrentActivity("ParqueAcuatico");
        }
    }
    
    public void onTryGoOut(User visitante) {
    	getZonaActividad().remove(visitante);
        getRegistro().unregisterUserFromActivity(getIdentificator(), ACTIVITY, visitante.getIdentificator());
    }
    
    public void onGoOutSuccess(User visitante) {
    	visitante.setPermisoActividad(Permission.NONE);
        imprimirColas();
        visitante.setCurrentActivity("ParqueAcuatico");
    }
    
    public void goOut(User visitante) {
//      getRegistro().comprobarDetenerReanudar();
      waitIfProgramIsStopped();
      try {
      	onTryGoOut(visitante);
//          getZonaActividad().remove(visitante);
//          getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());

          onGoOutSuccess(visitante);
//          visitante.setPermisoActividad(Permission.NONE);
//          imprimirColas();
//          visitante.setCurrentActivity("ParqueAcuatico");
      } catch (Exception e) {
      }
  }
    
    public void goOut(AdultUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        try {
        	onTryGoOut(visitante);
//            getZonaActividad().remove(visitante);
//            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());

            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goOut(YoungUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
        	onTryGoOut(visitante);
//            getZonaActividad().remove(visitante);
//            getRegistro().eliminarVisitanteZonaActividad(getIdentificator(), ACTIVITY, visitante.getIdentificator());

            onGoOutSuccess(visitante);
//            visitante.setPermisoActividad(Permission.NONE);
//            imprimirColas();
//            visitante.setCurrentActivity("ParqueAcuatico");
        } catch (Exception e) {
        }
    }

    public void goOut(ChildUser visitante) {
//        getRegistro().comprobarDetenerReanudar();
        waitIfProgramIsStopped();
        
        try {
            if (visitante.getPermisoActividad() == Permission.SUPERVISED) {
                desencolarNinio(visitante);
            } else {
                getZonaActividad().remove(visitante);
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

    public UserRegistry getRegistro() {
        return userRegistry;
    }

    public void setRegistro(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public String toString() {
        return this.identificator;
    }

    protected void imprimirColaEspera() {
        System.out.println("La actividad: " + identificator + " y la cola de espera es: " + waitingLine.toString());
    }

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificador(String identificator) {
        this.identificator = identificator;
    }

    public ArrayBlockingQueue<User> getZonaActividad() {
        return this.activityArea;
    }

    public void setZonaActividad(ArrayBlockingQueue<User> zonaActividad) {
        this.activityArea = zonaActividad;
    }

    public ArrayBlockingQueue<User> getColaEspera() {
        return waitingLine;
    }

    public void setColaEspera(ArrayBlockingQueue<User> colaEspera) {
        this.waitingLine = colaEspera;
    }

    public LifeGuard getVigilante() {
        return lifeguard;
    }

    public void setVigilante(LifeGuard vigilante) {
        this.lifeguard = vigilante;
    }

    public int getCapacidadTotal() {
        return capacityActivity;
    }

    public void setCapacidadTotal(int capacidadTotal) {
        this.capacityActivity = capacidadTotal;
    }

    public int getCapacidadInterior() {
        return capacityUseActivity;
    }

    public void setCapacidadInterior(int capacidadInterior) {
        this.capacityUseActivity = capacidadInterior;
    }

    public ArrayBlockingQueue<User> getZonaEsperaAcompanante() {
        return waitingAreaSupervisor;
    }

    public void setZonaEsperaAcompanante(ArrayBlockingQueue<User> zonaEsperaAcompanante) {
        this.waitingAreaSupervisor = zonaEsperaAcompanante;
    }
}
