package application.user;

import application.AquaticPark;

public class SupervisorUser extends User {

    public SupervisorUser(String identificator, int age, AquaticPark park) {
        super(identificator, age, null, park);
    }

    public void run() {
        // do nothing, it is slave to her fatherhood
    }

}
