package servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import prueba1.Visitante;

public interface ControladorServidorRMI extends Remote {
	
	public List<String> buscarVisitanteLista(String identificator) throws RemoteException;
	
	public int getNumeroAdultos()throws RemoteException;

	public int getNumeroNinios()throws RemoteException;
	
	public int getVisitantesActualesEnZona(String identificator)throws RemoteException;
	
	public int getVisitantesAcumuladoEnZona(String identificator)throws RemoteException;
        
        public List<String> getIdentificadoresUsuariosEnActividad(String identificatorActividad,String identificatorArea)throws RemoteException;
       
}
