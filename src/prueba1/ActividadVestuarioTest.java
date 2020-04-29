package prueba1;

import java.util.ArrayList;
import java.util.List;

public class ActividadVestuarioTest {
	
	public static void main(String[] args) {
		ParqueAcuatico parque = new ParqueAcuatico();
		List<Actividad> actividades = new ArrayList<>();
		actividades.add(new ActividadVestuario(parque.getRegistro()));
		parque.setActividades(actividades);
		
		GeneradorVisitantes generadorVisitantes = new GeneradorVisitantes(parque);
		VisitanteAdulto adulto1 = generadorVisitantes.crearAdulto(1, 30);
        VisitanteAdulto adulto2 = generadorVisitantes.crearAdulto(2, 40);
        VisitanteAdulto adulto3 = generadorVisitantes.crearAdulto(3, 50);
        VisitanteNinio ninio1 = generadorVisitantes.crearNinio(4, 3);
        VisitanteNinio ninio2 = generadorVisitantes.crearNinio(6, 7);
        VisitanteNinio ninio3 = generadorVisitantes.crearNinio(8, 10);
        VisitanteMenor menor1 = generadorVisitantes.crearMenor(10, 13);
        VisitanteMenor menor2 = generadorVisitantes.crearMenor(11, 15);
        VisitanteMenor menor3 = generadorVisitantes.crearMenor(12, 17);

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
