package application.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.LockerRoomLifeGuard;
import application.user.AdultUser;
import application.user.ChildUser;
import application.user.User;

public class LockerRoomActivity extends BaseActivity {

    private ArrayBlockingQueue<User> activityAreaAdultUsers;

    public LockerRoomActivity(UserRegistry userRegistry) {
        super(ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_NAME, ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CAPACITY,
        		ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_CHILD_CAPACITY, ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_QUEUE_IS_FAIR, userRegistry);
        this.activityAreaAdultUsers = new ArrayBlockingQueue<>(ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_ADULT_CAPACITY, true);
    }
    
    @Override
    protected long getActivityTime() {
        return ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_MILISECONDS;
    }
    
    @Override
    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new LockerRoomLifeGuard(ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getRegistry());
    	getRegistry().registerLifeguard(getIdentificator(),  ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    @Override
    protected List<String> getActivitySubareas() {
    	ArrayList<String> areas = new ArrayList<>();
    	areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE);
    	areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY);
    	areas.add(ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_AREA_ACTIVITY_ADULT_USERS);
    	return areas;
    }
    
    @Override
    public void printStatus() {
    	System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE + " - " + getWaitingLine().toString());
    	System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY + " - " + getActivityArea().toString());
    	System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_AREA_ACTIVITY_ADULT_USERS + " - " + getActivityAreaAdultUsers().toString());
    }
    
    @Override
    protected synchronized void goIntoActivityAreaWithoutSupervisor(ChildUser user) {
    	getActivityArea().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
    	getActivityAreaAdultUsers().offer(user.getSupervisor());
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_AREA_ACTIVITY_ADULT_USERS, user.getSupervisor().getIdentificator());
    }
    
    protected synchronized void goIntoActivityArea(AdultUser user) {
    	getActivityAreaAdultUsers().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_AREA_ACTIVITY_ADULT_USERS, user.getIdentificator());
    }

    @Override
    public boolean goIn(ChildUser user) throws InterruptedException {
    	boolean result = false;
        waitIfProgramIsStopped();
        
        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();
            waitForLifeGuardPermission(user);
            
            if (user.getActivityPermissionType() == Permission.SUPERVISED) {
                result = passFromWaitingLineToActivity(user);
            } else if (user.getActivityPermissionType() == Permission.ALLOWED) {
            	goOutWaitingLine(user);
            	goIntoActivityAreaWithoutSupervisor(user);
            	result = true;
            }

            
        } catch (SecurityException e) {
            goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

    @Override
    public boolean goIn(AdultUser user) throws InterruptedException {
    	boolean result = false;
        waitIfProgramIsStopped();
        
        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();
            waitForLifeGuardPermission(user);
            goOutWaitingLine(user);
            goIntoActivityArea(user);
            result = true;

        } catch (SecurityException e) {
        	goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }
    
    protected void onTryGoOut(AdultUser user) {
    	getActivityAreaAdultUsers().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_AREA_ACTIVITY_ADULT_USERS, user.getIdentificator());
    }
    
    @Override
    public void goOut(AdultUser user) {
        waitIfProgramIsStopped();
        onTryGoOut(user);
        onGoOutSuccess(user);
    }
    
    public ArrayBlockingQueue<User> getActivityAreaAdultUsers() {
        return activityAreaAdultUsers;
    }
    

}
