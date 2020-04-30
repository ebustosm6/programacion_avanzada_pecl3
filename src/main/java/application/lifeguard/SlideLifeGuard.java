package application.lifeguard;

import java.util.concurrent.ArrayBlockingQueue;

import application.UserRegistry;
import application.enums.Permission;
import application.enums.SlideTicket;
import application.user.User;

public class SlideLifeGuard extends LifeGuard {

    public SlideLifeGuard(String id, ArrayBlockingQueue<User> colaEspera, UserRegistry userRegistry) {
        super(id, colaEspera, userRegistry);
    }

    public long getTiempoVigilancia() {
        return (long) ((int) (400) + (100 * Math.random()));
    }

    public Permission tipoPermiso(User visitante) {
        Permission tipoPermiso = Permission.NOT_ALLOWED;
        if (visitante.getAge() >= 11 && visitante.getAge() <= 14) {
            tipoPermiso = Permission.ALLOWED;
            visitante.setSlideTicket(SlideTicket.SLIDE_A);
        } else if (visitante.getAge() >= 15 && visitante.getAge() <= 17) {
            tipoPermiso = Permission.ALLOWED;
            visitante.setSlideTicket(SlideTicket.SLIDE_B);
        }else if(visitante.getAge() > 17){
            tipoPermiso = Permission.ALLOWED;
            visitante.setSlideTicket(SlideTicket.SLIDE_C);
        }
        return tipoPermiso;
    }

}
