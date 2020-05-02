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
import application.user.ChildUser;

public class AquaticPark implements AquaticParkInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private String identificator = "ParqueAcuatico";
    private Semaphore semaphore = new Semaphore(ApplicationGlobalConfig.TOTAL_USERS_IN_PARK, true);
    private List<Activity> activities = new ArrayList<>();
    private BlockingQueue<User> waitingLine = new ArrayBlockingQueue<>(5000, true);
    private static final String WAITING_LINE = "-colaEspera";
    private static final String OUTSIDE = "Fuera";
    private UserRegistry userRegistry;

	public AquaticPark(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
        initActivities();
        registerActivity();
    }

    private void initActivities() {
    	MainPoolActivity mainPool = new MainPoolActivity(userRegistry);
    	this.activities.add(new LockerRoomActivity(userRegistry));
    	this.activities.add(mainPool);
    	this.activities.add(new SlideActivity(userRegistry, mainPool));
        this.activities.add(new DeckChairActivity(userRegistry));
        this.activities.add(new ChildPoolActivity(userRegistry));
        this.activities.add(new WavePoolActivity(userRegistry));
    }

    private List<String> getActivityAreas() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(WAITING_LINE);
        return areas;
    }

    private void registerActivity() {
        getRegistry().registerActivity(getIdentificator());
        getRegistry().registerActivityAreas(getIdentificator(), getActivityAreas());
    }
    
    private void randomActivities(int n, List<Activity> activitiesToDo) {
		while (n > 0) {
            int i = (int) (((getActivities().size()-1) * Math.random()) + 1);  // skip locker room;
            activitiesToDo.add(activities.get(i));
            n--;
        }
	}

    public List<Activity> selectActivities(int n) {
        List<Activity> activitiesToDo = new ArrayList<>();
        if (n < ApplicationGlobalConfig.USER_MIN_NUM_ACTIVITIES || n > ApplicationGlobalConfig.USER_MAX_NUM_ACTIVITIES) {
            n = getActivities().size();
        }
        activitiesToDo.add(getActivities().get(0)); // LockerRoom
        randomActivities(n, activitiesToDo);
        activitiesToDo.add(getActivities().get(0)); // LockerRoom
        return activitiesToDo;
    }

    private synchronized void goIntoWaitingLine(User user) {
        getWaitingLine().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
        user.setCurrentActivity(getIdentificator());
    }
    
    private synchronized void goIntoWaitingLine(ChildUser user) {
        getWaitingLine().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
        user.setCurrentActivity(getIdentificator());
        getWaitingLine().offer(user.getSupervisor());
        getRegistry().registerUserInActivity(getIdentificator(), WAITING_LINE, user.getSupervisor().getIdentificator());
    }
    
    private synchronized void goOutWaitingLine(User user) {
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(identificator, WAITING_LINE, user.getIdentificator());
    }

    private synchronized void goOutWaitingLine(ChildUser user) {
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getIdentificator());
        getWaitingLine().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), WAITING_LINE, user.getSupervisor().getIdentificator());
    }
    
    private boolean passFromWaitingLineToActivity(User user) throws InterruptedException {
    	boolean success = true;
    	getSemaphore().acquire();
        goOutWaitingLine(user);
    	return success;
    }
    
    private boolean passFromWaitingLineToActivity(ChildUser user) throws InterruptedException {
    	boolean success = true;
    	getSemaphore().acquire(2);
        goOutWaitingLine(user);
    	return success;
    }
    
    public boolean goIn(User user) {
        getRegistry().waitIfProgramIsStopped();
        boolean success = false;
        try {
            goIntoWaitingLine(user);
            success = passFromWaitingLineToActivity(user);
            
        } catch (Exception e) {
            goOutWaitingLine(user);
            user.setCurrentActivity(OUTSIDE);
        }
        return success;
    }

    public boolean goIn(ChildUser user) {
        getRegistry().waitIfProgramIsStopped();
        boolean success = false;
        try {
            goIntoWaitingLine(user);
            success = passFromWaitingLineToActivity(user);
            
        } catch (Exception e) {
            goOutWaitingLine(user);
            user.setCurrentActivity(OUTSIDE);
        }
        return success;
    }

    private void onTryGoOut(User user) {
    	getWaitingLine().remove(user);
    	getSemaphore().release();
    }
    
    private void onTryGoOut(ChildUser user) {
    	getWaitingLine().remove(user);
    	getSemaphore().release(2);
    }
    
    private void onGoOutSuccess(User user) {
    	user.setCurrentActivity(OUTSIDE);
        getRegistry().removeUser(user);
    }
    
    private void onGoOutSuccess(ChildUser user) {
    	user.setCurrentActivity(OUTSIDE);
        getRegistry().removeUser(user);
        getRegistry().removeUser(user.getSupervisor());
    }

    public void goOut(User user) {
        getRegistry().waitIfProgramIsStopped();
        onTryGoOut(user);
        onGoOutSuccess(user);
    }

    public void goOut(ChildUser user) {
        getRegistry().waitIfProgramIsStopped();
        onTryGoOut(user);
        onGoOutSuccess(user);
    }
    
    public String getIdentificator() {
    	return this.identificator;
    }
    
    public UserRegistry getRegistry() {
        return userRegistry;
    }
    
    public Semaphore getSemaphore() {
        return semaphore;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public BlockingQueue<User> getWaitingLine() {
        return waitingLine;
    }

}
