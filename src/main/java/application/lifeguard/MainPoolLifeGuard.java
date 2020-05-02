package application.lifeguard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import application.UserRegistry;
import application.enums.Permission;
import application.user.ChildUser;
import application.user.SupervisorUser;
import application.user.User;

public class MainPoolLifeGuard extends LifeGuard {
        
	private BlockingQueue<User> zonaActividad;

    public MainPoolLifeGuard(String id, ArrayBlockingQueue<User> colaEspera, ArrayBlockingQueue<User> zonaActividad, UserRegistry userRegistry) {
        super(id, colaEspera, userRegistry);
        this.zonaActividad = zonaActividad;
    }

    public long getTiempoVigilancia() {
        return 500;
    }

    public long getTiempoExpulsion() {
        return (long) ((int) (500) + (500 * Math.random()));
    }

    public Permission tipoPermiso(User visitante) {
        Permission tipoPermiso = Permission.ALLOWED;
        if (visitante instanceof ChildUser) {
            tipoPermiso = Permission.SUPERVISED;
        }
        return tipoPermiso;
    }

    private User randomVisitante() {
        User visitante = null;
        int n = (int) (getZonaActividad().size() * Math.random());
        Iterator<User> iterator = getZonaActividad().iterator();
        List<User> visitantes = new ArrayList<>();
        iterator.forEachRemaining(visitantes::add);
        visitante = visitantes.get(n);
        if (visitante instanceof SupervisorUser) {
            visitante = visitantes.get(n - 1);
        }
        return visitante;
    }

    public void run() {
        User visitanteParaExpulsar;
        int count;
        while (true) {
            try {
            	count = 0;
                for (User visitante : getColaEspera()) { 
                    getRegistro().waitIfProgramIsStopped();
                    sleep(getTiempoVigilancia());
                    Permission permiso = tipoPermiso(visitante);
                    visitante.setPermisoActividad(permiso);
                    if (getZonaActividad().remainingCapacity() == 0) {
                        visitanteParaExpulsar = randomVisitante();
                        visitanteParaExpulsar.interrupt();
                        sleep(getTiempoExpulsion());
                        System.out.println("Vigilante " + getIdentificator() + " echando al visitante " + visitanteParaExpulsar.getIdentificator() + " con edad " + visitanteParaExpulsar.getAge());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public BlockingQueue<User> getZonaActividad() {
        return zonaActividad;
    }

    public void setZonaActividad(BlockingQueue<User> zonaActividad) {
        this.zonaActividad = zonaActividad;
    }

}
