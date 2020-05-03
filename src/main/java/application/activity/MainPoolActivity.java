package application.activity;

import java.util.concurrent.Semaphore;
import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.MainPoolLifeGuard;
import application.user.User;
import application.user.AdultUser;
import application.user.YoungUser;
import application.user.ChildUser;

public class MainPoolActivity extends BaseActivity {

    private Semaphore semaphore;

    public MainPoolActivity(UserRegistry userRegistry) {
        super(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_NAME, ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, userRegistry);
        this.semaphore = new Semaphore(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_CAPACITY, true);
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_MIN_MILISECONDS * Math.random()));
    }

    @Override
    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new MainPoolLifeGuard(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getActivityArea(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }
    
    @Override
    protected synchronized void goOutActivityArea(ChildUser user) {
        getActivityArea().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getActivityArea().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getSupervisor().getIdentificator());
        getSemaphore().release(2);
    }
    
    @Override
    protected synchronized void goOutActivityAreaWithoutSupervisor(ChildUser user) {
        getActivityArea().remove(user);
        getSemaphore().release();
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getWaitingAreaSupervisor().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
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

            if (user.getActivityPermissionType() == Permission.NOT_ALLOWED) {
                throw new SecurityException();
            } else if (user.getActivityPermissionType() == Permission.SUPERVISED) {
            	getSemaphore().acquire(2);
            	passFromWaitingLineToActivity(user);
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

            if (user.getActivityPermissionType() == Permission.ALLOWED) {
            	getSemaphore().acquire();
                goOutWaitingLine(user);
                goIntoActivityArea(user);
                result = true;
            } else {
                throw new SecurityException();
            }

        } catch (SecurityException e) {
        	goOutWaitingLine(user);
            onGoOutSuccess(user);

        }
        return result;
    }

    @Override
    public boolean goIn(YoungUser user) throws InterruptedException {
    	boolean result = false;
    	waitIfProgramIsStopped();
        
        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() != Permission.ALLOWED) {
                throw new SecurityException();
            }
            getSemaphore().acquire();
            goOutWaitingLine(user);
            goIntoActivityArea(user);
            result = true;

        } catch (SecurityException e) {
        	goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

    @Override
    protected void onDoActivityFail(User user) {
    	if (user instanceof ChildUser) {
            getActivityArea().remove(user);
            getActivityArea().remove(user.getSupervisor());
            getSemaphore().release(2);
        } else {
            getActivityArea().remove(user);
            getSemaphore().release();
        }
        user.setCurrentActivity(ApplicationGlobalConfig.PARK_IDENTIFICATOR);
    }
    
    @Override
    protected void onTryGoOut(User user) {
    	getActivityArea().remove(user);
    	getSemaphore().release();
    	getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
    }
    
    public Semaphore getSemaphore() {
        return semaphore;
    }

}
