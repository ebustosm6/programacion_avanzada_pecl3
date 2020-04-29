package prueba1;

public class GeneradorVisitantes extends Thread {

    private static int NUM_VISITANTES = 5000;
    private ParqueAcuatico parque;
    private RegistroVisitantes registro;

    public GeneradorVisitantes(ParqueAcuatico parque) {
        this.parque = parque;
        this.registro = parque.getRegistro();
    }

    private boolean necesitaAcompanniante(int edad) {
        return edad <= 10;
    }

    private boolean esMenor(int edad) {
        return edad >= 1 && edad <= 17;
    }

    public VisitanteAcompaniante crearAcompannante(int idHijo, int contador) {
        int edad = (int) (32 * Math.random() + 18);
        String identificador = "ID" + contador + "-" + edad + "-" + idHijo;
        return new VisitanteAcompaniante(identificador, edad, parque);
    }

    public VisitanteAdulto crearAdulto(int contador, int edad) {
        String identificador = "ID" + contador + "-" + edad;
        return new VisitanteAdulto(identificador, edad, parque);
    }

    public VisitanteMenor crearMenor(int contador, int edad) {
        String identificador = "ID" + contador + "-" + edad;
        return new VisitanteMenor(identificador, edad, parque);
    }

    public VisitanteNinio crearNinio(int contador, int edad) {
        String identificador = "ID" + contador + "-" + edad + "-" + (contador + 1);
        VisitanteAcompaniante acompaniante = crearAcompannante(contador, contador + 1);
        return new VisitanteNinio(identificador, edad, acompaniante, parque);
    }

    public void run() {
        int contador = 1;

        try {
            while (contador < NUM_VISITANTES) {
                registro.comprobarDetenerReanudar();
                sleep((long) ((int) (500) + (400 * Math.random())));

                int edad = (int) (49 * Math.random() + 1);
                String identificador;
                Visitante visitante;
                VisitanteAcompaniante acompaniante = null;
                if (esMenor(edad)) {
                    if (necesitaAcompanniante(edad)) {
                        identificador = "ID" + contador + "-" + edad + "-" + (contador + 1);
                        acompaniante = crearAcompannante(contador, contador + 1);
                        contador++;
                        visitante = new VisitanteNinio(identificador, edad, acompaniante, parque);
                    } else {
                        identificador = "ID" + contador + "-" + edad;
                        visitante = new VisitanteMenor(identificador, edad, parque);
                    }

                } else {
                    identificador = "ID" + contador + "-" + edad;
                    visitante = new VisitanteAdulto(identificador, edad, parque);
                }
                registro.aniadirVisitante(visitante);
                System.out.println("Starting visitante: " + visitante.toString());
                visitante.start();
                if (acompaniante != null) {
                    registro.aniadirVisitante(acompaniante);
                    System.out.println("Starting acompaniante: " + acompaniante.toString());
                    acompaniante.start();

                }
                contador++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
