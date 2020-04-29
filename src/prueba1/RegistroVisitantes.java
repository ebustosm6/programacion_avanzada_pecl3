package prueba1;

import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RegistroVisitantes implements Serializable{

    private Map<String, Visitante> visitantes = new HashMap<>();
    private Map<String, String> monitoresEnZonaActual = new ConcurrentHashMap<>();
    private Map<String, Integer> usuariosEnZonaActual = new ConcurrentHashMap<>();
    private Map<String, Integer> usuariosEnZonaAcumulado = new ConcurrentHashMap<>();
    private Map<String, ArrayList<String>> usuariosEnZonaActualIds = new ConcurrentHashMap<>();
    private int numeroAdultos = 0;
    private int numeroNinios = 0;
    private ControlUsuariosJFrame controlUsuarios;


    public RegistroVisitantes(ControlUsuariosJFrame controlUsuarios) {
        this.controlUsuarios = controlUsuarios;
    }
    
    public void comprobarDetenerReanudar(){
        try {
            controlUsuarios.getL().lock();
            while (controlUsuarios.getDetenerReanudar()) {
                try {
                    controlUsuarios.getC().await(); // espera a que le manden una señal
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } finally {
            controlUsuarios.getL().unlock();
        } 
    }
    
    public Map<String, Integer> getUsuariosEnZonaActual() {
        return usuariosEnZonaActual;
    }

    public void setUsuariosEnZonaActual(Map<String, Integer> usuariosEnZonaActual) {
        this.usuariosEnZonaActual = usuariosEnZonaActual;
    }

    public Map<String, Integer> getUsuariosEnZonaAcumulado() {
        return usuariosEnZonaAcumulado;
    }

    public void setUsuariosEnZonaAcumulado(Map<String, Integer> usuariosEnZonaAcumulado) {
        this.usuariosEnZonaAcumulado = usuariosEnZonaAcumulado;
    }

    public ArrayList<String> getColaParque() {
        return colaParque;
    }

    public void setColaParque(ArrayList<String> colaParque) {
        this.colaParque = colaParque;
    }
    private ArrayList<String> colaParque = new ArrayList<String>();

    public void registrarZonaActividad(String identificadorActividad) {
        this.usuariosEnZonaActual.put(identificadorActividad, 0);
        this.usuariosEnZonaAcumulado.put(identificadorActividad, 0);

    }

    public void registrarZonasActividad(String identificadorActividad, List<String> identificadoresAreas) {
        for (String identificadorArea : identificadoresAreas) {
            this.usuariosEnZonaActualIds.put(identificadorActividad + identificadorArea, new ArrayList<String>());
        }

    }

    public List<String> getIdentificadoresUsuariosEnActividad(String identificadorActividad, String identificadorArea) {
        return this.usuariosEnZonaActualIds.get(identificadorActividad + identificadorArea);
    }

    public void aniadirMonitorEnZona(String identificadorActividad, String identificadorAreaMonitor, String identificadorMonitor) {
        monitoresEnZonaActual.put(identificadorActividad, identificadorMonitor);
        controlUsuarios.setDatos(identificadorActividad+identificadorAreaMonitor, identificadorMonitor);
    }

    public void eliminarMonitorDeZona(String identificadorActividad) {
        monitoresEnZonaActual.put(identificadorActividad, "");
    }

    public synchronized void aniadirVisitanteZonaActividad(String identificadorActividad, String identificadorArea, String identificadorUsuario) {
        int cantidadActual = this.usuariosEnZonaActual.get(identificadorActividad) + 1;
        this.usuariosEnZonaActual.put(identificadorActividad, cantidadActual);
        int cantidadAcumulado = this.usuariosEnZonaAcumulado.get(identificadorActividad) + 1;
        this.usuariosEnZonaAcumulado.put(identificadorActividad, cantidadAcumulado);
        ArrayList<String> usuariosEnZonaActualIdsArray = this.usuariosEnZonaActualIds.get(identificadorActividad + identificadorArea);
        if (identificadorActividad.equals("ActividadTobogan")) {
        	// fantasia
        	System.out.println("asdasd");
        }
        usuariosEnZonaActualIdsArray.add(identificadorUsuario);
        usuariosEnZonaActualIdsArray.remove(null);
        this.usuariosEnZonaActualIds.put(identificadorActividad + identificadorArea,usuariosEnZonaActualIdsArray);
        controlUsuarios.setDatos(identificadorActividad+identificadorArea,usuariosEnZonaActualIdsArray.toString());
    }

    public synchronized void eliminarVisitanteZonaActividad(String identificadorActividad, String identificadorArea, String identificadorUsuario) {
        int cantidadActual = this.usuariosEnZonaActual.get(identificadorActividad) - 1;
        this.usuariosEnZonaActual.put(identificadorActividad, cantidadActual);
        ArrayList<String> usuariosEnZonaActualIdsArray = this.usuariosEnZonaActualIds.get(identificadorActividad + identificadorArea);
        usuariosEnZonaActualIdsArray.remove(identificadorUsuario);
        this.usuariosEnZonaActualIds.put(identificadorActividad + identificadorArea,usuariosEnZonaActualIdsArray);
        controlUsuarios.setDatos(identificadorActividad+identificadorArea,usuariosEnZonaActualIdsArray.toString());
        
    
    
    }

    public Visitante buscarVisitante(String identificador) {
        Visitante visitanteEncontrado = null;
        if (this.visitantes.containsKey(identificador)) {
            visitanteEncontrado = this.visitantes.get(identificador);
        }
        return visitanteEncontrado;
    }
    
    public List<String> buscarVisitanteLista(String identificador) {
        ArrayList<String> resultado = new ArrayList<>();
        Visitante visitante = buscarVisitante(identificador);
        if (visitante != null) {
            resultado.add(visitante.getIdentificador());
        resultado.add(visitante.getActividadActual());
        resultado.add(String.valueOf(visitante.getConteoActividades()));
        }
        return resultado;
    }

    public Map<String, Visitante> getVisitantes() {
        return visitantes;
    }

    public void aniadirVisitante(Visitante visitante) {
        this.visitantes.put(visitante.getIdentificador(), visitante);
        if (visitante instanceof VisitanteNinio|| visitante instanceof VisitanteMenor) {
            numeroNinios++;
        } else {
            numeroAdultos++;
        }
    }
    
    public void eliminarVisitante(Visitante visitante) {
        this.visitantes.put(visitante.getIdentificador(), visitante);
        if (visitante instanceof VisitanteNinio || visitante instanceof VisitanteMenor) {
            numeroNinios--;
        } else {
            numeroAdultos--;
        }
    }

    public int getNumeroAdultos() {
        return numeroAdultos;
    }

    public int getNumeroNinios() {
        return numeroNinios;
    }

    public int getVisitantesActualesEnZona(String identificador) {
        return this.usuariosEnZonaActual.get(identificador);
    }

    public int getVisitantesAcumuladoEnZona(String identificador) {
        return this.usuariosEnZonaAcumulado.get(identificador);
    }

}
