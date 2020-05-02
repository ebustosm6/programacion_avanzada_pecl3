package application.config;

public class ApplicationGlobalConfig {
	// park
	public static final String PARK_IDENTIFICATOR = "ParqueAcuatico";
	public static final String PARK_OUTSIDE = "Fuera";
	// users
	public static final int TOTAL_CREATED_USERS = 5000;
	public static final int TOTAL_USERS_IN_PARK = 100;
	public static final int USER_MIN_NUM_ACTIVITIES = 5;
	public static final int USER_MAX_NUM_ACTIVITIES = 15;
	public static final int USER_ADULT_AGE = 18;
	public static final int USER_WITH_SUPERVISOR_LIMIT_AGE = 11;
	public static final int USER_SUPERVISOR_LIMIT_AGE = 50;
	public static final int USER_GENERATION_MIN_MILISECONDS = 400;
	public static final int USER_GENERATION_MAX_MILISECONDS = 700;
	// activities
	public static final String ACTIVITY_DEFAULT_LIFEGUARD_IDENTIFICATOR = "DefaultLifeguard";
	public static final String ACTIVITY_AREA_WAITING_LINE = "-colaEspera";
	public static final String ACTIVITY_AREA_ACTIVITY = "-zonaActividad";
	public static final String ACTIVITY_AREA_WAITING_AREA_SUPERVISORS = "-zonaEsperaAcompaniante";
	public static final String ACTIVITY_AREA_LIFEGUARD = "-monitor";
	public static final int ACTIVITY_DEFAULT_CAPACITY = 20;
	public static final int ACTIVITY_MIN_MILISECONDS = 100;
	public static final int ACTIVITY_MAX_MILISECONDS = 1000;
	
	public static final String ACTIVITY_CHILD_POOL_NAME = "ActividadPiscinaNinos";
	public static final String ACTIVITY_CHILD_POOL_LIFEGUARD_IDENTIFICATOR = "VigilantePiscinaNinos";
	public static final int ACTIVITY_CHILD_POOL_CAPACITY = 15;
	public static final int ACTIVITY_CHILD_POOL_MIN_MILISECONDS = 1000;
	public static final int ACTIVITY_CHILD_POOL_MAX_MILISECONDS = 3000;
	
	public static final String ACTIVITY_DECK_CHAIR_NAME = "ActividadTumbonas";
	public static final String ACTIVITY_DECK_CHAIR_LIFEGUARD_IDENTIFICATOR = "VigilanteTumbonas";
	public static final int ACTIVITY_DECK_CHAIR_CAPACITY = 20;
	public static final int ACTIVITY_DECK_CHAIR_MIN_MILISECONDS = 2000;
	public static final int ACTIVITY_DECK_CHAIR_MAX_MILISECONDS = 4000;
	public static final boolean ACTIVITY_DECK_CHAIR_QUEUE_IS_FAIR = false;
	
	public static final String ACTIVITY_LOCKER_ROOM_NAME = "ActividadVestuario";
	public static final String ACTIVITY_LOCKER_ROOM_LIFEGUARD_IDENTIFICATOR = "VigilanteVestuarios";
	public static final int ACTIVITY_LOCKER_ROOM_CAPACITY = 30;
	public static final int ACTIVITY_LOCKER_ROOM_ADULT_CAPACITY = 20;
	public static final int ACTIVITY_LOCKER_ROOM_CHILD_CAPACITY = 10;
	public static final int ACTIVITY_LOCKER_ROOM_MILISECONDS = 3000;
	public static final boolean ACTIVITY_LOCKER_ROOM_QUEUE_IS_FAIR = true;
	public static final String ACTIVITY_LOCKER_ROOM_AREA_ACTIVITY_ADULT_USERS = "-zonaActividadAdultos";
	
	public static final String ACTIVITY_MAIN_POOL_NAME = "ActividadPiscinaGrande";
	public static final String ACTIVITY_MAIN_POOL_LIFEGUARD_IDENTIFICATOR = "VigilantePiscinaGrande";
	public static final int ACTIVITY_MAIN_POOL_CAPACITY = 50;
	public static final int ACTIVITY_MAIN_POOL_MIN_MILISECONDS = 3000;
	public static final int ACTIVITY_MAIN_POOL_MAX_MILISECONDS = 5000;
	
	public static final String ACTIVITY_SLIDE_NAME = "ActividadTobogan";
	public static final String ACTIVITY_SLIDE_LIFEGUARD_A_IDENTIFICATOR = "VigilanteToboganA";
	public static final String ACTIVITY_SLIDE_LIFEGUARD_B_IDENTIFICATOR = "VigilanteToboganB";
	public static final String ACTIVITY_SLIDE_LIFEGUARD_C_IDENTIFICATOR = "VigilanteToboganC";
	public static final int ACTIVITY_SLIDE_CAPACITY = 1;
	public static final int ACTIVITY_SLIDE_MIN_MILISECONDS = 2000;
	public static final int ACTIVITY_SLIDE_MAX_MILISECONDS = 3000;
	public static final String ACTIVITY_SLIDE_B_NAME = "ActividadToboganB";
	public static final String ACTIVITY_SLIDE_C_NAME = "ActividadToboganC";
	public static final String ACTIVITY_AREA_ACTIVITY_B = "-zonaActividadB";
	public static final String ACTIVITY_AREA_ACTIVITY_C = "-zonaActividadC";
	
	
	public static final String ACTIVITY_WAVE_POOL_NAME = "ActividadPiscinaOlas";
	public static final String ACTIVITY_WAVE_POOL_LIFEGUARD_IDENTIFICATOR = "VigilantePisinaOlas";
	public static final int ACTIVITY_WAVE_POOL_CAPACITY = 20;
	public static final int ACTIVITY_WAVE_POOL_MIN_MILISECONDS = 2000;
	public static final int ACTIVITY_WAVE_POOL_MAX_MILISECONDS = 5000;
	public static final int ACTIVITY_WAVE_POOL_ENTRANCE_USERS = 2;
	

}
