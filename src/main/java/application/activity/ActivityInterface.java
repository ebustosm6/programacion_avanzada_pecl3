package application.activity;


import application.user.AdultUser;
import application.user.ChildUser;
import application.user.User;
import application.user.YoungUser;

public interface ActivityInterface {

	public boolean goIn(ChildUser user) throws InterruptedException;
	public boolean goIn(AdultUser user) throws InterruptedException;
	public boolean goIn(YoungUser user) throws InterruptedException;
	public void doActivity(User user);
	public void goOut(User user);
	
}
