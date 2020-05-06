package application.config;

public class ApplicationGlobalConfig {

    // park
    public static final String PARK_IDENTIFICATOR = "Park";
    public static final String PARK_OUTSIDE = "Outside";

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
    public static final String ACTIVITY_AREA_WAITING_LINE = "WaitingLine";
    public static final String ACTIVITY_AREA_ACTIVITY = "-ActivityArea";
    public static final String ACTIVITY_AREA_WAITING_AREA_SUPERVISORS = "-WaitingAreaSupervisors";
    public static final String ACTIVITY_AREA_LIFEGUARD = "-Guard";
    public static final int ACTIVITY_DEFAULT_CAPACITY = 20;
    public static final int ACTIVITY_MIN_MILISECONDS = 100;
    public static final int ACTIVITY_MAX_MILISECONDS = 1000;

    public static final String ACTIVITY_CHILD_POOL_NAME = "ChildPoolActivity";
    public static final int ACTIVITY_CHILD_POOL_CAPACITY = 15;
    public static final int ACTIVITY_CHILD_POOL_MIN_MILISECONDS = 1000;
    public static final int ACTIVITY_CHILD_POOL_MAX_MILISECONDS = 3000;

    public static final String ACTIVITY_DECK_CHAIR_NAME = "DeskChairActivity";
    public static final int ACTIVITY_DECK_CHAIR_CAPACITY = 20;
    public static final int ACTIVITY_DECK_CHAIR_MIN_MILISECONDS = 2000;
    public static final int ACTIVITY_DECK_CHAIR_MAX_MILISECONDS = 4000;
    public static final boolean ACTIVITY_DECK_CHAIR_QUEUE_IS_FAIR = false;

    public static final String ACTIVITY_LOCKER_ROOM_NAME = "LockerRoomActivity";
    public static final int ACTIVITY_LOCKER_ROOM_CAPACITY = 30;
    public static final int ACTIVITY_LOCKER_ROOM_ADULT_CAPACITY = 20;
    public static final int ACTIVITY_LOCKER_ROOM_CHILD_CAPACITY = 10;
    public static final int ACTIVITY_LOCKER_ROOM_MILISECONDS = 3000;
    public static final boolean ACTIVITY_LOCKER_ROOM_QUEUE_IS_FAIR = true;
    public static final String ACTIVITY_LOCKER_ROOM_AREA_ACTIVITY_ADULT_USERS = "-ActivityAreaAdults";

    public static final String ACTIVITY_MAIN_POOL_NAME = "MainPoolActivity";
    public static final int ACTIVITY_MAIN_POOL_CAPACITY = 50;
    public static final int ACTIVITY_MAIN_POOL_MIN_MILISECONDS = 3000;
    public static final int ACTIVITY_MAIN_POOL_MAX_MILISECONDS = 5000;

    public static final String ACTIVITY_SLIDE_NAME = "SlideActivity";
    public static final int ACTIVITY_SLIDE_CAPACITY = 1;
    public static final int ACTIVITY_SLIDE_MIN_MILISECONDS = 2000;
    public static final int ACTIVITY_SLIDE_MAX_MILISECONDS = 3000;
    public static final String ACTIVITY_SLIDE_A_NAME = "SlideActivityA";
    public static final String ACTIVITY_SLIDE_B_NAME = "SlideActivityB";
    public static final String ACTIVITY_SLIDE_C_NAME = "SlideActivityC";
    public static final String ACTIVITY_AREA_ACTIVITY_B = "-ActivityAreaB";
    public static final String ACTIVITY_AREA_ACTIVITY_C = "-ActivityAreaC";

    public static final String ACTIVITY_WAVE_POOL_NAME = "WavePoolActivity";
    public static final int ACTIVITY_WAVE_POOL_CAPACITY = 20;
    public static final int ACTIVITY_WAVE_POOL_MIN_MILISECONDS = 2000;
    public static final int ACTIVITY_WAVE_POOL_MAX_MILISECONDS = 5000;
    public static final int ACTIVITY_WAVE_POOL_ENTRANCE_USERS = 2;

    // lifeguards
    public static final String ACTIVITY_DEFAULT_LIFEGUARD_IDENTIFICATOR = "DefaultLifeguard";
    public static final int ACTIVITY_DEFAULT_LIFEGUARD_MIN_MILISECONDS = 400;
    public static final int ACTIVITY_DEFAULT_LIFEGUARD_MAX_MILISECONDS = 900;

    public static final String ACTIVITY_CHILD_POOL_LIFEGUARD_IDENTIFICATOR = "ChildPoolLifeguard";
    public static final int ACTIVITY_CHILD_POOL_LIFEGUARD_MIN_MILISECONDS = 500;
    public static final int ACTIVITY_CHILD_POOL_LIFEGUARD_MAX_MILISECONDS = 1500;

    public static final String ACTIVITY_DECK_CHAIR_LIFEGUARD_IDENTIFICATOR = "DeckChairLifeguard";
    public static final int ACTIVITY_DECK_CHAIR_LIFEGUARD_MIN_MILISECONDS = 500;
    public static final int ACTIVITY_DECK_CHAIR_LIFEGUARD_MAX_MILISECONDS = 900;

    public static final String ACTIVITY_LOCKER_ROOM_LIFEGUARD_IDENTIFICATOR = "LockerRoomLifeguard";
    public static final int ACTIVITY_LOCKER_ROOM_LIFEGUARD_MILISECONDS = 1000;

    public static final String ACTIVITY_MAIN_POOL_LIFEGUARD_IDENTIFICATOR = "MainPoolLifeguard";
    public static final int ACTIVITY_MAIN_POOL_LIFEGUARD_MILISECONDS = 500;
    public static final int ACTIVITY_MAIN_POOL_LIFEGUARD_EJECTION_MIN_MILISECONDS = 500;
    public static final int ACTIVITY_MAIN_POOL_LIFEGUARD_EJECTION_MAX_MILISECONDS = 1000;

    public static final String ACTIVITY_SLIDE_LIFEGUARD_A_IDENTIFICATOR = "SlideLifeguardA";
    public static final String ACTIVITY_SLIDE_LIFEGUARD_B_IDENTIFICATOR = "SlideLifeguardB";
    public static final String ACTIVITY_SLIDE_LIFEGUARD_C_IDENTIFICATOR = "SlideLifeguardC";
    public static final int ACTIVITY_SLIDE_LIFEGUARD_MIN_MILISECONDS = 400;
    public static final int ACTIVITY_SLIDE_LIFEGUARD_MAX_MILISECONDS = 500;

    public static final String ACTIVITY_WAVE_POOL_LIFEGUARD_IDENTIFICATOR = "WavePoolLifeguard";
    public static final int ACTIVITY_WAVE_POOL_LIFEGUARD_MILISECONDS = 1000;

}
