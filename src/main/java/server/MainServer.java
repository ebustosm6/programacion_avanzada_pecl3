package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JOptionPane;

import application.AquaticPark;
import application.UserRegistry;
import application.user.UserGenerator;
import common.config.NetworkConfig;
import server.controller.ServerController;
import server.ui.UserControlJFrame;


public class MainServer {

    public static void main(String[] args) throws Exception {
        
        UserControlJFrame userControl = new UserControlJFrame();
        UserRegistry userRegistry = new UserRegistry(userControl);
        AquaticPark park = new AquaticPark(userRegistry);
        ServerController serverController = new ServerController(userRegistry);
        startServer(serverController);
        UserGenerator generadorVisitantes = new UserGenerator(park);
        generadorVisitantes.start();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 userControl.setVisible(true);
            }
        });
    }
        public static void startServer(ServerController controlador){
        try
        {
            ServerController obj = controlador; 
            Registry registry = LocateRegistry.createRegistry(NetworkConfig.PORT);
            Naming.rebind(NetworkConfig.SERVER_CONTROLLER_URI, obj);
            System.out.println("Started server");
        }catch (Exception e)
        {
            System.out.println(" Error: " + e.getMessage());
            e.printStackTrace();
        }
        }
}

