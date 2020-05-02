package application.activity;

import application.UserRegistry;
import application.config.ApplicationGlobalConfig;
import application.lifeguard.LifeGuard;
import application.lifeguard.DeckChairLifeGuard;

public class DeckChairActivity extends Activity {

//    private static int CAPACITY = ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_CAPACITY;
    private static String IDENTIFICATOR = "ActividadTumbonas";
    private static boolean IS_FAIR_QUEUE = false;
//    private static final String WAITING_LINE = "-colaEspera";
//    private static final String ACTIVITY = "-zonaActividad";
//    private static final String WAITING_AREA_SUPERVISORS = "-zonaEsperaAcompaniante";
    
    public DeckChairActivity(UserRegistry userRegistry) {
        super(IDENTIFICATOR, ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_CAPACITY, ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_CAPACITY, IS_FAIR_QUEUE, userRegistry);
    }

    @Override
    public long getActivityTime() {
    	return (long) ((ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_MAX_MILISECONDS - ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_MIN_MILISECONDS) + 
        		(ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_MIN_MILISECONDS * Math.random()));
    }

    @Override
    public LifeGuard initActivityLifeguard() {
        LifeGuard guard = new DeckChairLifeGuard("VigilanteTumbonas", getWaitingLine(), getRegistry());
        getRegistry().registerLifeguard(getIdentificator(), "-monitor", guard.getIdentificator());
        return guard;
    }
    
}
