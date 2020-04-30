package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.enums.Permission;
import application.user.User;

/**
 *
 * @author vic88
 */
public class ChildPoolLifeGuard extends LifeGuard {
    
    public ChildPoolLifeGuard(String id, ArrayBlockingQueue<User> colaEspera, UserRegistry userRegistry) {
		super(id, colaEspera, userRegistry);
	}
    
    public long getTiempoVigilancia() {
        return (long) ((int) (1000) + (500 * Math.random()));
    }
    
    public Permission tipoPermiso(User visitante) {
    	Permission tipoPermiso = Permission.NOT_ALLOWED;
    	if (visitante.getAge() >= 1 && visitante.getAge() <= 5) {
    		tipoPermiso = Permission.SUPERVISED;
    	} else if (visitante.getAge() >= 6 && visitante.getAge() <= 10) {
    		tipoPermiso = Permission.ALLOWED;
    	}
        return tipoPermiso;
    }
    
}
