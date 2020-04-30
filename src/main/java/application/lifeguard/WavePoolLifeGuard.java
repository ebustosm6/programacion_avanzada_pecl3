package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.enums.Permission;
import application.user.User;

public class WavePoolLifeGuard extends LifeGuard {
	
	public WavePoolLifeGuard(String id, ArrayBlockingQueue<User> colaEspera, UserRegistry userRegistry) {
		super(id, colaEspera, userRegistry);
	}
	
	public long getTiempoVigilancia() {
        return 1000;
    }
	
	public Permission tipoPermiso(User visitante) {
    	Permission tipoPermiso = Permission.NOT_ALLOWED;
    	if (visitante.getAge() >= 6 && visitante.getAge() <= 10) {
    		tipoPermiso = Permission.SUPERVISED;
    	} else if (visitante.getAge() > 10) {
    		tipoPermiso = Permission.ALLOWED;
    	}
        return tipoPermiso;
    }

}
