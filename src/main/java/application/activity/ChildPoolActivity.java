/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.activity;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.ChildPoolLifeGuard;


public class ChildPoolActivity extends BaseActivity {

//    private static String IDENTIFICATOR = ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_NAME; //"ActividadPiscinaNinos";
//    private static String LIFEGUARD_IDENTIFICATOR = ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_LIFEGUARD_IDENTIFICATOR; // "VigilantePiscinaNinos";
    

    public ChildPoolActivity(UserRegistry userRegistry) {
        super(ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_NAME, ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_CAPACITY, userRegistry);
    }
    
    @Override
    public long getActivityTime() {
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
