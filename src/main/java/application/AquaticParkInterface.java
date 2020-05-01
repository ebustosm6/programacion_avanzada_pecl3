package application;

import java.util.List;

import application.activity.Activity;
import application.user.User;

public interface AquaticParkInterface {
	
	public List<Activity> selectActivities(int n);
	public boolean goIn(User user);
	public void goOut(User user);
}
