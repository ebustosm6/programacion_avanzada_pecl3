package application.activity;

import java.util.ArrayList;
import java.util.List;

import application.AquaticPark;
import application.user.AdultUser;
import application.user.ChildUser;
import application.user.UserGenerator;
import application.user.YoungUser;

public class SlideActivityTest {

    public static void main(String[] args) {
        AquaticPark parque = new AquaticPark();
        MainPoolActivity piscinaGrande = new MainPoolActivity(parque.getRegistry());
        List<Activity> actividades = new ArrayList<>();
        SlideActivity actividadTobogan = new SlideActivity(parque.getRegistry(), piscinaGrande);
        actividades.add(actividadTobogan);
        parque.setActivities(actividades);

        UserGenerator generadorVisitantes = new UserGenerator(parque);
        AdultUser adulto1 = generadorVisitantes.createAdultUser(1, 30);
        AdultUser adulto2 = generadorVisitantes.createAdultUser(2, 40);
        AdultUser adulto3 = generadorVisitantes.createAdultUser(3, 50);
        ChildUser ninio1 = generadorVisitantes.createChildUser(4, 3);
        ChildUser ninio2 = generadorVisitantes.createChildUser(6, 7);
        ChildUser ninio3 = generadorVisitantes.createChildUser(8, 10);
        YoungUser menor1 = generadorVisitantes.crearYoungUser(10, 13);
        YoungUser menor2 = generadorVisitantes.crearYoungUser(11, 15);
        YoungUser menor3 = generadorVisitantes.crearYoungUser(12, 17);

        adulto1.start();
        adulto2.start();
        adulto3.start();
        ninio1.start();
        ninio2.start();
        ninio3.start();
        menor1.start();
        menor2.start();
        menor3.start();

    }

}
