package server.controller;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerControllerRMI extends Remote {
	
	public List<String> lookForUserAsList(String identificator) throws RemoteException;
	
	public int getAdultsCount()throws RemoteException;

	public int getChildCount()throws RemoteException;
	
	public int getUserCountInArea(String identificator)throws RemoteException;
	
	public int getUserTotalCountInArea(String identificator)throws RemoteException;
        
    public List<String> getUserIdsInArea(String identificatorActividad,String identificatorArea)throws RemoteException;
       
}
