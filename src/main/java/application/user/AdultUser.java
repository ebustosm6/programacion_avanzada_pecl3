package application.user;

import application.AquaticPark;
import application.activity.BaseActivity;

public class AdultUser extends User {
	
	public AdultUser(String identificator, int age, AquaticPark park) {
		super(identificator, age, null, park);
	}
	
	protected void onEachActivity(BaseActivity activity) throws InterruptedException {
		System.out.println(toString() + " - goes into " + activity.toString());
		boolean isInsideActivity = activity.goIn(this);				
		if (isInsideActivity) {
			activity.doActivity(this);
			activity.goOut(this);
			addActivity();
			System.out.println(toString() + " - goes out " + activity.toString());
		}
	}
	
//	public void run() {
//		try {
//			System.out.println("Entrando al parque: " + toString());
//			boolean dentroParque = getPark().goIn(this);
//			
//			if (dentroParque) {
//				int cantidadActividades = getRandomActivities();
//				System.out.println("Escogiendo actividade del parque: " + cantidadActividades);
//				setActividades(getPark().selectActivities(cantidadActividades));
//				
//				for (BaseActivity actividad: getActivities()) {
//					System.out.println("Entrando a la actividad "+getIdentificator()+": " + actividad.toString());
//					boolean dentro = actividad.goIn(this);
//					if (dentro) {
//						actividad.doActivity(this);
//						actividad.goOut(this);
//						addActivity();
//					}
//				}
//				
//				System.out.println("Saliendo del parque: " + toString());
//				getPark().goOut(this);
//			}
//			
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

}
