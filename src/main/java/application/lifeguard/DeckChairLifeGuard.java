package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.user.User;

public class DeckChairLifeGuard extends BaseLifeGuard {
	
	public DeckChairLifeGuard(String id, ArrayBlockingQueue<User> waitingLine, UserRegistry userRegistry) {
		super(id, waitingLine, userRegistry);
	}
	
	public long getWatchingTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_LIFEGUARD_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_LIFEGUARD_MIN_MILISECONDS) 
        		+ (ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_LIFEGUARD_MIN_MILISECONDS * Math.random()));
    }
	
	public Permission setPermissionToUser(User user) {
    	Permission permType = Permission.NOT_ALLOWED;
    	if (user.getAge() >= 15) {
    		permType = Permission.ALLOWED;
    	}
        return permType;
    }

}
