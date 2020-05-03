package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.user.User;

public class WavePoolLifeGuard extends BaseLifeGuard {
	
	public WavePoolLifeGuard(String id, ArrayBlockingQueue<User> waitingLine, UserRegistry userRegistry) {
		super(id, waitingLine, userRegistry);
	}
	
	@Override
	protected long getWatchingTime() {
        return ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_LIFEGUARD_MILISECONDS;
    }
	
	@Override
	protected Permission setPermissionToUser(User user) {
    	Permission permType = Permission.NOT_ALLOWED;
    	if (user.getAge() >= 6 && user.getAge() <= 10) {
    		permType = Permission.SUPERVISED;
    	} else if (user.getAge() > 10) {
    		permType = Permission.ALLOWED;
    	}
        return permType;
    }

}
