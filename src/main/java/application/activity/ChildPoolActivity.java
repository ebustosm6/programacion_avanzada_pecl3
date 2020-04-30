/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.activity;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.lifeguard.LifeGuard;
import application.lifeguard.ChildPoolLifeGuard;


public class ChildPoolActivity extends Activity {

//    private static int CAPACITY = ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_CAPACITY;
    private static String IDENTIFICATOR = "ActividadPiscinaNinos";
    

    public ChildPoolActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_CAPACITY, userRegistry);
    }
    
    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_MIN_MILISECONDS * Math.random()));
    }

    @Override
    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new ChildPoolLifeGuard("VigilantePiscinaNinos", getColaEspera(), getRegistro());
    	getRegistro().aniadirMonitorEnZona(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }

    

}
