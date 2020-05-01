package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import application.activity.MainPoolActivity;
import application.activity.WavePoolActivity;
import application.config.ApplicationGlobalConfig;
import application.activity.SlideActivity;
import application.activity.DeckChairActivity;
import application.activity.Activity;
import application.activity.ChildPoolActivity;
import application.activity.LockerRoomActivity;
import application.user.User;
import application.user.AdultUser;
import application.user.YoungUser;
import application.user.ChildUser;

public class AquaticPark implements Serializable {

	private String identificator = "ParqueAcuatico";
//    private static int NUM_VISITANTES = ApplicationGlobalConfig.TOTAL_USERS_IN_PARK;
    private Semaphore semaphore = new Semaphore(ApplicationGlobalConfig.TOTAL_USERS_IN_PARK, true);
    private List<Activity> activities = new ArrayList<>();
    private BlockingQueue<User> waitingLine = new ArrayBlockingQueue<>(5000, true);
    private static final String WAITING_LINE = "-colaEspera";
    private static final String OUTSIDE = "Fuera";
    private UserRegistry userRegistry;
//    private MainPoolActivity piscinaGrande;

    public AquaticPark(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
        initActivities();
        registerActivity();
    }

    public void initActivities() {
    	MainPoolActivity mainPool = new MainPoolActivity(userRegistry);
    	this.activities.add(new LockerRoomActivity(userRegistry));
    	this.activities.add(mainPool);
    	this.activities.add(new SlideActivity(userRegistry, mainPool));
        this.activities.add(new DeckChairActivity(userRegistry));
        this.activities.add(new ChildPoolActivity(userRegistry));
        this.activities.add(new WavePoolActivity(userRegistry));
    }

    public UserRegistry getRegistry() {
        return userRegistry;
    }

