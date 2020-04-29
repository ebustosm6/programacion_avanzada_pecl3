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
    
    RegistroVisitantes registro;

    public ControladorServidor(RegistroVisitantes registro)throws RemoteException{
        this.registro = registro;
    }

    @Override
    public List<String> buscarVisitanteLista(String identificador) {
        List<String> resultado = new ArrayList<>();
        Visitante visitante = registro.buscarVisitante(identificador);
        if (visitante != null) {
            resultado.add(visitante.getIdentificador());
            resultado.add(visitante.getActividadActual());
            resultado.add(String.valueOf(visitante.getConteoActividades()));
        }
        
        return resultado;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumeroAdultos() {
        return registro.getNumeroAdultos();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumeroNinios() {
        return registro.getNumeroNinios();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVisitantesActualesEnZona(String identificador) {
        return registro.getVisitantesActualesEnZona(identificador);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVisitantesAcumuladoEnZona(String identificador) {
         return registro.getVisitantesAcumuladoEnZona(identificador);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<String> getIdentificadoresUsuariosEnActividad(String identificadorActividad, String identificadorArea){
        return registro.getIdentificadoresUsuariosEnActividad(identificadorActividad, identificadorArea);
    }
}
