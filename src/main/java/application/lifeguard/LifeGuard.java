package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import application.UserRegistry;
import application.enums.Permission;
import application.user.User;

public class LifeGuard extends Thread {

    private String identificator;
    private BlockingQueue<User> colaEspera;
    private UserRegistry userRegistry;

    public String getIdentificator() {
        return identificator;
    }

    public void setIdentificador(String identificator) {
        this.identificator = identificator;
    }

    public LifeGuard(String id, ArrayBlockingQueue<User> colaEspera, UserRegistry userRegistry) {
        this.identificator = id;
        this.colaEspera = colaEspera;
        this.userRegistry = userRegistry;
    }

    public Permission tipoPermiso(User visitante) {
        Permission tipoPermiso = Permission.NOT_ALLOWED;
        if (visitante.getAge() > 0) {
            tipoPermiso = Permission.ALLOWED;
        }
        return tipoPermiso;
    }

    public long getTiempoVigilancia() {
        return (long) ((int) (500) + (400 * Math.random()));
    }

    public UserRegistry getRegistro() {
        return userRegistry;
    }

    public void setRegistro(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public void run() {
        while (true) {
            try {
                for (User visitante : colaEspera) {
                    getRegistro().waitIfProgramIsStopped();
                    sleep(getTiempoVigilancia());
                    Permission permiso = tipoPermiso(visitante);
                    visitante.setPermisoActividad(permiso);
                    if (permiso == Permission.NOT_ALLOWED) {
                        System.out.println("Vigilante " + getIdentificator() + " echando al visitante " + visitante.getIdentificator() + " con edad " + visitante.getAge());
                        System.out.println("----------------------------------------------------------------------------------------------------------");
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public BlockingQueue<User> getColaEspera() {
        return colaEspera;
    }

    public void setColaEspera(BlockingQueue<User> colaEspera) {
        this.colaEspera = colaEspera;
    }

}
