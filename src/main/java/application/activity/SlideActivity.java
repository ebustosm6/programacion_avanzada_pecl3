package application.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.enums.SlideTicket;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.SlideLifeGuard;
import application.user.AdultUser;
import application.user.ChildUser;
import application.user.User;
import application.user.YoungUser;

public class SlideActivity extends BaseActivity {

    private SlideTicket ticket;
    private ArrayBlockingQueue<User> slideB = new ArrayBlockingQueue<User>(ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, true);
    private ArrayBlockingQueue<User> slideC = new ArrayBlockingQueue<User>(ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, true);
    private MainPoolActivity mainPool;
    private BaseLifeGuard lifeguardB;
    private BaseLifeGuard lifeguardC;

    public SlideActivity(UserRegistry userRegistry, MainPoolActivity piscinaGrande) {
        super(ApplicationGlobalConfig.ACTIVITY_SLIDE_NAME, ApplicationGlobalConfig.ACTIVITY_SLIDE_CAPACITY, userRegistry);
        this.mainPool = piscinaGrande;
        this.lifeguardB = initLifeGuard(ApplicationGlobalConfig.ACTIVITY_SLIDE_LIFEGUARD_B_IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_SLIDE_B_NAME);
        this.lifeguardC = initLifeGuard(ApplicationGlobalConfig.ACTIVITY_SLIDE_LIFEGUARD_C_IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_SLIDE_C_NAME);
        initOtherLifeguards();
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_SLIDE_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_SLIDE_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_SLIDE_MIN_MILISECONDS * Math.random()));
    }
    
    @Override
    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new SlideLifeGuard(ApplicationGlobalConfig.ACTIVITY_SLIDE_LIFEGUARD_A_IDENTIFICATOR, getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    @Override
    protected List<String> getActivitySubareas() {
        ArrayList<String> areas = new ArrayList<>();
        areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE);
        areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY);
        areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_B);
        areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_C);
        areas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS);
        return areas;
    }

    protected BaseLifeGuard initLifeGuard(String identificator, String identificatorActividad) {
        BaseLifeGuard guard = new SlideLifeGuard(identificator, getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(identificatorActividad, ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    private void initOtherLifeguards() {
        lifeguardB.start();
        lifeguardC.start();
    }
    
    @Override
    protected synchronized void goIntoWaitingLine(ChildUser user) {
    	getWaitingLine().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
        user.setCurrentActivity(getIdentificator());
        user.getSupervisor().setCurrentActivity(getIdentificator());
        getMainPool().getWaitingAreaSupervisor().offer(user.getSupervisor());
        getRegistry().registerUserInActivity(getMainPool().getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
    }
    
    @Override
    protected synchronized void goOutWaitingLine(ChildUser user) {
    	getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
        getMainPool().getWaitingAreaSupervisor().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getMainPool().getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
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

            if (user.getActivityPermissionType() == Permission.ALLOWED) {
                goIntoSlide(user);
            } else {
                throw new SecurityException();
            }
            printStatus();
            result = true;
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
                goIntoSlide(user);
            }
            printStatus();
            result = true;
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

            if (user.getActivityPermissionType() == Permission.ALLOWED) {
                goIntoSlide(user);
            }
            printStatus();
            result = true;
        } catch (SecurityException e) {
        	goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }
    
    @Override
    public void onGoOutSuccess(User user) {
        user.setCurrentActivity(getMainPool().getIdentificator());
        getMainPool().getActivityArea().offer(user);
        getRegistry().registerUserInActivity(getMainPool().getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getMainPool().doActivity(user);
        getMainPool().goOut(user);
        user.setCurrentActivity(ApplicationGlobalConfig.PARK_IDENTIFICATOR);
    }

    @Override
    public void goOut(ChildUser user) {
        waitIfProgramIsStopped();
        
        try {
            if (user.getSlideTicket() == SlideTicket.SLIDE_A) {
                getActivityArea().remove(user); //Tobogan A
                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
            } else {
                getSlideB().remove(user);
                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_B, user.getIdentificator());
            }
            printStatus();
            
            onGoOutSuccess(user);
        } catch (Exception e) {
        }
    }

    @Override
    public void goOut(AdultUser user) {
        waitIfProgramIsStopped();
        try {
            getSlideC().remove(user);
            getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_C, user.getIdentificator());
            printStatus();
            
            onGoOutSuccess(user);
        } catch (Exception e) {
        }
    }

    @Override
    public void goOut(YoungUser user) {
        waitIfProgramIsStopped();
        try {
            if (user.getSlideTicket() == SlideTicket.SLIDE_A) {
                getActivityArea().remove(user); //Slide A
                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
            } else {
                getSlideB().remove(user);
                getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_B, user.getIdentificator());
            }
            printStatus();
            
            onGoOutSuccess(user);
        } catch (Exception e) {
        }
    }

    public void goIntoSlide(User user) throws InterruptedException{
        getMainPool().getSemaphore().acquire();
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
        if (user.getSlideTicket() == SlideTicket.SLIDE_A) {
            while (!getActivityArea().offer(user)) {
            }
            user.setCurrentActivity(ApplicationGlobalConfig.ACTIVITY_SLIDE_A_NAME);
            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());

        } else if (user.getSlideTicket() == SlideTicket.SLIDE_B) {
            while (!getSlideB().offer(user)) {
            }
            user.setCurrentActivity(ApplicationGlobalConfig.ACTIVITY_SLIDE_B_NAME);
            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_B, user.getIdentificator());
        } else {
            while (!getSlideC().offer(user)) {
            }
            user.setCurrentActivity(ApplicationGlobalConfig.ACTIVITY_SLIDE_C_NAME);
            getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_C, user.getIdentificator());
        }
    }

    @Override
    public void printStatus() {
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE + " - " + getWaitingLine().toString());
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_SLIDE_NAME + " - " + getActivityArea().toString());
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_B + " - " + getSlideB().toString());
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_C + " - " + getSlideC().toString());
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS + " - " + getMainPool().getWaitingAreaSupervisor().toString());
    }

    public SlideTicket getTicket() {
        return ticket;
    }

    public ArrayBlockingQueue<User> getSlideC() {
        return slideC;
    }

    public ArrayBlockingQueue<User> getSlideB() {
        return slideB;
    }

    public MainPoolActivity getMainPool() {
        return mainPool;
    }


}
