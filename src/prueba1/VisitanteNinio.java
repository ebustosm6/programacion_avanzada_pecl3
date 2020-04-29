package prueba1;

public class VisitanteNinio extends Visitante {
	
	public VisitanteNinio(String identificador, int edad, VisitanteAcompaniante acompaniante, ParqueAcuatico parque) {
		super(identificador, edad, acompaniante, parque);
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
