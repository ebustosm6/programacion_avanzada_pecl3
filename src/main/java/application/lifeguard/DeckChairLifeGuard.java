package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.enums.Permission;
import application.user.User;

public class DeckChairLifeGuard extends LifeGuard {
	
	public DeckChairLifeGuard(String id, ArrayBlockingQueue<User> colaEspera, UserRegistry userRegistry) {
		super(id, colaEspera, userRegistry);
	}
	
	public long getTiempoVigilancia() {
        return (long) ((int) (500) + (400 * Math.random()));
    }
	
	public Permission tipoPermiso(User visitante) {
    	Permission tipoPermiso = Permission.NOT_ALLOWED;
    	if (visitante.getAge() >= 15) {
    		tipoPermiso = Permission.ALLOWED;
    	}
        return tipoPermiso;
    }

}
