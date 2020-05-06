package client;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import client.ui.ClientControlJFrame;

public class MainClient {

    public static void main(String args[]) throws RemoteException, MalformedURLException, NotBoundException {

        ClientControlJFrame clientUI = new ClientControlJFrame();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                clientUI.setVisible(true);
            }
        });
    }

}
