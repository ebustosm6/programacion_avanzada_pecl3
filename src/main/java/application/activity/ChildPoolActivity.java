package application.activity;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.ChildPoolLifeGuard;


public class ChildPoolActivity extends BaseActivity {

    public ChildPoolActivity(UserRegistry userRegistry) {
        super(ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_NAME, ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_CAPACITY, userRegistry);
    }
    
    @Override
    protected long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_MIN_MILISECONDS * Math.random()));
    }

    @Override
    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new ChildPoolLifeGuard(ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getRegistry());
    	getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }
    

}
