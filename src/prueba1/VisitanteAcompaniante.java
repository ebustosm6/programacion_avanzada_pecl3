package prueba1;

public class VisitanteAcompaniante extends Visitante {
	
	public VisitanteAcompaniante(String identificador, int edad, ParqueAcuatico parque) {
		super(identificador, edad, null, parque);
	}
	
	public void run() {
		// no hace nada, los ninios se crian solos
	}

}
