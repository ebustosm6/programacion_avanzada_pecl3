/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import prueba1.ControlUsuariosJFrame;
import prueba1.GeneradorVisitantes;
import prueba1.ParqueAcuatico;
import prueba1.RegistroVisitantes;


/**
 *
 * @author vic88
 */
public class MainServer {

    public static void main(String[] args) throws Exception {
        
        ControlUsuariosJFrame userControl = new ControlUsuariosJFrame();
        RegistroVisitantes userRegistry = new RegistroVisitantes(userControl);
        ParqueAcuatico parque = new ParqueAcuatico(userRegistry);
        ControladorServidor controlador = new ControladorServidor(userRegistry);
        iniciarServidor(controlador);
        GeneradorVisitantes generadorVisitantes = new GeneradorVisitantes(parque);
        generadorVisitantes.start();
        
//        try { //special exception handler for registry creation
//        	RegistroVisitantesRMI stub = (RegistroVisitantesRMI) UnicastRemoteObject.exportObject(userRegistry,0);
//            Registry reg;
//            try {
//            	reg = LocateRegistry.createRegistry(1099);
//                System.out.println("java RMI registry created.");
//
//            } catch(Exception e) {
//            	System.out.println("Using existing registry");
//            	reg = LocateRegistry.getRegistry();
//            }
//        	reg.rebind("ServerRMI", stub);
//
//        } catch (RemoteException e) {
//        	e.printStackTrace();
//        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 userControl.setVisible(true);
            }
        });
    }
        public static void iniciarServidor(ControladorServidor controlador){
        try
        {
            ControladorServidor obj = controlador; //Crea una instancia del objeto que implementa la interfaz, como objeto a registrar 
            Registry registry = LocateRegistry.createRegistry(1099); //Arranca rmiregistry local en el puerto 1099
            Naming.rebind("//localhost/ObjetoControlador",obj);   //rebind sólo funciona sobre una url del equipo local 
            System.out.println("El Objeto Controlador ha quedado registrado");
            JOptionPane.showMessageDialog(null, "El Objeto Controlador ha quedado registrado ", "Exito en la creacion", JOptionPane.INFORMATION_MESSAGE);
        }catch (Exception e)
        {
            System.out.println(" Error: " + e.getMessage());
            e.printStackTrace();
        }
        }
}