    public List<String> getActivityAreas() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(WAITING_LINE);
        return areas;
    }

    public void registerActivity() {
        this.userRegistry.registerActivity(identificator);
        this.userRegistry.registerActivityAreas(identificator, getActivityAreas());
    }
    
    private void randomActivities(int n, List<Activity> activitiesToDo) {
		while (n > 0) {
            int i = (int) (((activities.size()-1) * Math.random()) + 1);  // skip locker room;
//            if (i == 0) { 
//                i = 1;
//            }
            activitiesToDo.add(activities.get(i));
            n--;
        }
	}

    public List<Activity> selectActivities(int n) {
        List<Activity> activitiesToDo = new ArrayList<>();
        if (n < ApplicationGlobalConfig.USER_MIN_NUM_ACTIVITIES || n > ApplicationGlobalConfig.USER_MAX_NUM_ACTIVITIES) {
            n = activities.size();
        }
        activitiesToDo.add(activities.get(0)); // LockerRoom
        randomActivities(n, activitiesToDo);
        activitiesToDo.add(activities.get(0)); // LockerRoom
        return activitiesToDo;
    }

    public synchronized void goIntoWaitingLine(ChildUser user) {
        getWaitingLine().offer(user);
        getRegistry().registerUserInActivity(identificator, WAITING_LINE, user.getIdentificator());
        user.setCurrentActivity(identificator);
        getWaitingLine().offer(user.getSupervisor());
        getRegistry().registerUserInActivity(identificator, WAITING_LINE, user.getSupervisor().getIdentificator());
    }
    
    public synchronized void goOutWaitingLine(User user) {
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, user.getIdentificator());
    }

    public synchronized void goOutWaitingLine(ChildUser user) {
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, user.getIdentificator());
        getWaitingLine().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, user.getSupervisor().getIdentificator());
    }
    
    public boolean passFromWaitingLineToActivity(User user) throws InterruptedException {
    	boolean success = true;
    	semaphore.acquire();
        goOutWaitingLine(user);
    	return success;
    }
    
    public boolean passFromWaitingLineToActivity(ChildUser user) throws InterruptedException {
    	boolean success = true;
    	semaphore.acquire(2);
        goOutWaitingLine(user);
    	return success;
    }

    public boolean goIn(ChildUser visitante) {
        getRegistry().waitIfProgramIsStopped();
        boolean resultado = false;
        try {
            goIntoWaitingLine(visitante);
//            visitante.setCurrentActivity(identificator);
            resultado = passFromWaitingLineToActivity(visitante);
//            semaphore.acquire(2);
//            goOutWaitingLine(visitante);
//            resultado = true;
            
        } catch (Exception e) {
            goOutWaitingLine(visitante);
            visitante.setCurrentActivity(OUTSIDE);
        }
        return resultado;
    }

    public boolean goIn(AdultUser visitante) {
        getRegistry().waitIfProgramIsStopped();
        boolean resultado = false;
        try {
        	goIntoWaitingLine(visitante);
//            getColaEspera().offer(visitante);
//            getRegistry().registerUserInActivity(identificator, WAITING_LINE, visitante.getIdentificator());
//            visitante.setCurrentActivity(identificator);
            resultado = passFromWaitingLineToActivity(visitante);
//            semaphore.acquire();
//            goOutWaitingLine(visitante);
////            getColaEspera().remove(visitante);
////            getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, visitante.getIdentificator());
//            resultado = true;
            
        } catch (Exception e) {
        	goOutWaitingLine(visitante);
//            getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, visitante.getIdentificator());
            visitante.setCurrentActivity(OUTSIDE);
        }
        return resultado;
    }

    public boolean goIn(YoungUser visitante) {
        getRegistry().waitIfProgramIsStopped();
        boolean resultado = false;
        try {
        	goIntoWaitingLine(visitante);
//            getColaEspera().offer(visitante);
//            getRegistry().registerUserInActivity(identificator, WAITING_LINE, visitante.getIdentificator());
//            visitante.setCurrentActivity(identificator);
            resultado = passFromWaitingLineToActivity(visitante);
//            semaphore.acquire();
//            goOutWaitingLine(visitante);
////            getColaEspera().remove(visitante);
////            getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, visitante.getIdentificator());
//            resultado = true;
            
        } catch (Exception e) {
        	goOutWaitingLine(visitante);
//            getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, visitante.getIdentificator());
            visitante.setCurrentActivity(OUTSIDE);
        }
        return resultado;
    }
    
    public void onTryGoOut(User user) {
    	getWaitingLine().remove(user);
        semaphore.release();
    }
    
    public void onTryGoOut(ChildUser user) {
    	getWaitingLine().remove(user);
        semaphore.release(2);
    }
    
    public void onGoOutSuccess(User user) {
    	user.setCurrentActivity(OUTSIDE);
        getRegistry().removeUser(user);
    }
    
    public void onGoOutSuccess(ChildUser user) {
    	user.setCurrentActivity(OUTSIDE);
        getRegistry().removeUser(user);
        getRegistry().removeUser(user.getSupervisor());
    }

    public void goOut(AdultUser visitante) {
        getRegistry().waitIfProgramIsStopped();
        onTryGoOut(visitante);
//        getColaEspera().remove(visitante);
//        semaphore.release();
        onGoOutSuccess(visitante);
//        visitante.setCurrentActivity("Fuera");
//        getRegistry().removeUser(visitante);
    }

    public void goOut(YoungUser visitante) {
        getRegistry().waitIfProgramIsStopped();
        onTryGoOut(visitante);
//        getColaEspera().remove(visitante);
//        semaphore.release();
        onGoOutSuccess(visitante);
//        visitante.setCurrentActivity("Fuera");
//        getRegistry().removeUser(visitante);
    }

    public void goOut(ChildUser visitante) {
        getRegistry().waitIfProgramIsStopped();
        onTryGoOut(visitante);
//        goOutWaitingLine(visitante);
//        semaphore.release(2);
        onGoOutSuccess(visitante);
//        visitante.setCurrentActivity("Fuera");
//        getRegistry().removeUser(visitante);
//        getRegistry().removeUser(visitante.getSupervisor());
    }
    
    public synchronized void goIntoWaitingLine(User user) {
        getWaitingLine().offer(user);
        getRegistry().registerUserInActivity(identificator, WAITING_LINE, user.getIdentificator());
        user.setCurrentActivity(identificator);
    }

//    public static int getNUM_VISITANTES() {
//        return NUM_VISITANTES;
//    }
//
//    public static void setNUM_VISITANTES(int NUM_VISITANTES) {
//        AquaticPark.NUM_VISITANTES = NUM_VISITANTES;
//    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

//    public void setSemaforo(Semaphore semaforo) {
//        this.semaphore = semaforo;
//    }

//    public List<Activity> getActividades() {
//        return activities;
//    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public BlockingQueue<User> getWaitingLine() {
        return waitingLine;
    }

//    public void setColaEspera(BlockingQueue<User> colaEspera) {
//        this.waitingLine = colaEspera;
//    }

}
