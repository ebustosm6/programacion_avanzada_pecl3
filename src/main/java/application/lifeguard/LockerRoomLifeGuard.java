package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.user.User;

public class LockerRoomLifeGuard extends BaseLifeGuard {

    public LockerRoomLifeGuard(String id, ArrayBlockingQueue<User> waitingLine, UserRegistry userRegistry) {
        super(id, waitingLine, userRegistry);
    }
    
    @Override
    protected long getWatchingTime() {
        return ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_LIFEGUARD_MILISECONDS;
    }

    @Override
    protected Permission setPermissionToUser(User user) {
        Permission permType = Permission.ALLOWED;
        if (user.getAge() <= 10) {
        	permType = Permission.SUPERVISED;
        }
        return permType;
    }

}
