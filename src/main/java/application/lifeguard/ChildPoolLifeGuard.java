package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.user.User;


public class ChildPoolLifeGuard extends BaseLifeGuard {
    
    public ChildPoolLifeGuard(String id, ArrayBlockingQueue<User> waitingLine, UserRegistry userRegistry) {
		super(id, waitingLine, userRegistry);
	}
    
    @Override
    protected long getWatchingTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_LIFEGUARD_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_LIFEGUARD_MIN_MILISECONDS) 
        		+ (ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_LIFEGUARD_MIN_MILISECONDS * Math.random()));
    }
    
    @Override
    protected Permission setPermissionToUser(User user) {
    	Permission permType = Permission.NOT_ALLOWED;
    	if (user.getAge() >= 1 && user.getAge() <= 5) {
    		permType = Permission.SUPERVISED;
    	} else if (user.getAge() >= 6 && user.getAge() <= 10) {
    		permType = Permission.ALLOWED;
    	}
        return permType;
    }
    
}
