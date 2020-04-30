package application.user;

import application.AquaticPark;
import application.activity.Activity;

public class SupervisorUser extends User {
	
	public SupervisorUser(String identificator, int age, AquaticPark park) {
		super(identificator, age, null, park);
	}
	
	public void run() {
	}

}
