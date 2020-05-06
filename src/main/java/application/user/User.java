package application.user;

import java.util.List;

import application.AquaticPark;
import application.activity.BaseActivity;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.enums.SlideTicket;

public class User extends Thread {

    private String identificator;
    private int age;
    private User supervisor;
    private AquaticPark park;
    private String currentActivity = ApplicationGlobalConfig.PARK_OUTSIDE;
    private int totalActivitiesDone = 0;
    private List<BaseActivity> activities;
    private Permission activityPermissionType = Permission.NONE;
    private SlideTicket slideTicket;

    public User(String identificator, int age, User supervisor, AquaticPark park) {
        this.identificator = identificator;
        this.age = age;
        this.supervisor = supervisor;
        this.park = park;
        this.slideTicket = null;
    }

    protected int getRandomActivities() {
        return (int) ((ApplicationGlobalConfig.USER_MAX_NUM_ACTIVITIES - ApplicationGlobalConfig.USER_MIN_NUM_ACTIVITIES) * Math.random()
                + ApplicationGlobalConfig.USER_MIN_NUM_ACTIVITIES);
    }

    protected void onEachActivity(BaseActivity activity) throws InterruptedException {
        throw new AbstractMethodError();
    }

    @Override
    public void run() {
        try {
            System.out.println(toString() + " - goes into " + ApplicationGlobalConfig.PARK_IDENTIFICATOR);
            boolean isInsidePark = getPark().goIn(this);

            if (isInsidePark) {
                int activitiesCount = getRandomActivities();
                System.out.println(toString() + " - chosing activities - " + activitiesCount);
                setActividades(getPark().selectActivities(activitiesCount));

                for (BaseActivity activity : getActivities()) {
                    onEachActivity(activity);
                }

                System.out.println(toString() + " - goes out " + ApplicationGlobalConfig.PARK_IDENTIFICATOR);
                getPark().goOut(this);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public SlideTicket getSlideTicket() {
        return slideTicket;
    }

    public void setSlideTicket(SlideTicket value) {
        this.slideTicket = value;
    }

    @Override
    public String toString() {
        return this.identificator;
    }

    public String getIdentificator() {
        return identificator;
    }

    public int getAge() {
        return age;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String value) {
        this.currentActivity = value;
    }

    public int getTotalActivitiesDone() {
        return totalActivitiesDone;
    }

    public void addActivity() {
        this.totalActivitiesDone++;
    }

    public AquaticPark getPark() {
        return park;
    }

    public List<BaseActivity> getActivities() {
        return activities;
    }

    public void setActividades(List<BaseActivity> value) {
        this.activities = value;
    }

    public synchronized Permission getActivityPermissionType() {
        return activityPermissionType;
    }

    public synchronized void setActivityPermissionType(Permission value) {
        this.activityPermissionType = value;
    }

}
