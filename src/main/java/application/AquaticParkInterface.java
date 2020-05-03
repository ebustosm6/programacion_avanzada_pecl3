package application;

import java.util.List;

import application.activity.BaseActivity;
import application.user.User;

public interface AquaticParkInterface {
	
	public List<BaseActivity> selectActivities(int n);
	public boolean goIn(User user);
	public void goOut(User user);
}
