package prueba1;

public class VisitanteMenor extends Visitante {
	
	public VisitanteMenor(String identificador, int edad, ParqueAcuatico parque) {
		super(identificador, edad, null, parque);
	}
	
	public void run() {
		try {
			System.out.println("Entrando al parque: " + toString());
			boolean dentroParque = getParque().entrar(this);
			
			if (dentroParque) {
				int cantidadActividades = (int) (10 * Math.random() + 5);
				System.out.println("Escogiendo actividade del parque: " + cantidadActividades);
				setActividades(getParque().escogerActividades(cantidadActividades));
				
				for (Actividad actividad: getActividades()) {
					System.out.println("Entrando a la actividad "+getIdentificador()+": " + actividad.toString());
					boolean dentro = actividad.entrar(this);
					if (dentro) {
						actividad.disfrutar(this);
						actividad.salir(this);
						aniadirConteoActividades();
					}
				}
				
				System.out.println("Saliendo del parque: " + toString());
				getParque().salir(this);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
