package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import application.AquaticPark;
import application.StopResume;
import application.UserRegistry;
import application.user.UserGenerator;
import common.config.NetworkConfig;
import server.controller.ServerController;
import server.ui.UserControlJFrame;

public class MainServer {

    public static void main(String[] args) throws Exception {

        StopResume stopResume = new StopResume();
        UserControlJFrame userControlUI = new UserControlJFrame(stopResume);
        UserRegistry userRegistry = new UserRegistry(userControlUI, stopResume);
        AquaticPark park = new AquaticPark(userRegistry);
        ServerController serverController = new ServerController(userRegistry);
        startServer(serverController);
        UserGenerator generadorVisitantes = new UserGenerator(park);
        generadorVisitantes.start();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                userControlUI.setVisible(true);
            }
        });
    }

    public static void startServer(ServerController controller) {
        try {
            ServerController controllerObj = controller;
            Registry registry = LocateRegistry.createRegistry(NetworkConfig.PORT);
            Naming.rebind(NetworkConfig.SERVER_CONTROLLER_URI, controllerObj);
            System.out.println("Started server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
