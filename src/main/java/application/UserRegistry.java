package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import server.ui.UserControlJFrame;
import application.user.User;
import application.user.YoungUser;
import application.user.ChildUser;

public class UserRegistry {

    private Map<String, User> users = new HashMap<>();
    private Map<String, String> lifeguardsInAreas = new ConcurrentHashMap<>();
    private Map<String, Integer> usersInAreas = new ConcurrentHashMap<>();
    private Map<String, Integer> totalUsersInAreas = new ConcurrentHashMap<>();
    private Map<String, ArrayList<String>> usersIdsInAreas = new ConcurrentHashMap<>();
    private int adultUsersCount = 0;
    private int underAgeUsersCount = 0;
    private UserControlJFrame userControl;
    private StopResume stopResume;

    public UserRegistry(UserControlJFrame userControl, StopResume stopResume) {
        this.userControl = userControl;
        this.stopResume = stopResume;
    }

    public void waitIfProgramIsStopped() {
        try {
            getStopResume().stopResume();
        } catch (InterruptedException e) {

        }
    }

    public void registerActivity(String activityId) {
        this.usersInAreas.put(activityId, 0);
        this.totalUsersInAreas.put(activityId, 0);
    }

    public void registerActivityAreas(String activityId, List<String> areasIds) {
        for (String areaId : areasIds) {
            this.usersIdsInAreas.put(activityId + areaId, new ArrayList<String>());
        }
    }

    public List<String> getUserIdsInActivity(String activityId, String areaId) {
        return this.usersIdsInAreas.get(activityId + areaId);
    }

    public void registerLifeguard(String activityId, String areaId, String lifeguardId) {
        lifeguardsInAreas.put(activityId, lifeguardId);
        userControl.setData(activityId + areaId, lifeguardId);
    }

    public synchronized void registerUserInActivity(String activityId, String areaId, String userId) {
        int countNow = this.usersInAreas.get(activityId) + 1;
        this.usersInAreas.put(activityId, countNow);
        int countTotal = this.totalUsersInAreas.get(activityId) + 1;
        this.totalUsersInAreas.put(activityId, countTotal);
        ArrayList<String> usersInActivity = this.usersIdsInAreas.get(activityId + areaId);
        usersInActivity.add(userId);
        usersInActivity.remove(null);
        this.usersIdsInAreas.put(activityId + areaId, usersInActivity);
        userControl.setData(activityId + areaId, usersInActivity.toString());
    }

    public synchronized void unregisterUserFromActivity(String identificatorActividad, String identificatorArea, String identificatorUsuario) {
        int countNow = this.usersInAreas.get(identificatorActividad) - 1;
        this.usersInAreas.put(identificatorActividad, countNow);
        ArrayList<String> usersInActivity = this.usersIdsInAreas.get(identificatorActividad + identificatorArea);
        usersInActivity.remove(identificatorUsuario);
        this.usersIdsInAreas.put(identificatorActividad + identificatorArea, usersInActivity);
        userControl.setData(identificatorActividad + identificatorArea, usersInActivity.toString());
    }

    public User searchUser(String userId) {
        User user = null;
        if (this.users.containsKey(userId)) {
            user = this.users.get(userId);
        }
        return user;
    }

    public void addUser(User user) {
        this.users.put(user.getIdentificator(), user);
        if (user instanceof ChildUser || user instanceof YoungUser) {
            underAgeUsersCount++;
        } else {
            adultUsersCount++;
        }
    }

    public void removeUser(User user) {
        this.users.put(user.getIdentificator(), user);
        if (user instanceof ChildUser || user instanceof YoungUser) {
            underAgeUsersCount--;
        } else {
            adultUsersCount--;
        }
    }

    public int getAdultsCount() {
        return adultUsersCount;
    }

    public int getUnderAgeCount() {
        return underAgeUsersCount;
    }

    public int getUsersInActivity(String activityId) {
        return this.usersInAreas.get(activityId);
    }

    public int getUsersInActivityTotal(String activityId) {
        return this.totalUsersInAreas.get(activityId);
    }

    public StopResume getStopResume() {
        return stopResume;
    }

}
