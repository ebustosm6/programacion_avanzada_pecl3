package cliente;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import prueba1.RegistroVisitantes;
import prueba1.Visitante;
import servidor.ControladorServidor;
import servidor.ControladorServidorRMI;


public class ControlCliente {
	
	
	
	
        ControladorServidorRMI controlador;
	
	public ControlCliente() throws RemoteException, NotBoundException, MalformedURLException {
		
		this.controlador =(ControladorServidorRMI) Naming.lookup("//127.0.0.1/ObjetoControlador");
	}
	
	public ArrayList<String> controlUbicacion(String identificadorUsuario) throws RemoteException {
		// returns [id_usuario, ubicacion, actividades
                ArrayList<String> res = (ArrayList<String>) controlador.buscarVisitanteLista(identificadorUsuario);
		return (ArrayList<String>) res;
	}
	
	public int controlMenores() throws RemoteException {
		return controlador.getNumeroNinios();
	}
	
	public ArrayList<String> controlToboganes() throws RemoteException {
		// returns [tobogan_a, tobogan_b, tobogan_c]
		ArrayList<String> resultado = new ArrayList<>();
		resultado.add(controlador.getIdentificadoresUsuariosEnActividad("ActividadTobogan", "-zonaActividad").toString());
		resultado.add(controlador.getIdentificadoresUsuariosEnActividad("ActividadTobogan", "-zonaActividadB").toString());
		resultado.add(controlador.getIdentificadoresUsuariosEnActividad("ActividadTobogan", "-zonaActividadC").toString());
		return resultado;
	}
	
	public ArrayList<String> controlAforo() throws RemoteException {
		// returns [vestuarios, piscina_olas, piscina_ninios, tumbonas, toboganes, piscina_grande]
		ArrayList<String> resultado = new ArrayList<>();
		resultado.add(String.valueOf(controlador.getVisitantesActualesEnZona("ActividadVestuario")));
		resultado.add(String.valueOf(controlador.getVisitantesActualesEnZona("ActividadPiscinaOlas")));
		resultado.add(String.valueOf(controlador.getVisitantesActualesEnZona("ActividadPiscinaNinos")));
		resultado.add(String.valueOf(controlador.getVisitantesActualesEnZona("ActividadTumbonas")));
		resultado.add(String.valueOf(controlador.getVisitantesActualesEnZona("ActividadTobogan")));
		resultado.add(String.valueOf(controlador.getVisitantesActualesEnZona("ActividadPiscinaGrande")));
//                resultado.add("0");
//                resultado.add("0");
//                resultado.add("0");
		return resultado;
	}

}
