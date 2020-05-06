package server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import application.UserRegistry;
import application.user.User;

public class ServerController extends UnicastRemoteObject implements ServerControllerRMI {

    private static final long serialVersionUID = 1L;
    UserRegistry userRegistry;

    public ServerController(UserRegistry userRegistry) throws RemoteException {
        this.userRegistry = userRegistry;
    }

    @Override
    public List<String> lookForUserAsList(String identificator) {
        List<String> result = new ArrayList<>();
        User visitante = userRegistry.searchUser(identificator);
        if (visitante != null) {
            result.add(visitante.getIdentificator());
            result.add(visitante.getCurrentActivity());
            result.add(String.valueOf(visitante.getTotalActivitiesDone()));
        }

        return result;
    }

    @Override
    public int getAdultsCount() {
        return userRegistry.getAdultsCount();
    }

    @Override
    public int getChildCount() {
        return userRegistry.getUnderAgeCount();
    }

    @Override
    public int getUserCountInArea(String identificator) {
        return userRegistry.getUsersInActivity(identificator);
    }

    @Override
    public int getUserTotalCountInArea(String identificator) {
        return userRegistry.getUsersInActivityTotal(identificator);
    }

    @Override
    public List<String> getUserIdsInArea(String identificatorActividad, String identificatorArea) {
        return userRegistry.getUserIdsInActivity(identificatorActividad, identificatorArea);
    }
}
