package application.user;

import application.AquaticPark;
import application.activity.Activity;

public class AdultUser extends User {
	
	public AdultUser(String identificator, int age, AquaticPark park) {
		super(identificator, age, null, park);
	}
	
	public void run() {
		try {
			System.out.println("Entrando al parque: " + toString());
			boolean dentroParque = getPark().goIn(this);
			
			if (dentroParque) {
				int cantidadActividades = (int) (10 * Math.random() + 5);
				System.out.println("Escogiendo actividade del parque: " + cantidadActividades);
				setActividades(getPark().selectActivities(cantidadActividades));
				
				for (Activity actividad: getActivities()) {
					System.out.println("Entrando a la actividad "+getIdentificator()+": " + actividad.toString());
					boolean dentro = actividad.goIn(this);
					if (dentro) {
						actividad.doActivity(this);
						actividad.goOut(this);
						addActivity();
					}
				}
				
				System.out.println("Saliendo del parque: " + toString());
				getPark().goOut(this);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
