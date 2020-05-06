package application.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.lifeguard.BaseLifeGuard;
import application.user.User;
import application.user.AdultUser;
import application.user.YoungUser;
import application.user.ChildUser;

public class BaseActivity implements ActivityInterface {

    private String identificator;
    private int capacityActivity = ApplicationGlobalConfig.ACTIVITY_DEFAULT_CAPACITY;
    private int capacityUseActivity = ApplicationGlobalConfig.ACTIVITY_DEFAULT_CAPACITY;
    private ArrayBlockingQueue<User> waitingLine;
    private ArrayBlockingQueue<User> activityArea;
    private ArrayBlockingQueue<User> waitingAreaSupervisor;
    private BaseLifeGuard lifeguard;
    private UserRegistry userRegistry;

    public BaseActivity(String identificator, int capacity, UserRegistry userRegistry) {
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

    public BaseActivity(String identificator, int capacityActivity, int capacityUseActivity, boolean colaFifo, UserRegistry userRegistry) {
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

    protected long getActivityTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MIN_MILISECONDS)
                + (ApplicationGlobalConfig.ACTIVITY_MIN_MILISECONDS * Math.random()));
    }

    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new BaseLifeGuard(ApplicationGlobalConfig.ACTIVITY_DEFAULT_LIFEGUARD_IDENTIFICATOR, getWaitingLine(), userRegistry);
        userRegistry.registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }

    protected void startActivityLifeguard() {
        getLifeguard().start();
    }

    protected void initActivityArea() {
        this.userRegistry.registerActivity(getIdentificator());
        this.userRegistry.registerActivityAreas(getIdentificator(), getActivitySubareas());

    }

    protected List<String> getActivitySubareas() {
        ArrayList<String> subAreas = new ArrayList<>();
        subAreas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE);
        subAreas.add(ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY);
        subAreas.add(ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS);
        return subAreas;
    }

    protected void printStatus() {
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE + " - " + getWaitingLine().toString());
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY + " - " + getActivityArea().toString());
        System.out.println(getIdentificator() + " - " + ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS + " - " + getWaitingAreaSupervisor().toString());
    }

    protected synchronized void goIntoWaitingLine(User user) {
        getWaitingLine().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
        user.setCurrentActivity(getIdentificator());
    }

    protected synchronized void goIntoWaitingLine(ChildUser user) {
        while (!getWaitingLine().offer(user)) {
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
        while (!getWaitingLine().offer(user.getSupervisor())) {
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getSupervisor().getIdentificator());
        user.setCurrentActivity(getIdentificator());
        user.getSupervisor().setCurrentActivity(getIdentificator());
    }

    protected synchronized void goOutWaitingLine(User user) {
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
    }

    protected synchronized void goOutWaitingLine(ChildUser user) {
        getWaitingLine().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getIdentificator());
        getWaitingLine().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_LINE, user.getSupervisor().getIdentificator());
    }

    protected synchronized void goIntoActivityArea(User user) {
        getActivityArea().offer(user);
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
    }

    protected synchronized void goIntoActivityArea(ChildUser user) {
        while (!getActivityArea().offer(user)) {
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        while (!getActivityArea().offer(user.getSupervisor())) {
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getSupervisor().getIdentificator());
    }

    protected synchronized void goIntoActivityAreaWithoutSupervisor(ChildUser user) {
        while (!getActivityArea().offer(user)) {
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        while (!getWaitingAreaSupervisor().offer(user.getSupervisor())) {
        }
        getRegistry().registerUserInActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
    }

    protected synchronized boolean passFromWaitingLineToActivity(User user) throws InterruptedException {
        boolean success = true;
        goOutWaitingLine(user);
        goIntoActivityArea(user);
        return success;
    }

    protected synchronized boolean passFromWaitingLineToActivity(ChildUser user) {
        boolean success = true;
        goOutWaitingLine(user);
        goIntoActivityArea(user);
        return success;
    }

    protected synchronized void goOutActivityArea(ChildUser user) {
        getActivityArea().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getActivityArea().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getSupervisor().getIdentificator());
    }

    protected synchronized void goOutActivityAreaWithoutSupervisor(ChildUser user) {
        getActivityArea().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
        getWaitingAreaSupervisor().remove(user.getSupervisor());
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_WAITING_AREA_SUPERVISORS, user.getSupervisor().getIdentificator());
    }

    protected void waitForLifeGuardPermission(User user) throws InterruptedException {
        while (user.getActivityPermissionType() == Permission.NONE) {
            user.sleep(500);
        }
    }

    protected void waitIfProgramIsStopped() {
        getRegistry().waitIfProgramIsStopped();
    }

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
                passFromWaitingLineToActivity(user);
            } else if (user.getActivityPermissionType() == Permission.ALLOWED) {
                goOutWaitingLine(user);
                goIntoActivityAreaWithoutSupervisor(user);
            }
            result = true;
        } catch (SecurityException e) {
            goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

    public boolean goIn(AdultUser user) throws InterruptedException {
        boolean result = false;
        waitIfProgramIsStopped();

        try {
            user.setActivityPermissionType(Permission.NONE);
            goIntoWaitingLine(user);
            printStatus();
            waitForLifeGuardPermission(user);

            if (user.getActivityPermissionType() == Permission.ALLOWED) {
                result = passFromWaitingLineToActivity(user);
            } else {
                throw new SecurityException();
            }

        } catch (SecurityException e) {
            goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

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
            result = passFromWaitingLineToActivity(user);

        } catch (SecurityException e) {
            goOutWaitingLine(user);
            onGoOutSuccess(user);
        }
        return result;
    }

    protected void onDoActivityFail(User user) {
        if (user instanceof ChildUser) {
            getActivityArea().remove(user);
            getActivityArea().remove(user.getSupervisor());
        } else {
            getActivityArea().remove(user);
        }
        user.setCurrentActivity(ApplicationGlobalConfig.PARK_IDENTIFICATOR);
    }

    public void doActivity(User user) {
        waitIfProgramIsStopped();
        try {
            printStatus();
            user.sleep(getActivityTime());
        } catch (InterruptedException e) {
            onDoActivityFail(user);
        }
    }

    protected void onTryGoOut(User user) {
        getActivityArea().remove(user);
        getRegistry().unregisterUserFromActivity(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY, user.getIdentificator());
    }

    protected void onTryGoOut(ChildUser user) {
        if (user.getActivityPermissionType() == Permission.SUPERVISED) {
            goOutActivityArea(user);
        } else {
            goOutActivityAreaWithoutSupervisor(user);
        }
    }

    protected void onGoOutSuccess(User user) {
        user.setActivityPermissionType(Permission.NONE);
        printStatus();
        user.setCurrentActivity(ApplicationGlobalConfig.PARK_IDENTIFICATOR);
    }

    public void goOut(User user) {
        waitIfProgramIsStopped();
        try {
            onTryGoOut(user);
            onGoOutSuccess(user);
        } catch (Exception e) {
        }
    }

    public void goOut(AdultUser user) {
        waitIfProgramIsStopped();
        try {
            onTryGoOut(user);
            onGoOutSuccess(user);
        } catch (Exception e) {
        }
    }

    public void goOut(YoungUser user) {
        waitIfProgramIsStopped();
        try {
            onTryGoOut(user);
            onGoOutSuccess(user);
        } catch (Exception e) {
        }
    }

    public void goOut(ChildUser user) {
        waitIfProgramIsStopped();
        try {
            onTryGoOut(user);
            onGoOutSuccess(user);
        } catch (Exception e) {
        }
    }

    public UserRegistry getRegistry() {
        return userRegistry;
    }

    public String toString() {
        return this.identificator;
    }

    public String getIdentificator() {
        return identificator;
    }

    public ArrayBlockingQueue<User> getActivityArea() {
        return this.activityArea;
    }

    public ArrayBlockingQueue<User> getWaitingLine() {
        return waitingLine;
    }

    public BaseLifeGuard getLifeguard() {
        return lifeguard;
    }

    public int getCapacityActivity() {
        return capacityActivity;
    }

    public int getCapacityUseActivity() {
        return capacityUseActivity;
    }

    public ArrayBlockingQueue<User> getWaitingAreaSupervisor() {
        return waitingAreaSupervisor;
    }

}
