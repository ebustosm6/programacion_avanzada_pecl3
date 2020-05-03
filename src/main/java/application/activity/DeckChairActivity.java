package application.activity;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.lifeguard.BaseLifeGuard;
import application.lifeguard.DeckChairLifeGuard;

public class DeckChairActivity extends BaseActivity {

//    private static String IDENTIFICATOR = ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_NAME; //"ActividadTumbonas";
//    private static String LIFEGUARD_IDENTIFICATOR = ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_LIFEGUARD_IDENTIFICATOR;// "VigilanteTumbonas";
//    private static boolean IS_FAIR_QUEUE = false;
    
    public DeckChairActivity(UserRegistry userRegistry) {
        super(ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_NAME, ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_CAPACITY, 
        		ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_CAPACITY, ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_QUEUE_IS_FAIR, userRegistry);
    }

    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_MIN_MILISECONDS * Math.random()));
    }

    @Override
    protected BaseLifeGuard initActivityLifeguard() {
        BaseLifeGuard guard = new DeckChairLifeGuard(ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_LIFEGUARD_IDENTIFICATOR, getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), ApplicationGlobalConfig.ACTIVITY_AREA_LIFEGUARD, guard.getIdentificator());
        return guard;
    }
    
}
