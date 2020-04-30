package client.controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import common.config.NetworkConfig;
import server.controller.ServerControllerRMI;


public class ClientController {
        
	ServerControllerRMI controlador;
	
	public ClientController() throws RemoteException, NotBoundException, MalformedURLException {
		
		this.controlador = (ServerControllerRMI) Naming.lookup(NetworkConfig.CLIENT_CONTROLLER_URI);
	}
	
	public ArrayList<String> controlUbicacion(String identificatorUsuario) throws RemoteException {
		// returns [id_usuario, ubicacion, actividades
                ArrayList<String> res = (ArrayList<String>) controlador.lookForUserAsList(identificatorUsuario);
		return (ArrayList<String>) res;
	}
	
	public int controlMenores() throws RemoteException {
		return controlador.getChildCount();
	}
	
	public ArrayList<String> controlToboganes() throws RemoteException {
		// returns [tobogan_a, tobogan_b, tobogan_c]
		ArrayList<String> resultado = new ArrayList<>();
		resultado.add(controlador.getUserIdsInArea("ActividadTobogan", "-zonaActividad").toString());
		resultado.add(controlador.getUserIdsInArea("ActividadTobogan", "-zonaActividadB").toString());
		resultado.add(controlador.getUserIdsInArea("ActividadTobogan", "-zonaActividadC").toString());
		return resultado;
	}
	
	public ArrayList<String> controlAforo() throws RemoteException {
		// returns [vestuarios, piscina_olas, piscina_ninios, tumbonas, toboganes, piscina_grande]
		ArrayList<String> resultado = new ArrayList<>();
		resultado.add(String.valueOf(controlador.getUserCountInArea("ActividadVestuario")));
		resultado.add(String.valueOf(controlador.getUserCountInArea("ActividadPiscinaOlas")));
		resultado.add(String.valueOf(controlador.getUserCountInArea("ActividadPiscinaNinos")));
		resultado.add(String.valueOf(controlador.getUserCountInArea("ActividadTumbonas")));
		resultado.add(String.valueOf(controlador.getUserCountInArea("ActividadTobogan")));
		resultado.add(String.valueOf(controlador.getUserCountInArea("ActividadPiscinaGrande")));
//                resultado.add("0");
//                resultado.add("0");
//                resultado.add("0");
		return resultado;
	}

}
