package application.activity;


import application.user.ChildUser;

public interface ActivityInterface {

	public long getActivityTime();
	public boolean goIn(ChildUser user) throws InterruptedException;
	
}
