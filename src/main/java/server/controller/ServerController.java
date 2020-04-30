package server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import application.UserRegistry;
import application.user.User;



public class ServerController extends UnicastRemoteObject implements ServerControllerRMI {
    
    UserRegistry userRegistry;

    public ServerController(UserRegistry userRegistry)throws RemoteException{
        this.userRegistry = userRegistry;
    }

    @Override
    public List<String> lookForUserAsList(String identificator) {
        List<String> result = new ArrayList<>();
        User visitante = userRegistry.buscarVisitante(identificator);
        if (visitante != null) {
        	result.add(visitante.getIdentificator());
        	result.add(visitante.getCurrentActivity());
        	result.add(String.valueOf(visitante.getTotalActivitiesDone()));
        }
        
        return result;
    }

    @Override
    public int getAdultsCount() {
        return userRegistry.getNumeroAdultos();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getChildCount() {
        return userRegistry.getNumeroNinios();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getUserCountInArea(String identificator) {
        return userRegistry.getVisitantesActualesEnZona(identificator);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getUserTotalCountInArea(String identificator) {
         return userRegistry.getVisitantesAcumuladoEnZona(identificator);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<String> getUserIdsInArea(String identificatorActividad, String identificatorArea){
        return userRegistry.getIdentificadoresUsuariosEnActividad(identificatorActividad, identificatorArea);
    }
}
