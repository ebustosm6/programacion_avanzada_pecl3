package client.controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import application.config.ApplicationGlobalConfig;
import common.config.NetworkConfig;
import server.controller.ServerControllerRMI;


public class ClientController {
        
	ServerControllerRMI controllerRmi;
	
	public ClientController() throws RemoteException, NotBoundException, MalformedURLException {
		
		this.controllerRmi = (ServerControllerRMI) Naming.lookup(NetworkConfig.CLIENT_CONTROLLER_URI);
	}
	
	public ArrayList<String> locationCheck(String userId) throws RemoteException {
		// returns userId, location, activitiesCount
		return (ArrayList<String>) controllerRmi.lookForUserAsList(userId);
	}
	
	public int underAgeCheck() throws RemoteException {
		return controllerRmi.getChildCount();
	}
	
	public ArrayList<String> slidesCheck() throws RemoteException {
		// array returned in the following order
		ArrayList<String> resultado = new ArrayList<>();
		resultado.add(controllerRmi.getUserIdsInArea(ApplicationGlobalConfig.ACTIVITY_SLIDE_NAME, ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY).toString());
		resultado.add(controllerRmi.getUserIdsInArea(ApplicationGlobalConfig.ACTIVITY_SLIDE_NAME, ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_B).toString());
		resultado.add(controllerRmi.getUserIdsInArea(ApplicationGlobalConfig.ACTIVITY_SLIDE_NAME, ApplicationGlobalConfig.ACTIVITY_AREA_ACTIVITY_C).toString());
		return resultado;
	}
	
	public ArrayList<String> countPeopleCheck() throws RemoteException {
		// array returned in the following order
		ArrayList<String> resultado = new ArrayList<>();
		resultado.add(String.valueOf(controllerRmi.getUserCountInArea(ApplicationGlobalConfig.ACTIVITY_LOCKER_ROOM_NAME)));
		resultado.add(String.valueOf(controllerRmi.getUserCountInArea(ApplicationGlobalConfig.ACTIVITY_WAVE_POOL_NAME)));
		resultado.add(String.valueOf(controllerRmi.getUserCountInArea(ApplicationGlobalConfig.ACTIVITY_CHILD_POOL_NAME)));
		resultado.add(String.valueOf(controllerRmi.getUserCountInArea(ApplicationGlobalConfig.ACTIVITY_DECK_CHAIR_NAME)));
		resultado.add(String.valueOf(controllerRmi.getUserCountInArea(ApplicationGlobalConfig.ACTIVITY_SLIDE_NAME)));
		resultado.add(String.valueOf(controllerRmi.getUserCountInArea(ApplicationGlobalConfig.ACTIVITY_MAIN_POOL_NAME)));
		return resultado;
	}

}
