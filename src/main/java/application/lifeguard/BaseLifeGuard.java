package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.user.User;

public class BaseLifeGuard extends Thread {

    private String identificator;
    private BlockingQueue<User> waitingLine;
    private UserRegistry userRegistry;
    private static final String EJECT_MESAGE = "ejects user from waiting line";

    public BaseLifeGuard(String id, ArrayBlockingQueue<User> waitingLine, UserRegistry userRegistry) {
        this.identificator = id;
        this.waitingLine = waitingLine;
        this.userRegistry = userRegistry;
    }

    protected Permission setPermissionToUser(User user) {
        Permission permType = Permission.NOT_ALLOWED;
        if (user.getAge() > 0) {
        	permType = Permission.ALLOWED;
        }
        return permType;
    }

    protected long getWatchingTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_DEFAULT_LIFEGUARD_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_DEFAULT_LIFEGUARD_MIN_MILISECONDS )
        		+ (ApplicationGlobalConfig.ACTIVITY_DEFAULT_LIFEGUARD_MIN_MILISECONDS * Math.random()));
    }

    public void run() {
        while (true) {
            try {
                for (User user : getWaitingLine()) {
                    getRegistry().waitIfProgramIsStopped();
                    sleep(getWatchingTime());
                    Permission perm = setPermissionToUser(user);
                    user.setActivityPermissionType(perm);
                    if (perm == Permission.NOT_ALLOWED) {
                    	System.out.println(getIdentificator() + " - " + EJECT_MESAGE + " - " + user.getIdentificator() + " - " + user.getAge());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public UserRegistry getRegistry() {
        return userRegistry;
    }

    public BlockingQueue<User> getWaitingLine() {
        return waitingLine;
    }

    public String getIdentificator() {
        return identificator;
    }

}
