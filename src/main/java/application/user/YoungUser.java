package application.user;

import application.AquaticPark;
import application.activity.Activity;

public class YoungUser extends User {
	
	public YoungUser(String identificator, int age, AquaticPark park) {
		super(identificator, age, null, park);
	}
	
	public void run() {
		try {
			System.out.println("Entrando al parque: " + toString());
			boolean dentroParque = getPark().entrar(this);
			
			if (dentroParque) {
				int cantidadActividades = getRandomActivities();
				System.out.println("Escogiendo actividade del parque: " + cantidadActividades);
				setActividades(getPark().escogerActividades(cantidadActividades));
				
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
				getPark().salir(this);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
