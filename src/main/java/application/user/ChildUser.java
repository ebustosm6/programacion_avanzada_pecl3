package application.user;

import application.AquaticPark;
import application.activity.BaseActivity;
import application.config.ApplicationGlobalConfig;

public class ChildUser extends User {
	
	public ChildUser(String identificator, int age, SupervisorUser supervisor, AquaticPark park) {
		super(identificator, age, supervisor, park);
	}
	
	@Override
	protected void onEachActivity(BaseActivity activity) throws InterruptedException {
		System.out.println(toString() + " - goes into " + activity.toString());
		boolean isInsideActivity = activity.goIn(this);				
		if (isInsideActivity) {
			activity.doActivity(this);
			activity.goOut(this);
			addActivity();
			System.out.println(toString() + " - goes out " + activity.toString());
		}
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
				
				for (BaseActivity activity: getActivities()) {
					onEachActivity(activity);
				}
				
				System.out.println(toString() + " - goes out " + ApplicationGlobalConfig.PARK_IDENTIFICATOR);
				getPark().goOut(this);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
