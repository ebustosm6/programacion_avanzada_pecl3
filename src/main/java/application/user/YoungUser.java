package application.user;

import application.AquaticPark;
import application.activity.BaseActivity;

public class YoungUser extends User {

    public YoungUser(String identificator, int age, AquaticPark park) {
        super(identificator, age, null, park);
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

}
