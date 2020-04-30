package application.user;

import java.io.Serializable;
import java.util.List;

import application.AquaticPark;
import application.activity.Activity;
import application.config.ApplicationGlobalConfig;
import application.enums.Permission;
import application.enums.SlideTicket;

public class User extends Thread implements Serializable{

    private String identificator;
    private int age;
    private User supervisor;
    private AquaticPark park;
    private String currentActivity = "Exterior"; //Activities.OUTSIDE
    private int totalActivitiesDone = 0;
	private List<Activity> activities;
    private Permission activityPermissionType = Permission.NONE;
    private SlideTicket slideTicket;
            

    public User(String identificator, int age, User supervisor, AquaticPark park) {
        this.identificator = identificator;
        this.age = age;
        this.supervisor = supervisor;
        this.park = park;
        this.slideTicket = null;
    }
    
    public int getRandomActivities() {
    	return (int) (
    			(ApplicationGlobalConfig.USER_MAX_NUM_ACTIVITIES - ApplicationGlobalConfig.USER_MIN_NUM_ACTIVITIES) * Math.random() 
    			+ ApplicationGlobalConfig.USER_MIN_NUM_ACTIVITIES);
    }

    public SlideTicket getSlideTicket() {
        return slideTicket;
    }

    public void setSlideTicket(SlideTicket value) {
        this.slideTicket = value;
    }

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

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActividades(List<Activity> value) {
        this.activities = value;
    }
    
    public synchronized Permission getPermisoActividad() {
        return activityPermissionType;
    }

    public synchronized void setPermisoActividad(Permission value) {
        this.activityPermissionType = value;
    }

}
