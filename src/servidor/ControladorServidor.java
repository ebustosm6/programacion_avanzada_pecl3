/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import prueba1.RegistroVisitantes;
import prueba1.Visitante;

/**
 *
 * @author vic88
 */
public class ControladorServidor extends UnicastRemoteObject implements ControladorServidorRMI {
    
    RegistroVisitantes userRegistry;

    public ControladorServidor(RegistroVisitantes userRegistry)throws RemoteException{
        this.userRegistry = userRegistry;
    }

    @Override
    public List<String> buscarVisitanteLista(String identificator) {
        List<String> resultado = new ArrayList<>();
        Visitante visitante = userRegistry.buscarVisitante(identificator);
        if (visitante != null) {
            resultado.add(visitante.getIdentificator());
            resultado.add(visitante.getActividadActual());
            resultado.add(String.valueOf(visitante.getConteoActividades()));
        }
        
        return resultado;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumeroAdultos() {
        return userRegistry.getNumeroAdultos();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumeroNinios() {
        return userRegistry.getNumeroNinios();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVisitantesActualesEnZona(String identificator) {
        return userRegistry.getVisitantesActualesEnZona(identificator);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVisitantesAcumuladoEnZona(String identificator) {
         return userRegistry.getVisitantesAcumuladoEnZona(identificator);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<String> getIdentificadoresUsuariosEnActividad(String identificatorActividad, String identificatorArea){
        return userRegistry.getIdentificadoresUsuariosEnActividad(identificatorActividad, identificatorArea);
    }
}
