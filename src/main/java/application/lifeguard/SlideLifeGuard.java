package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.enums.SlideTicket;
import application.user.User;

public class SlideLifeGuard extends BaseLifeGuard {

    public SlideLifeGuard(String id, ArrayBlockingQueue<User> waitingLine, UserRegistry userRegistry) {
        super(id, waitingLine, userRegistry);
    }

    public long getWatchingTime() {
        return (long) ((ApplicationGlobalConfig.ACTIVITY_SLIDE_LIFEGUARD_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_SLIDE_LIFEGUARD_MIN_MILISECONDS) 
        		+ (ApplicationGlobalConfig.ACTIVITY_SLIDE_LIFEGUARD_MIN_MILISECONDS * Math.random()));
    }

    public Permission setPermissionToUser(User user) {
        Permission permType = Permission.NOT_ALLOWED;
        if (user.getAge() >= 11 && user.getAge() <= 14) {
            permType = Permission.ALLOWED;
            user.setSlideTicket(SlideTicket.SLIDE_A);
        } else if (user.getAge() >= 15 && user.getAge() <= 17) {
            permType = Permission.ALLOWED;
            user.setSlideTicket(SlideTicket.SLIDE_B);
        }else if(user.getAge() > 17){
            permType = Permission.ALLOWED;
            user.setSlideTicket(SlideTicket.SLIDE_C);
        }
        return permType;
    }

}
