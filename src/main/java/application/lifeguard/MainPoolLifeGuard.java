package application.lifeguard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.user.ChildUser;
import application.user.SupervisorUser;
import application.user.User;

public class MainPoolLifeGuard extends BaseLifeGuard {

    private BlockingQueue<User> activityArea;
    private static final String EJECT_MESAGE = "ejects user from activity area";

    public MainPoolLifeGuard(String id, ArrayBlockingQueue<User> waitingLine, ArrayBlockingQueue<User> activityArea, UserRegistry userRegistry) {
        super(id, waitingLine, userRegistry);
        this.activityArea = activityArea;
    }

    @Override
    protected long getWatchingTime() {
        return ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_LIFEGUARD_MILISECONDS;
    }

    protected long getEjectionTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_LIFEGUARD_EJECTION_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_LIFEGUARD_EJECTION_MIN_MILISECONDS)
                + (ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_LIFEGUARD_EJECTION_MIN_MILISECONDS * Math.random()));
    }

    @Override
    protected Permission setPermissionToUser(User user) {
        Permission permType = Permission.ALLOWED;
        if (user instanceof ChildUser) {
            permType = Permission.SUPERVISED;
        }
        return permType;
    }

    private User selectRandomUserInArea() {
        User user = null;
        int n = (int) (getActivityArea().size() * Math.random());
        Iterator<User> iterator = getActivityArea().iterator();
        List<User> users = new ArrayList<>();
        iterator.forEachRemaining(users::add);
        user = users.get(n);
        if (user instanceof SupervisorUser) {
            user = users.get(n - 1);
        }
        return user;
    }

    @Override
    public void run() {
        User randomUser;
        while (true) {
            try {
                for (User user : getWaitingLine()) {
                    getRegistry().waitIfProgramIsStopped();
                    sleep(getWatchingTime());
                    Permission perm = setPermissionToUser(user);
                    user.setActivityPermissionType(perm);
                    if (getActivityArea().remainingCapacity() == 0) {
                        randomUser = selectRandomUserInArea();
                        randomUser.interrupt();
                        sleep(getEjectionTime());
                        System.out.println(getIdentificator() + " - " + EJECT_MESAGE + " - " + user.getIdentificator() + " - " + user.getAge());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public BlockingQueue<User> getActivityArea() {
        return activityArea;
    }

}
